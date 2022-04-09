package com.zhichao.mall.search;

import com.zhichao.mall.search.config.ElasticsearchConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    void query() throws IOException {

        // 创建请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定检索条件 dsl
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);
        AvgAggregationBuilder ageBalanceAvg = AggregationBuilders.avg("ageBalanceAvg").field("balance");
        sourceBuilder.aggregation(ageBalanceAvg);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse.toString());

        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hitsHit : hitsHits) {
            String sourceAsString = hitsHit.getSourceAsString();
//         JSON.parseObject()
        }

        // 查询到聚合的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
    }


}
