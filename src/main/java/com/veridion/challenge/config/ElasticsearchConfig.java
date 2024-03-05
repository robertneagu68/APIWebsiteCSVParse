package com.veridion.challenge.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the Elasticsearch client.
 * Retrieves host and port configurations from application properties.
 */
@Configuration
public class ElasticsearchConfig {

  @Value("${elasticsearch.host:localhost}")
  public String elasticsearchHost;

  @Value("${elasticsearch.port:9200}")
  public int elasticsearchPort;

  /**
   * Configures and provides a singleton instance of RestHighLevelClient for Elasticsearch operations.
   * @return A configured instance of RestHighLevelClient.
   */
  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {
    return new RestHighLevelClient(
      RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http")));
  }
}

