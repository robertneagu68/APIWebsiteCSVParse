package com.veridion.challenge.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a simple structure to hold company information from a CSV file.
 */
@Getter
@Setter
public class CompanyCsv {
  public String domain;
  public String company_commercial_name;
  public String company_legal_name;
  public String company_all_available_names;
}
