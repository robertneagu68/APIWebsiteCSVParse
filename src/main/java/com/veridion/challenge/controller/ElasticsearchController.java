package com.veridion.challenge.controller;

import com.veridion.challenge.config.ElasticsearchInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller for Elasticsearch-related operations.
 * Provides endpoints for managing Elasticsearch indices within the application.
 */

@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchController {

  private final ElasticsearchInitializer elasticsearchInitializer;

  /**
   * Constructs an instance of ElasticsearchController.
   * @param elasticsearchService The service responsible for initializing Elasticsearch indices.
   */

  public ElasticsearchController(ElasticsearchInitializer elasticsearchService) {
    this.elasticsearchInitializer = elasticsearchService;
  }

  /**
   * Creates the "company_profiles" index in Elasticsearch if it does not already exist.
   * This endpoint is designed to be called to ensure the necessary index structure is set up for storing company data.
   * @return A string message indicating the success or failure of the index creation process.
   */

  @GetMapping("/create-index")
  public String createIndex() {
    try {
      elasticsearchInitializer.createIndex("company_profiles");
      return "Index created successfully";
    } catch (IOException e) {
      e.printStackTrace();
      return "Failed to create index";
    }
  }
}

