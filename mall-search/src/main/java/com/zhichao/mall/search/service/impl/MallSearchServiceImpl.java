package com.zhichao.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhichao.mall.search.config.ElasticsearchConfig;
import com.zhichao.mall.search.constant.EsConstant;
import com.zhichao.mall.search.service.MallSearchService;
import com.zhichao.mall.search.to.SkuInfoEsModel;
import com.zhichao.mall.search.vo.SearchParam;
import com.zhichao.mall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 商品检索实现类
 * @Author: zhichao
 * @Date: 2022/2/15 22:56
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 商品检索
     *
     * @param param 检索参数
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {

        SearchResult searchResult = new SearchResult();

        // 直接进入到检索画面，没有输入检索条件
        if (Objects.isNull(param) || param.isNull()) {
            return searchResult;
        }

        // 构建查询请求
        SearchRequest searchRequest = new SearchRequest();

        // 构建检索语句
        SearchSourceBuilder searchSourceBuilder = buildSearchSource(param);
        System.out.println(searchSourceBuilder.toString());

        try {
            // 执行查询
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

            // 属性封装
            SearchHits searchHits = searchResponse.getHits();
            // 检索命中记录
            searchResult = buildSearchResult(param, searchResponse, searchHits);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 构建检索语句
     *
     * @param param
     * @return
     */
    private SearchSourceBuilder buildSearchSource(SearchParam param) {
        // 执行检索
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 模糊匹配，过滤(属性，分类，库存，品牌，价格区间，禄存)，排序，分页，高亮，聚合分析。
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 1. 模糊匹配
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            /**
             *      "must": [
             *         {
             *           "match": {
             *             "skuTitle": "iPhone"
             *           }
             *         }
             *       ],
             */
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        // 2. 过滤(属性，分类，库存，品牌，价格区间，禄存)
        if (StringUtils.isNotEmpty(param.getCatalog3Id())) {
            // 过滤分类
            /**
             *      "filter": [
             *         {
             *           "term": {
             *             "catalogId": "225"
             *           }
             *         },
             */
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 过滤品牌id
        /**       "filter": [
         *          "terms": {
         *             "brandId": [
         *               "11",
         *               "12"
         *             ]
         *           }
         */
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 过滤库存
        /**
         *          "term": {
         *             "hasStock": 1
         *           }
         */
        if (param.getHasStock() != null && param.getHasStock() == 1) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock()));
        }
        // 过滤价格
        /**
         *          "range": {
         *             "skuPrice": {
         *               "gte": 0,
         *               "lte": 5501
         *             }
         *           }
         */
        if (StringUtils.isNotEmpty(param.getSkuPrice())) {
            String[] prices = param.getSkuPrice().split("_");
            if (prices.length > 0) {
                RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("skuPrice");
                if (prices.length >= 1 && StringUtils.isNotEmpty(prices[0])) {
                    rangeQueryBuilder.gte(prices[0]);
                }
                if (prices.length == 2) {
                    rangeQueryBuilder.lte(prices[1]);
                }
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }
        // 过滤属性
        /**
         *         "nested": {
         *             "path": "attrs",
         *             "query": {
         *               "bool": {
         *                 "must": [
         *                   {
         *                     "term": {
         *                       "attrs.attrId": {
         *                         "value": "11"
         *                       }
         *                     }
         *                   },
         *                   {
         *                     "term": {
         *                       "attrs.attrValue": {
         *                         "value": "OLED屏幕"
         *                       }
         *                     }
         *                   }
         *                 ]
         *               }
         *             }
         *           }
         */
        List<String> attrs = param.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            BoolQueryBuilder attrBoolQueryBuilder = new BoolQueryBuilder();
            attrs.stream().forEach(attr -> {
                // 分割数据 attrs=1_5寸:8寸&attrs=2_16G:8G
                String[] attrIdAndVal = StringUtils.split(attr, '_');
                String[] attrVal = attrIdAndVal[1].split(":");
                attrBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrIdAndVal[0]));
                attrBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrVal));
            });
            NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(nestedQueryBuilder);

        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 3. 排序
        /**
         *   "sort": [
         *     {
         *       "skuPrice": {
         *         "order": "desc"
         *       }
         *     }
         *   ]
         */
        if (StringUtils.isNotEmpty(param.getSort())) {
            // 分割数据 saleCount_asc
            String[] sortItemAndOrder = param.getSort().split("_");
            searchSourceBuilder.sort(sortItemAndOrder[0], SortOrder.fromString(sortItemAndOrder[1]));
        }

        // 4. 分页
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        // 5. 高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            searchSourceBuilder.highlighter(builder);
        }


        // 6. 聚合分析
        // 品牌id
        /**
         *  "aggs": {
         *     "brand_agg": {
         *       "terms": {
         *         "field": "brandId",
         *         "size": 2
         *       },
         *       "aggs": {
         *         "brand_name_agg": {
         *           "terms": {
         *             "field": "brandName",
         *             "size": 10
         *           }
         *         },
         *         "brand_img_agg": {
         *           "terms": {
         *             "field": "brandImg",
         *             "size": 10
         *           }
         *         }
         *       }
         *     },
         */
        TermsAggregationBuilder brandAgg = new TermsAggregationBuilder("brand_agg");
        brandAgg.field("brandId").size(2);
        // 同一品牌的品牌名称和和品牌图片的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(10));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10));
        searchSourceBuilder.aggregation(brandAgg);

        // 分类
        /**
         *    "catalog_agg": {
         *       "terms": {
         *         "field": "catalogId",
         *         "size": 10
         *       },
         *       "aggs": {
         *         "catalog_name_agg": {
         *           "terms": {
         *             "field": "catalogName",
         *             "size": 10
         *           }
         *         }
         *       }
         *     },
         */
        TermsAggregationBuilder catalogAgg = new TermsAggregationBuilder("catalog_agg");
        catalogAgg.field("catalogId").size(10);
        // 分类名称的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10));
        searchSourceBuilder.aggregation(catalogAgg);

        // 属性
        NestedAggregationBuilder nestedAttrAgg = new NestedAggregationBuilder("attr_agg", "attrs");
        // 属性id
        TermsAggregationBuilder attrIdAgg = new TermsAggregationBuilder("attr_id_agg");
        attrIdAgg.field("attrs.attrId").size(10);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //聚合分析出当前  attr_id对应的所有可能的属性值  attrValue
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nestedAttrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(nestedAttrAgg);
        return searchSourceBuilder;
    }

    /**
     * 封装检索结果
     *
     * @param param
     * @param searchResponse
     * @param searchHits
     * @return 封装后的结果
     */
    private SearchResult buildSearchResult(SearchParam param, SearchResponse searchResponse, SearchHits searchHits) {
        SearchResult searchResult = new SearchResult();

        SearchHit[] hitResults = searchHits.getHits();
        if (hitResults != null && hitResults.length > 0) {
            // 查询到的所有商品信息
            ArrayList<SkuInfoEsModel> products = new ArrayList<>();

            // 封装部分数据
            for (SearchHit hitResult : hitResults) {
                // 封装所有商品信息
                // 检索结果的json
                String sourceAsString = hitResult.getSourceAsString();
                SkuInfoEsModel skuInfoEsModel = JSON.parseObject(sourceAsString, SkuInfoEsModel.class);
                // 取得高亮关键字
                if (StringUtils.isNotEmpty(param.getKeyword())) {
                    Map<String, HighlightField> highlightFields = hitResult.getHighlightFields();
                    HighlightField highlightSkuTitle = highlightFields.get("skuTitle");
                    skuInfoEsModel.setSkuTitle(highlightSkuTitle.getFragments()[0].string());
                }
                products.add(skuInfoEsModel);
            }
            searchResult.setProducts(products);

            // 处理所有所有聚合信息
            Aggregations productAgg = searchResponse.getAggregations();
            // 分类聚合信息
            ParsedLongTerms catalogAggResult = productAgg.get("catalog_agg");
            ArrayList<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
            List<? extends Terms.Bucket> buckets = catalogAggResult.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                SearchResult.CatalogVo catalog = new SearchResult.CatalogVo();
                String keyAsString = bucket.getKeyAsString();
                catalog.setCatalogId(Long.parseLong(keyAsString));
                ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
                String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
                catalog.setCatalogName(catalogName);
                catalogVos.add(catalog);
            }
            searchResult.setCatalogs(catalogVos);

            // 属性信息
            ParsedNested attrAggResult = productAgg.get("attr_agg");
            ParsedLongTerms attrIdAggResult = attrAggResult.getAggregations().get("attr_id_agg");
            List<? extends Terms.Bucket> attrIdBuckets = attrIdAggResult.getBuckets();
            List<SearchResult.AttrVo> attrVos = new ArrayList<>();
            for (Terms.Bucket attrIdBucket : attrIdBuckets) {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                // 属性id
                Long attrId = (Long) attrIdBucket.getKey();
                // 属性名称
                ParsedStringTerms attrNameAgg = attrIdBucket.getAggregations().get("attr_name_agg");
                String attrName = (String) attrNameAgg.getBuckets().get(0).getKey();
                // 属性值(不唯一)
                ParsedStringTerms attrValueAgg = attrIdBucket.getAggregations().get("attr_value_agg");
                List<? extends Terms.Bucket> attrValueBuckets = attrValueAgg.getBuckets();
                List<String> attrValueList = new ArrayList<>();
                for (Terms.Bucket attrValueBucket : attrValueBuckets) {
                    attrValueList.add(attrValueBucket.getKeyAsString());
                }
                attrVo.setAttrId(attrId);
                attrVo.setAttrName(attrName);
                attrVo.setAttrValue(attrValueList);
                attrVos.add(attrVo);
            }
            searchResult.setAttrs(attrVos);

            // 品牌相关信息
            ParsedLongTerms brandAggResult = productAgg.get("brand_agg");
            List<? extends Terms.Bucket> brandAggBuckets = brandAggResult.getBuckets();
            List<SearchResult.BrandVo> brandVos = new ArrayList<>();
            for (Terms.Bucket brandAggBucket : brandAggBuckets) {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                Long brandId = (Long) brandAggBucket.getKey();
                // 品牌图片
                String brandImg = ((ParsedStringTerms) brandAggBucket.getAggregations().get("brand_img_agg"))
                        .getBuckets().get(0).getKeyAsString();
                // 品牌名称
                String brandName = ((ParsedStringTerms) brandAggBucket.getAggregations().get("brand_name_agg"))
                        .getBuckets().get(0).getKeyAsString();
                brandVo.setBrandId(brandId);
                brandVo.setBrandImg(brandImg);
                brandVo.setBrandName(brandName);
                brandVos.add(brandVo);
            }
            searchResult.setBrands(brandVos);

            // 封装分页信息
            Long total = searchHits.getTotalHits().value;
            Integer pageNum = param.getPageNum();
            int totalPages = (int) (total + EsConstant.PRODUCT_PAGE_SIZE - 1) / EsConstant.PRODUCT_PAGE_SIZE;
            searchResult.setTotal(total);
            searchResult.setPageNum(pageNum);
            searchResult.setTotalPages(totalPages);
            // 页面显示页码
            List<Integer> pageNavs = new ArrayList<>();
            for (int i = 0; i < totalPages; i++) {
                pageNavs.add(i + 1);
            }
            searchResult.setPageNavs(pageNavs);
        }
        return searchResult;
    }
}
