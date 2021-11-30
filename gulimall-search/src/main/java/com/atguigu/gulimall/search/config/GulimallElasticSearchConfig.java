package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Author Dali
 * @Date 2021/11/29 20:27
 * @Version 1.0
 * @Description: 整合ES的步骤
 * 1、导入依赖：导入es的high-level-client
 * 2、编写配置：参照官网文档-https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started-initialization.html
 * 给容器中注入一个RestHighLevelClient
 * 3、参照API
 */
@SpringBootConfiguration
public class GulimallElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient() {

        RestClientBuilder builder = null;
        // 可以指定多个es
        builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));

        RestHighLevelClient client = new RestHighLevelClient(builder);

//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.56.10", 9200, "http")));
////                        new HttpHost("localhost", 9201, "http"))); 目前尚未使用集群，先用不了那么多

        return client;
    }
}
