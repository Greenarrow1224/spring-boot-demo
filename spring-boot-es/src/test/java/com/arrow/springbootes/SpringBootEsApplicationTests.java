package com.arrow.springbootes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootTest(classes = SpringBootEsApplicationTests.class)
class SpringBootEsApplicationTests {


    /**
     * ElasticsearchRestTemple是ElasticsearchOperations的子类的子类
     * 在ES7.x以下的版本使用的是ElasticsearchTemple，7.x以上版本已弃用ElasticsearchTemple，使用ElasticsearchRestTemple替代
     */
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


    /**
     * ElasticsearchOperations查询
     * 对关键字查询(TermQueryBuilder)
     * boolean查询(BoolQueryBuilder )
     * 范围（日期）查询(RangeQueryBuilder)
     * 当一次查询中有多个查询条件时，建议使用boolean查询，将其他查询条件通过BoolQueryBuilder的must、should、mustNot控制。
     */

    @Test
    void contextLoads() {
    }

}
