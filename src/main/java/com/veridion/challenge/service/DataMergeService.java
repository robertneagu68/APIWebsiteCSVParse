package com.veridion.challenge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veridion.challenge.domain.Company;
import com.veridion.challenge.domain.CompanyCsv;
import com.veridion.challenge.domain.CompanyMerged;
import com.veridion.challenge.repository.CompanyMergedRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataMergeService {
  private final CompanyMergedRepository companyMergedRepository;
  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;

  @Autowired
  public DataMergeService(CompanyMergedRepository companyMergedRepository, RestHighLevelClient client, ObjectMapper objectMapper) {
    this.companyMergedRepository = companyMergedRepository;
    this.client = client;
    this.objectMapper = objectMapper;
  }

  /**
   * Merges scraped company data with data from a CSV file and indexes the merged data in Elasticsearch.
   * @param scrapedCompanies A list of Company objects scraped from websites.
   * @param csvCompanies A list of CompanyCsv objects parsed from a CSV file.
   * @return A list of CompanyMerged objects representing the merged data.
   */
  public List<CompanyMerged> mergeCompanyData(List<Company> scrapedCompanies, List<CompanyCsv> csvCompanies) {
    Map<String, CompanyCsv> csvCompanyMap = csvCompanies.stream()
      .collect(Collectors.toMap(CompanyCsv::getDomain, Function.identity()));

    List<CompanyMerged> companyMergedList = new ArrayList<CompanyMerged>();

    for (Company company : scrapedCompanies) {
      CompanyCsv csvCompany = csvCompanyMap.get(company.getWebsite());

      if (csvCompany != null) {
        CompanyMerged companyMerged = new CompanyMerged(company, csvCompany);
        companyMergedList.add(companyMerged);
      }
    }

    companyMergedRepository.saveAll(companyMergedList);

    for(CompanyMerged company : companyMergedList) {
      try {
        IndexRequest request = new IndexRequest("company_profiles")
          .id(company.getId().toString())
          .source(convertCompanyToJson(company));
        client.index(request, RequestOptions.DEFAULT);
      } catch (IOException e) {
        System.out.println("Failed to convert company to JSON");
      }
    }

    return companyMergedList;
  }

  /**
   * Converts a CompanyMerged object to a JSON string map for indexing in Elasticsearch.
   * @param company The CompanyMerged object to convert.
   * @return A map representing the JSON structure of the company data.
   * @throws IOException if there's an issue in processing the company data to JSON.
   */
  private Map<String, Object> convertCompanyToJson(CompanyMerged company) throws IOException {
    String json = objectMapper.writeValueAsString(company);
    return objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
  }

}

