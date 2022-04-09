package com.zhichao.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.CategoryDao;
import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.service.CategoryBrandRelationService;
import com.zhichao.mall.product.service.CategoryService;
import com.zhichao.mall.product.vo.CatalogLevel2Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 商品分类管理Service实现类
 * @Author: zhichao
 * @Date: 2021/6/10 00:35
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService relationService;

    /**
     * redis操作
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取商品分类和其对应的子分类
     *
     * @return 商品分类列表
     */
    @Override
    public List<CategoryEntity> listWithTree() {

        // 获取所有商品
        List<CategoryEntity> categoryList = baseMapper.selectList(null);

        // TODO 缺少异常处理

        // 处理一级分类
        final List<CategoryEntity> categoryTreeList = categoryList.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(parent -> {
            parent.setChildren(getChildren(parent, categoryList));
            return parent;
        }).collect(Collectors.toList());
        return categoryTreeList;
    }

    /**
     * 删除当前商品种别
     *
     * @param ids 商品id
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {

        // TODO 验证当前节点是否可以被删除

        // 删除节点
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 获取当前商品种别各个层级的id
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] getCateGoryPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        path.add(catelogId);
        getCateGoryPath(catelogId, path);
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 更新商品种别相关信息
     *
     * @param category
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRelationById(CategoryEntity category) {
        this.updateById(category);
        relationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 获取一级分类
     * <p>  @Cacheable
     * 默认
     * 1. 若果缓存命中，则不调用方法
     * 2. key是默认生成的，名字是 【缓存的名字::SimpleKey】 ，值是 序列化的值
     * 3. 默认的缓存时间是-1
     * </p>
     * <p>
     * 自定义
     * 1. 指定所需要的key
     * 2. 指定数据的存活时间，需要在配置文件中填写
     * 3. 指定存储数据为json格式
     *
     *
     *
     * </p>
     *
     * @return
     */
    @Cacheable(value = {"category"}, key = "'level1Categories'")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_level", "1");
        List<CategoryEntity> categories = this.list(queryWrapper);
        return categories;
    }

    /**
     * 获取或有二级分类和其下的三级分类
     *
     * @return
     */
    @Override
    public Map<String, List<CatalogLevel2Vo>> getCatalogJson() {

        /**
         * 存在三个问题
         *  1.缓存穿透
         *    解决方法：当检索不到的时候，设置一个空值
         *  2.缓存雪崩
         *    解决方法：随机设置key的过期时间
         *  3.缓存击穿
         *    加锁
         *
         */
        Map<String, List<CatalogLevel2Vo>> resultMap = new HashMap<>();

        // 查询缓存中的数据
        String categoriesJSON = redisTemplate.opsForValue().get("categoriesJSON");
        if (StringUtils.isEmpty(categoriesJSON)) {
            // 查询数据
            resultMap = getCategoriesMap();
            // 保存数据
            String cacheJSON = JSON.toJSONString(resultMap);
            redisTemplate.opsForValue().set("categoriesJSON", cacheJSON, 60, TimeUnit.MINUTES);
        } else {
            // 转换缓存中的数据
            TypeReference<Map<String, List<CatalogLevel2Vo>>> typeReference = new TypeReference<Map<String, List<CatalogLevel2Vo>>>() {
            };
            resultMap = JSON.parseObject(categoriesJSON, typeReference);
        }
        return resultMap;
    }

    private void saveCacheWithRedisLock() {
        // 查询锁
        String ownKey = UUID.randomUUID().toString();
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent("lock", ownKey, 300, TimeUnit.SECONDS);
        if (lockResult) {
            try {
                // 抢占锁成功
                // 查询数据
                Map<String, List<CatalogLevel2Vo>> resultMap = getCategoriesMap();
                // 保存数据
                String cacheJSON = JSON.toJSONString(resultMap);
                redisTemplate.opsForValue().set("categoriesJSON", cacheJSON);
            } finally {
                // 关闭锁
                // --------------------
                // 当正好在获得锁后，解锁前锁过期了，会存在误删其他人误删的锁
//            String lockKey = redisTemplate.opsForValue().get("lock");
//            if (ownKey.equals(lockKey)) {
//                redisTemplate.delete("lock");
//            }
                // 运用脚本删除锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), ownKey);
            }
        } else {
            // 抢占失败，重试
            saveCacheWithRedisLock();
        }
    }

    /**
     * 查询分类
     *
     * @return
     */
    private Map<String, List<CatalogLevel2Vo>> getCategoriesMap() {
        // 获得所有分类
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        List<CategoryEntity> allCategories = this.list(queryWrapper);

        // 获得所有一级分类
        List<CategoryEntity> categories1 = getLevel1Categories();

        // 获得所有二级分类
        Map<String, List<CategoryEntity>> categories2 = getCategories(allCategories, 2);

        // 获得所有三级分类
        Map<String, List<CategoryEntity>> categories3 = getCategories(allCategories, 3);

        // 处理结果
        Map<String, List<CatalogLevel2Vo>> resultMap = categories1.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
            String level2parentId = value.getCatId().toString();
            // 处理二级分类
            List<CategoryEntity> category2Entities = categories2.get(level2parentId);
            List<CatalogLevel2Vo> level2Vos = category2Entities.stream().map(item -> {
                String level3ParentId = item.getCatId().toString();
                // 处理三级分类
                List<CategoryEntity> category3Entities = categories3.get(level3ParentId);
                List<CatalogLevel2Vo.Catalog3Vo> catalog3VoList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(category3Entities)) {
                    catalog3VoList = category3Entities.stream().map(items3 -> {
                        CatalogLevel2Vo.Catalog3Vo catalog3Vo = new CatalogLevel2Vo.Catalog3Vo();
                        catalog3Vo.setCatalog2Id(level3ParentId);
                        catalog3Vo.setId(items3.getCatId().toString());
                        catalog3Vo.setName(items3.getName());
                        return catalog3Vo;
                    }).collect(Collectors.toList());
                }
                return new CatalogLevel2Vo(item.getParentCid().toString(), item.getCatId().toString(), item.getName(), catalog3VoList);
            }).collect(Collectors.toList());
            return level2Vos;
        }));
        //        // 生成key是二级分类id，value是其下的子分类
//        Map<Long, List<CatalogLevel2Vo.Catalog3Vo>> category2Map = new HashMap<>(categories2.size());
//        for (CategoryEntity categoryEntity : categories3) {
//            Long parentCid = categoryEntity.getParentCid();
//            List<CatalogLevel2Vo.Catalog3Vo> categoryEntities = category2Map.getOrDefault(parentCid, new ArrayList<>());
//            // 生成三级分类结构
//            CatalogLevel2Vo.Catalog3Vo catalog3Vo = new CatalogLevel2Vo.Catalog3Vo();
//            catalog3Vo.setCatalog2Id(parentCid.toString());
//            catalog3Vo.setId(categoryEntity.getCatId().toString());
//            catalog3Vo.setName(categoryEntity.getName());
//            categoryEntities.add(catalog3Vo);
//            // 添加数据
//            category2Map.put(parentCid, categoryEntities);
//        }
//
//        // 生成所需要的二级分类结构
//        for (CategoryEntity categoryEntity : categories2) {
//            CatalogLevel2Vo catalogLevel2Vo = new CatalogLevel2Vo();
//            catalogLevel2Vo.setCatalog1Id(categoryEntity.getParentCid().toString());
//            catalogLevel2Vo.setId(categoryEntity.getCatId().toString());
//            catalogLevel2Vo.setName(categoryEntity.getName());
//            catalogLevel2Vo.setCatalog3List(category2Map.get(categoryEntity.getCatId()));
//            resultList.add(catalogLevel2Vo);
//        }
//
//        // 生成最终的数据
//        Map<String, List<CatalogLevel2Vo>> resultMap = new HashMap<>(resultList.size());
//        for (CatalogLevel2Vo catalogLevel2Vo : resultList) {
//            List<CatalogLevel2Vo> voList = resultMap.getOrDefault(
//                    catalogLevel2Vo.getCatalog1Id(), new ArrayList<>());
//            voList.add(catalogLevel2Vo);
//            resultMap.put(catalogLevel2Vo.getCatalog1Id(), voList);
//        }
        return resultMap;
    }


    /**
     * 生成指定层级的分类
     *
     * @param categories
     * @param level
     * @return
     */
    private Map<String, List<CategoryEntity>> getCategories(List<CategoryEntity> categories, int level) {
        Map<String, List<CategoryEntity>> resultMap = new HashMap<>(categories.size());
        List<CategoryEntity> categoryEntityList = categories.stream().filter(entity ->
                level == entity.getCatLevel()).collect(Collectors.toList());

        for (CategoryEntity catalogLevelVo : categoryEntityList) {
            List<CategoryEntity> voList = resultMap.getOrDefault(
                    catalogLevelVo.getParentCid().toString(), new ArrayList<>());
            voList.add(catalogLevelVo);
            resultMap.put(catalogLevelVo.getParentCid().toString(), voList);
        }

        return resultMap;
    }

    private void getCateGoryPath(Long catelogId, List<Long> path) {
        // 获取当前节点的父节点信息
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            path.add(categoryEntity.getParentCid());
            getCateGoryPath(categoryEntity.getParentCid(), path);
        }
    }

    /**
     * 获取二级分类及以下分类的子类
     *
     * @param root            父类
     * @param allCategoryList 商品list
     * @return 处理后的商品列表
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> allCategoryList) {

        Integer catLevel = 3;
        if (catLevel.equals(root.getCatLevel())) {
            return new ArrayList<>();
        }
        List<CategoryEntity> children = allCategoryList.stream().filter(categoryEntity ->
                root.getCatId().equals(categoryEntity.getParentCid())
        ).map(parent -> {
            parent.setChildren(getChildren(parent, allCategoryList));
            return parent;
        }).collect(Collectors.toList());
        return children;
    }

}
