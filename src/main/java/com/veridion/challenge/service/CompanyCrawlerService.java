package com.veridion.challenge.service;

import com.veridion.challenge.domain.Company;
import com.veridion.challenge.repository.CompanyRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CompanyCrawlerService {
  @Autowired
  private CompanyService companyService;

  @Autowired
  private CompanyRepository companyRepository;

  private List<Company> validCompanies;

  /**
   * Retrieves all valid companies previously saved.
   * @return A list of all valid companies.
   */
  public List<Company> getValidCompanies() {
    return companyRepository.findAll();
  }

  /**
   * Crawls a list of given websites to extract company data asynchronously.
   * @param websites A list of website URLs to crawl.
   * @return A list of Company objects with extracted data.
   */
  public List<Company> crawlWebsites(List<String> websites) {
    List<CompletableFuture<Company>> futures = websites.stream()
      .map(this::crawlWebsiteAsync)
      .toList();

    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    allOf.join();

    List<Company> companyList =  futures.stream()
      .map(CompletableFuture::join)
      .collect(Collectors.toList());

    List<Company> validCompanies = companyList.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    companyRepository.saveAll(validCompanies);


    return companyList;
  }

  /**
   * Initiates asynchronous crawling of a single website.
   * @param website The URL of the website to crawl.
   * @return A CompletableFuture of Company containing the extracted company data.
   */
  private CompletableFuture<Company> crawlWebsiteAsync(String website) {
    validCompanies = new ArrayList<Company>();
    return CompletableFuture.supplyAsync(() -> {
      try {
        URL url = createUrl(website);
        if (url != null  && isHostReachable(url.getHost())) {
          Document document = Jsoup.connect(url.toString()).get();
          Company company = companyService.extractDataFromWebsite(document, website);
          if (company != null) {
            validCompanies.add(company); // Add the valid company to the list
          }
          return companyService.extractDataFromWebsite(document, website);
          //return company;
        } else {
          System.out.println("Invalid URL: " + website);
        }
      } catch (IOException ignored) {
      }
      return null;
    });
  }

  /**
   * Creates a URL object from a given website string, adding "http://" prefix if missing to help for scraping.
   * @param website The website URL as a string.
   * @return A URL object or null if the URL is malformed.
   */
  private URL createUrl(String website) {
    try {
      String url = website;
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://" + url;
      }
      return new URL(url);
    } catch (MalformedURLException e) {
      return null;
    }
  }

  /**
   * Checks if a host is reachable within a specified timeout.
   * @param host The hostname to check.
   * @return true if the host is reachable; false otherwise.
   */
  private boolean isHostReachable(String host) {
    try {
      InetAddress address = InetAddress.getByName(host);
      return address.isReachable(10000);
    } catch (IOException e) {
      return false;
    }
  }

}
