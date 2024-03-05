package com.veridion.challenge.config;

import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Component for initializing Elasticsearch indices required by the application.
 */
@Component
public class ElasticsearchInitializer {

  private final RestHighLevelClient restHighLevelClient;

  /**
   * Constructs an ElasticsearchInitializer with a RestHighLevelClient.
   * @param client The Elasticsearch RestHighLevelClient.
   */
  public ElasticsearchInitializer(RestHighLevelClient client) {
    this.restHighLevelClient = client;
  }

  /**
   * Creates an index with predefined settings and mappings if it doesn't already exist.
   * @param indexName The name of the index to create.
   * @throws IOException If there is an issue communicating with Elasticsearch.
   */
  public void createIndex(String indexName) throws IOException {

    GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
    boolean indexExists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

    if (indexExists) {
      System.out.println("Index already exists, skipping creation.");
      return;
    }

    String settings = """
    {
      "settings": {
        "analysis": {
          "tokenizer": {
            "edge_ngram_tokenizer": {
              "type": "edge_ngram",
              "min_gram": 2,
              "max_gram": 10,
              "token_chars": ["letter", "digit"]
            }
          },
          "analyzer": {
            "edge_ngram_analyzer": {
              "type": "custom",
              "tokenizer": "edge_ngram_tokenizer",
              "filter": ["lowercase"]
            }
          }
        }
      },
      "mappings": {
        "properties": {
          "companyLegalName": {
            "type": "text",
            "analyzer": "edge_ngram_analyzer"
          },
          ...
        }
      }
    }
    """;

    CreateIndexRequest request = new CreateIndexRequest("company_profiles")
      .source(settings, XContentType.JSON);

    request.settings(Settings.builder()
      .put("index.number_of_shards", 3)
      .put("index.number_of_replicas", 2)
    );
    String mappings = """
            {
              "properties": {
                "name": { "type": "text" },
                "domain": { "type": "keyword" },
                "website": { "type": "keyword" },
                "phoneNumber": { "type": "keyword" },
                "facebookProfile": { "type": "keyword" },
                "address": { "type": "text" },
                "company_commercial_name": { "type": "text" },
                "company_legal_name": { "type": "text" },
                "company_all_available_names": { "type": "text" }
              }
            }""";
    request.mapping(mappings, XContentType.JSON);

    restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
  }
}
