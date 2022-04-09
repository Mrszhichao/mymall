package com.zhichao.mall.search.vo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 商品检索相关参数类
 * @Author: zhichao
 * @Date: 2022/2/15 21:55
 */
@Data
public class SearchParam {

    /**
     * 全文匹配key
     */
    private String keyword;

    /**
     * 商品三级分类的id
     */
    private String catalog3Id;

    /**
     * 排序
     * 销量--saleCount_asc, saleCount_desc,
     * 价格--skuPrice_asc, skuPrice_desc,
     * 热度--hotScore_asc,hotScore_desc
     */
    private String sort;

    /**
     * 是否只显示有货
     * 0（无库存）1（有库存）
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     * 1_500/_500/500_
     */
    private String skuPrice;

    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * 属性
     * attrs=1_5寸:8寸&attrs=2_16G:8G
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 检索条件是不是空
     *
     * @return
     */
    public boolean isNull() {
        return StringUtils.isEmpty(keyword) &&
                StringUtils.isEmpty(catalog3Id) &&
                StringUtils.isEmpty(sort) &&
                StringUtils.isEmpty(skuPrice) &&
                Objects.isNull(hasStock) &&
                CollectionUtils.isEmpty(brandId) &&
                CollectionUtils.isEmpty(attrs);
    }

}
