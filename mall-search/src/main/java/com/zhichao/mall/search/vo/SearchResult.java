package com.zhichao.mall.search.vo;

import com.zhichao.mall.search.to.SkuInfoEsModel;
import lombok.Data;

import java.util.List;

/**
 * @Description: 全文检索结果
 * @Author: zhichao
 * @Date: 2022/2/15 22:17
 */
@Data
public class SearchResult {

    /**
     * 查询到的所有商品信息
     */
    private List<SkuInfoEsModel> products;

    // ------分页信息--------
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Integer totalPages;

    /**
     * 页面显示页码
     */
    private List<Integer> pageNavs;
    // ---------------------

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVo> brands;

    /**
     * 当前查询到的结果，所有涉及到的所有分
     */
    private List<CatalogVo> catalogs;

    /**
     * 当前查询到的结果，所有涉及到的所有属性
     */
    private List<AttrVo> attrs;


    /**
     * 品牌
     */
    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    /**
     * 分类
     */
    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    /**
     * 属性
     */
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
