package com.zhichao.mall.search;

import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @Description:测试类
 * @Author: zhichao
 * @Date: 2022/2/21 23:01
 */
@SpringBootTest
public class MethodTest {

    @Test
    public void test01() {
        String ss = "1";
        String[] s = ss.split("_");
        System.out.println(Arrays.toString(s));
    }

    @Test
    public void test02() {
        // SortOrder
        System.out.println(SortOrder.fromString("a"));
    }
}
