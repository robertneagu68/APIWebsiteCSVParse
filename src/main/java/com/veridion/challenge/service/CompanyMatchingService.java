package com.veridion.challenge.service;


import com.veridion.challenge.domain.CompanyMerged;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompanyMatchingService {

  private final RestHighLevelClient client;

  @Autowired
  public CompanyMatchingService(RestHighLevelClient client) {
    this.client = client;
  }

  /**
   * Determines the best matching company profile based on given search criteria and
   * on the "score", meaning how many fields were matched on that entity.
   * @param companies The list of CompanyMerged candidates for matching.
   * @param name The name of the company to match.
   * @param website The website of the company to match.
   * @param phoneNumber The phone number of the company to match.
   * @param facebookProfile The Facebook profile of the company to match.
   * @return The best matching CompanyMerged profile, or null if no match is found.
   */
  public CompanyMerged determineBestMatch(List<CompanyMerged> companies, String name, String website, String phoneNumber, String facebookProfile) {
    CompanyMerged bestMatch = null;
    int highestScore = -1;

    for (CompanyMerged company : companies) {
      int score = 0;

      if (name != null && !name.isEmpty() && (name.equalsIgnoreCase(company.getName())
        || name.equalsIgnoreCase(company.getCompany_legal_name())
        || name.equalsIgnoreCase(company.getCompany_commercial_name())
        || name.equalsIgnoreCase(company.getCompany_all_available_names()))) {
        score += 1;
      }
      if (website != null && !website.isEmpty() && website.equalsIgnoreCase(company.website)) {
        score += 1;
      }
      if (phoneNumber != null && !phoneNumber.isEmpty() && phoneNumber.equals(company.phoneNumber)) {
        score += 1;
      }
      if (facebookProfile != null && !facebookProfile.isEmpty() && facebookProfile.equals(company.facebookProfile)) {
        score += 1;
      }

      if (score > highestScore) {
        bestMatch = company;
        highestScore = score;
      }
    }
    if (Objects.nonNull(bestMatch)) {
      System.out.println("Highest Score for" + bestMatch.getName() + ":" + highestScore);
    }

    return bestMatch;
  }

  /**
   * Searches for company profiles that match given criteria.
   * @param name The company name to search for.
   * @param website The company website to search for.
   * @param phoneNumber The company phone number to search for.
   * @param facebookProfile The company Facebook profile to search for.
   * @return A list of CompanyMerged profiles matching the search criteria.
   */
  public List<CompanyMerged> matchCompanies(String name, String website, String phoneNumber, String facebookProfile) {
    try {
      SearchRequest searchRequest = new SearchRequest("company_profiles");
      BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

      if (name != null && !name.isEmpty()) {
        BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
        subQuery.should(QueryBuilders.matchQuery("name", name));
        subQuery.should(QueryBuilders.matchQuery("companyLegalName", name));
        subQuery.should(QueryBuilders.matchQuery("companyCommercialName", name));
        subQuery.should(QueryBuilders.matchQuery("companyAllAvailableNames", name));
        boolQueryBuilder.must(subQuery);
      }


      if (website != null && !website.isEmpty()) {
        boolQueryBuilder.must(QueryBuilders.matchQuery("website", website));
      }
      if (phoneNumber != null && !phoneNumber.isEmpty()) {
        boolQueryBuilder.must(QueryBuilders.matchQuery("phoneNumber", phoneNumber));
      }
      if (facebookProfile != null && !facebookProfile.isEmpty()) {
        boolQueryBuilder.must(QueryBuilders.matchQuery("facebookProfile", facebookProfile));
      }


      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.query(boolQueryBuilder);
      searchRequest.source(searchSourceBuilder);

      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      SearchHit[] searchHits = searchResponse.getHits().getHits();
      List<CompanyMerged> matchedCompanies = new ArrayList<>();

      for (SearchHit hit : searchHits) {
        CompanyMerged company = convertSearchHitToCompanyMerged(hit);
        matchedCompanies.add(company);
      }

      return matchedCompanies;
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  /**
   * Converts a SearchHit object to a CompanyMerged object.
   * @param hit The SearchHit object from Elasticsearch response.
   * @return A CompanyMerged object created from the SearchHit object.
   */
  private CompanyMerged convertSearchHitToCompanyMerged(SearchHit hit) {
    Map<String, Object> sourceAsMap = hit.getSourceAsMap();

    CompanyMerged company = new CompanyMerged();
    company.name = (String) sourceAsMap.getOrDefault("name", "");
    company.domain = (String) sourceAsMap.getOrDefault("domain", "");
    company.website = (String) sourceAsMap.getOrDefault("website", "");
    company.phoneNumber = (String) sourceAsMap.getOrDefault("phoneNumber", "");
    company.facebookProfile = (String) sourceAsMap.getOrDefault("facebookProfile", "");
    company.address = (String) sourceAsMap.getOrDefault("address", "");
    company.company_commercial_name = (String) sourceAsMap.getOrDefault("company_commercial_name", "");
    company.company_legal_name = (String) sourceAsMap.getOrDefault("company_legal_name", "");
    company.company_all_available_names = (String) sourceAsMap.getOrDefault("company_all_available_names", "");

    return company;
  }

}





