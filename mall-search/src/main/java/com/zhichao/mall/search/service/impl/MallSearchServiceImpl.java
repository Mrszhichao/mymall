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
 * @Description: ?????????????????????
 * @Author: zhichao
 * @Date: 2022/2/15 22:56
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * ????????????
     *
     * @param param ????????????
     * @return
     */
    @Override
    public SearchResult search(SearchParam param) {

        SearchResult searchResult = new SearchResult();

        // ??????????????????????????????????????????????????????
        if (Objects.isNull(param) || param.isNull()) {
            return searchResult;
        }

        // ??????????????????
        SearchRequest searchRequest = new SearchRequest();

        // ??????????????????
        SearchSourceBuilder searchSourceBuilder = buildSearchSource(param);
        System.out.println(searchSourceBuilder.toString());

        try {
            // ????????????
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

            // ????????????
            SearchHits searchHits = searchResponse.getHits();
            // ??????????????????
            searchResult = buildSearchResult(param, searchResponse, searchHits);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * ??????????????????
     *
     * @param param
     * @return
     */
    private SearchSourceBuilder buildSearchSource(SearchParam param) {
        // ????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ?????????????????????(?????????????????????????????????????????????????????????)?????????????????????????????????????????????
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 1. ????????????
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

        // 2. ??????(?????????????????????????????????????????????????????????)
        if (StringUtils.isNotEmpty(param.getCatalog3Id())) {
            // ????????????
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
        // ????????????id
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
        // ????????????
        /**
         *          "term": {
         *             "hasStock": 1
         *           }
         */
        if (param.getHasStock() != null && param.getHasStock() == 1) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock()));
        }
        // ????????????
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
        // ????????????
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
         *                         "value": "OLED??????"
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
                // ???????????? attrs=1_5???:8???&attrs=2_16G:8G
                String[] attrIdAndVal = StringUtils.split(attr, '_');
                String[] attrVal = attrIdAndVal[1].split(":");
                attrBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrIdAndVal[0]));
                attrBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrVal));
            });
            NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(nestedQueryBuilder);

        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 3. ??????
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
            // ???????????? saleCount_asc
            String[] sortItemAndOrder = param.getSort().split("_");
            searchSourceBuilder.sort(sortItemAndOrder[0], SortOrder.fromString(sortItemAndOrder[1]));
        }

        // 4. ??????
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        // 5. ??????
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            searchSourceBuilder.highlighter(builder);
        }


        // 6. ????????????
        // ??????id
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
        // ?????????????????????????????????????????????????????????
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(10));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10));
        searchSourceBuilder.aggregation(brandAgg);

        // ??????
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
        // ????????????????????????
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10));
        searchSourceBuilder.aggregation(catalogAgg);

        // ??????
        NestedAggregationBuilder nestedAttrAgg = new NestedAggregationBuilder("attr_agg", "attrs");
        // ??????id
        TermsAggregationBuilder attrIdAgg = new TermsAggregationBuilder("attr_id_agg");
        attrIdAgg.field("attrs.attrId").size(10);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //?????????????????????  attr_id?????????????????????????????????  attrValue
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nestedAttrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(nestedAttrAgg);
        return searchSourceBuilder;
    }

    /**
     * ??????????????????
     *
     * @param param
     * @param searchResponse
     * @param searchHits
     * @return ??????????????????
     */
    private SearchResult buildSearchResult(SearchParam param, SearchResponse searchResponse, SearchHits searchHits) {
        SearchResult searchResult = new SearchResult();

        SearchHit[] hitResults = searchHits.getHits();
        if (hitResults != null && hitResults.length > 0) {
            // ??????????????????????????????
            ArrayList<SkuInfoEsModel> products = new ArrayList<>();

            // ??????????????????
            for (SearchHit hitResult : hitResults) {
                // ????????????????????????
                // ???????????????json
                String sourceAsString = hitResult.getSourceAsString();
                SkuInfoEsModel skuInfoEsModel = JSON.parseObject(sourceAsString, SkuInfoEsModel.class);
                // ?????????????????????
                if (StringUtils.isNotEmpty(param.getKeyword())) {
                    Map<String, HighlightField> highlightFields = hitResult.getHighlightFields();
                    HighlightField highlightSkuTitle = highlightFields.get("skuTitle");
                    skuInfoEsModel.setSkuTitle(highlightSkuTitle.getFragments()[0].string());
                }
                products.add(skuInfoEsModel);
            }
            searchResult.setProducts(products);

            // ??????????????????????????????
            Aggregations productAgg = searchResponse.getAggregations();
            // ??????????????????
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

            // ????????????
            ParsedNested attrAggResult = productAgg.get("attr_agg");
            ParsedLongTerms attrIdAggResult = attrAggResult.getAggregations().get("attr_id_agg");
            List<? extends Terms.Bucket> attrIdBuckets = attrIdAggResult.getBuckets();
            List<SearchResult.AttrVo> attrVos = new ArrayList<>();
            for (Terms.Bucket attrIdBucket : attrIdBuckets) {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                // ??????id
                Long attrId = (Long) attrIdBucket.getKey();
                // ????????????
                ParsedStringTerms attrNameAgg = attrIdBucket.getAggregations().get("attr_name_agg");
                String attrName = (String) attrNameAgg.getBuckets().get(0).getKey();
                // ?????????(?????????)
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

            // ??????????????????
            ParsedLongTerms brandAggResult = productAgg.get("brand_agg");
            List<? extends Terms.Bucket> brandAggBuckets = brandAggResult.getBuckets();
            List<SearchResult.BrandVo> brandVos = new ArrayList<>();
            for (Terms.Bucket brandAggBucket : brandAggBuckets) {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                Long brandId = (Long) brandAggBucket.getKey();
                // ????????????
                String brandImg = ((ParsedStringTerms) brandAggBucket.getAggregations().get("brand_img_agg"))
                        .getBuckets().get(0).getKeyAsString();
                // ????????????
                String brandName = ((ParsedStringTerms) brandAggBucket.getAggregations().get("brand_name_agg"))
                        .getBuckets().get(0).getKeyAsString();
                brandVo.setBrandId(brandId);
                brandVo.setBrandImg(brandImg);
                brandVo.setBrandName(brandName);
                brandVos.add(brandVo);
            }
            searchResult.setBrands(brandVos);

            // ??????????????????
            Long total = searchHits.getTotalHits().value;
            Integer pageNum = param.getPageNum();
            int totalPages = (int) (total + EsConstant.PRODUCT_PAGE_SIZE - 1) / EsConstant.PRODUCT_PAGE_SIZE;
            searchResult.setTotal(total);
            searchResult.setPageNum(pageNum);
            searchResult.setTotalPages(totalPages);
            // ??????????????????
            List<Integer> pageNavs = new ArrayList<>();
            for (int i = 0; i < totalPages; i++) {
                pageNavs.add(i + 1);
            }
            searchResult.setPageNavs(pageNavs);
        }
        return searchResult;
    }
}
