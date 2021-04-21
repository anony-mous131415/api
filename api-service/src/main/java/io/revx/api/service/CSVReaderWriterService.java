package io.revx.api.service;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.controller.strategy.BulkStrategiesDataDTO;
import io.revx.api.utility.Util;
import io.revx.api.utils.CustomMappingStrategy;
import io.revx.core.constant.Constants;
import io.revx.core.model.ChartCSVDashboardData;
import io.revx.core.model.ListCSVDashboardData;
import io.revx.core.model.SlicexGridCSVData;


@Component
public class CSVReaderWriterService {

  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  @Autowired
  ApplicationProperties applicationProperties;

  public void writeToCSV(String filename, List<ChartCSVDashboardData> resultForCsv)
      throws Exception {
    Util.createFileDirectory(applicationProperties.getDownloadFilePath(), filename);
    FileWriter fw = new FileWriter(applicationProperties.getDownloadFilePath() + "/" + filename);
    CustomMappingStrategy<ChartCSVDashboardData> mappingStrategy =
        new CustomMappingStrategy<ChartCSVDashboardData>(
            loginUserDetailsService.getHighestRoleOfLoginUser());
    mappingStrategy.setType(ChartCSVDashboardData.class);
    StatefulBeanToCsv<ChartCSVDashboardData> writer =
        new StatefulBeanToCsvBuilder<ChartCSVDashboardData>(fw)
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(true).withMappingStrategy(mappingStrategy).build();
    writer.write(resultForCsv);
    fw.close();
  }

  public void writeListToCSV(String filename, List<ListCSVDashboardData> resultForCsv)
      throws Exception {
    Util.createFileDirectory(applicationProperties.getDownloadFilePath(), filename);
    FileWriter fw = new FileWriter(applicationProperties.getDownloadFilePath() + "/" + filename);
    CustomMappingStrategy<ListCSVDashboardData> mappingStrategy =
        new CustomMappingStrategy<ListCSVDashboardData>(
            loginUserDetailsService.getHighestRoleOfLoginUser());
    mappingStrategy.setType(ListCSVDashboardData.class);
    StatefulBeanToCsv<ListCSVDashboardData> writer =
        new StatefulBeanToCsvBuilder<ListCSVDashboardData>(fw)
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(true).withMappingStrategy(mappingStrategy).build();
    writer.write(resultForCsv);
    fw.close();
  }

  public void writeSlicexDataToCSV(String filename, List<SlicexGridCSVData> resultForCsv)
      throws Exception {
    Util.createFileDirectory(applicationProperties.getDownloadFilePath(), filename);
    FileWriter fw = new FileWriter(applicationProperties.getDownloadFilePath() + "/" + filename);
    CustomMappingStrategy<SlicexGridCSVData> mappingStrategy =
        new CustomMappingStrategy<SlicexGridCSVData>(
            loginUserDetailsService.getHighestRoleOfLoginUser());
    mappingStrategy.setType(SlicexGridCSVData.class);
    StatefulBeanToCsv<SlicexGridCSVData> writer =
        new StatefulBeanToCsvBuilder<SlicexGridCSVData>(fw)
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(true).withMappingStrategy(mappingStrategy).build();
    writer.write(resultForCsv);
    fw.close();
  }

  public void writeBulkToCSV(String filename, List<BulkStrategiesDataDTO> resultForCsv)
      throws Exception {
    Util.createFileDirectory(applicationProperties.getDownloadFilePath(), filename);
   // FileWriter fw = new FileWriter(applicationProperties.getDownloadFilePath() + "/" + filename,StandardCharsets.UTF_8);
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(applicationProperties.getDownloadFilePath() + "/" + filename),StandardCharsets.UTF_16LE);
    CustomMappingStrategy<BulkStrategiesDataDTO> mappingStrategy =
        new CustomMappingStrategy<>(
            loginUserDetailsService.getHighestRoleOfLoginUser());
    mappingStrategy.setType(BulkStrategiesDataDTO.class);
    StatefulBeanToCsv<BulkStrategiesDataDTO> writer =
        new StatefulBeanToCsvBuilder<BulkStrategiesDataDTO>(fw)
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(Constants.COMMA_SEPARATOR)
            .withOrderedResults(true).withMappingStrategy(mappingStrategy).build();
    writer.write(resultForCsv);
    fw.close();
  }


}

