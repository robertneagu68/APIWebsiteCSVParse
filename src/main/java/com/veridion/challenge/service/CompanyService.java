package com.veridion.challenge.service;

import com.veridion.challenge.domain.Company;
import com.veridion.challenge.repository.CompanyRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  @Autowired
  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  /**
   * Extracts company data from a parsed HTML document of a website.
   * @param document The Jsoup Document of the website.
   * @param website The URL of the website.
   * @return A Company object populated with extracted data or null in case of failure.
   */
  public Company extractDataFromWebsite(Document document, String website) {
    try {
      String name = extractName(document);
      String phoneNumber = extractPhoneNumber(document);
      String facebookProfile = extractFacebookProfile(document);
      String address = extractAddress(document);

      Company company = new Company();
      company.setName(name);
      company.setWebsite(website);
      company.setPhoneNumber(phoneNumber);
      company.setFacebookProfile(facebookProfile);
      company.setAddress(address);

      return company;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @param document extracted using JSoup
   * @return The name of the scraped company
   */
  private String extractName(Document document) {
    String name = document.select("h1, title").text();
    if (name.isEmpty()) {
      return "Default Name";
    } else {
      return name.length() > 255 ? name.substring(0, 255) : name;
    }
  }

  /**
   * @param document extracted using JSoup
   * @return The phone number of the scraped company
   */
  private String extractPhoneNumber(Document document) {
    Elements phoneLinks = document.select("a[href^=tel]");
    Set<String> uniquePhoneNumbers = new HashSet<>();

    for (Element link : phoneLinks) {
      uniquePhoneNumbers.add(link.text().trim());
    }

    return uniquePhoneNumbers.stream().findFirst().orElse("PhoneNumber not found");
  }

  /**
   * @param document extracted using JSoup
   * @return The facebook link of the scraped company
   */
  private String extractFacebookProfile(Document document) {
    Elements facebookLinks = document.select("a[href*=facebook.com]");

    for (Element link : facebookLinks) {
      String href = link.attr("abs:href");

      Matcher matcher = Pattern.compile("https?://(www\\.)?facebook\\.com/([^/?&]+)").matcher(href);
      if (matcher.find()) {
        return "https://www.facebook.com/" + matcher.group(2);
      }
    }
    return "Facebook profile URL not found";
  }

  /**
   * @param document extracted using JSoup
   * @return The adress of the scraped company
   */
  private String extractAddress(Document document) {
    String address = document.select("span[class*=address]").text();
    return address.isEmpty() ? "Default address" : address;
  }
}
