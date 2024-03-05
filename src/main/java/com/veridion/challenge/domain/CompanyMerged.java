package com.veridion.challenge.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a merged view of company information, combining data from web scraping and CSV sources.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class CompanyMerged {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String name;
  public String domain;
  public String website;
  public String phoneNumber;
  public String facebookProfile;
  public String address;
  public String company_commercial_name;
  public String company_legal_name;
  public String company_all_available_names;

  /**
   * Constructs a CompanyMerged instance by merging Company and CompanyCsv data.
   * @param company The company data obtained from web scraping.
   * @param companyCsv The company data obtained from a CSV file.
   */
  public CompanyMerged(Company company, CompanyCsv companyCsv) {
    this.name = company.getName();
    this.website = company.getWebsite();
    this.phoneNumber = company.getPhoneNumber();
    this.facebookProfile = company.getFacebookProfile();
    this.address = company.getAddress();
    this.domain = companyCsv.getDomain();
    this.company_commercial_name = companyCsv.getCompany_commercial_name();
    this.company_legal_name = companyCsv.getCompany_legal_name();
    this.company_all_available_names = companyCsv.getCompany_all_available_names();
  }
}
