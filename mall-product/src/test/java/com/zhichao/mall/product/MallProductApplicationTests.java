package com.zhichao.mall.product;

import com.zhichao.mall.product.dao.AttrGroupDao;
import com.zhichao.mall.product.entity.BrandEntity;
import com.zhichao.mall.product.service.BrandService;
import com.zhichao.mall.product.vo.SpuItemSaleAttrVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;


    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功");

    }

    @Test
    void text2() {
        ValueOperations<String, String> opt = redisTemplate.opsForValue();
        opt.set("hello", "world" + UUID.randomUUID().toString());
        String hello = opt.get("categoriesJSON");
        System.out.println(hello);
    }

    /**
     *
     */
    @Test
    void text3() {
        System.out.println(redissonClient);
    }

    @Test
    public void test4() {
        List<SpuItemSaleAttrVo> spuItemSaleAttrVos = attrGroupDao.selectSpuItemSaleAttr(5l, 225l);
        System.out.println(spuItemSaleAttrVos);
    }

}
