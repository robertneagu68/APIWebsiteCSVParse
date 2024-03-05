package com.veridion.challenge.controller;

import com.veridion.challenge.domain.Company;
import com.veridion.challenge.domain.CompanyCsv;
import com.veridion.challenge.domain.CompanyMerged;
import com.veridion.challenge.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest Controller for handling CSV company data
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {
  @Autowired
  private CompanyCrawlerService companyCrawlerService;

  @Autowired
  private CsvParserService csvParserService;

  @Autowired
  private CompanyMatchingService companyMatchingService;

  @Autowired
  private DataMergeService dataMergeService;

  /**
   * Merges company data from a CSV file with scraped company data and returns the merged list.
   * @return ResponseEntity containing the list of merged company data.
   */
  @PostMapping("/merge-csv")
  public ResponseEntity<List<CompanyMerged>> mergeCsvFileWithScrapedData() {
    List<CompanyCsv> csvCompanies = csvParserService.parseCsvFile("src/main/resources/sample-websites-company-names.csv");
    List<Company> scrapedCompanies = companyCrawlerService.getValidCompanies();
    List<CompanyMerged> mergedCompanies = dataMergeService.mergeCompanyData(scrapedCompanies, csvCompanies);

    return ResponseEntity.ok(mergedCompanies);
  }

  /**
   * Initiates the crawling of provided websites to gather company data.
   * @param websites List of website URLs to crawl.
   * @return ResponseEntity containing the list of companies with data extracted from the crawled websites.
   */
  @PostMapping("/crawl")
  public ResponseEntity<List<Company>> crawlWebsites(@RequestBody List<String> websites) {
    List<Company> crawledData = companyCrawlerService.crawlWebsites(websites);

    return ResponseEntity.ok(crawledData);
  }

  /**
   * Finds the best matching company profile based on provided search criteria: name, website, phone number, and Facebook profile.
   * @param name Optional search parameter for company name.
   * @param website Optional search parameter for company website.
   * @param phoneNumber Optional search parameter for company phone number.
   * @param facebookProfile Optional search parameter for company Facebook profile.
   * @return ResponseEntity containing the best matching company profile, or no content if no match is found.
   */
  @GetMapping("/match")
  public ResponseEntity<CompanyMerged> matchCompany(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String website,
    @RequestParam(required = false) String phoneNumber,
    @RequestParam(required = false) String facebookProfile) {

    List<CompanyMerged> matchedCompanies = companyMatchingService.matchCompanies(name, website, phoneNumber, facebookProfile);

    CompanyMerged bestMatch = companyMatchingService.determineBestMatch(matchedCompanies, name, website, phoneNumber, facebookProfile);

    return bestMatch != null ? ResponseEntity.ok(bestMatch) : ResponseEntity.noContent().build();
  }
}
