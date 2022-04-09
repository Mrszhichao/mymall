package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.ProductAttrValueDao;
import com.zhichao.mall.product.entity.ProductAttrValueEntity;
import com.zhichao.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据spuId查询属性值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> baseAttrInfoForSpu(Long spuId) {
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        return this.list(wrapper);
    }

}
