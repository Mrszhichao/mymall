package com.zhichao.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhichao.mall.search.config.ElasticsearchConfig;
import com.zhichao.mall.search.constant.EsConstant;
import com.zhichao.mall.search.service.ProductSkuService;
import com.zhichao.mall.search.to.SkuInfoEsModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 商品相关操作逻辑实现曾
 * @Author: zhichao
 * @Date: 2021/11/8 22:57
 */
@Slf4j
@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 保存商品sku
     *
     * @param skuInfo
     * @return
     */
    @Override
    public void saveProductSpuInfo(List<SkuInfoEsModel> skuInfo) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        // 便利model封装数据
        for (SkuInfoEsModel skuInfoEsModel : skuInfo) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuInfoEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuInfoEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        // 批量保存
        BulkResponse bulkResult = client.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        boolean hasFailures = bulkResult.hasFailures();
        List<String> ids = Arrays.stream(bulkResult.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if (hasFailures) {
            // 有失败的情况
            log.error("部分商品的es保存失败，失败的sku id是：" + ids);
        }
    }
}
