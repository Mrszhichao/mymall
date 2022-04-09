package com.zhichao.mall.search.service;

import com.zhichao.mall.search.vo.SearchParam;
import com.zhichao.mall.search.vo.SearchResult;

/**
 * @Description: 商品检索
 * @Author: zhichao
 * @Date: 2022/2/15 22:55
 */
public interface MallSearchService {

    /**
     * 商品检索
     *
     * @param param 检索参数
     * @return
     */
    SearchResult search(SearchParam param);
}
