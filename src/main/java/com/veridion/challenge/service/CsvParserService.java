package com.veridion.challenge.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.veridion.challenge.domain.CompanyCsv;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {
  /**
   * Parses a CSV file into a list of CompanyCsv objects.
   * @param filePath The file path of the CSV to be parsed.
   * @return A list of CompanyCsv objects parsed from the CSV file.
   */
  public List<CompanyCsv> parseCsvFile(String filePath) {
    try (FileReader reader = new FileReader(filePath)) {
      CsvToBean<CompanyCsv> csvToBean = new CsvToBeanBuilder<CompanyCsv>(reader)
        .withType(CompanyCsv.class)
        .withIgnoreLeadingWhiteSpace(true)
        .build();

      return csvToBean.parse();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
