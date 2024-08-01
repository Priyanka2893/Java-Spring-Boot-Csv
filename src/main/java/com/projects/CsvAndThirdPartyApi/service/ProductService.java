package com.projects.CsvAndThirdPartyApi.service;

import com.opencsv.CSVWriter;
import com.projects.CsvAndThirdPartyApi.entity.Person;
import com.projects.CsvAndThirdPartyApi.entity.Product;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.*;
import java.util.ArrayList;


@Service
public class ProductService {
    @Value("${base.url}")
    private String baseUrl;
    @Value("${mid.url}")
    private String midUrl;
    @Value("${end.url}")
    private String endUrl;
    @Value("${inputCsv.path}")
    private String inputCsvPath;
    @Value("${outputCsv.path}")
    private String outputCsvPath;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ResourceLoader resourceLoader;
    CSVParser parser = null;

    @PostConstruct
    private void readCsv() throws Exception {
        logger.info("******* Reading from csv *********");

        Resource resource = resourceLoader.getResource(inputCsvPath);
        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            parser = CSVFormat.DEFAULT.builder()
                    .setDelimiter(',')
                    .setHeader()
                    .build()
                    .parse(br);
            processDataFromCSV(parser);
        } catch (Exception e) {
            logger.error("Exception caught while reading csv {}", e.getMessage());
        }
    }


    public void processDataFromCSV(CSVParser parser) {
        logger.info("************* Processing data from CSV **********");
        Product product = new Product();
        ArrayList<Person> personList = new ArrayList<>();
        try {
            for (CSVRecord record : this.parser) {
                String productId = record.get("productId");
                String locationId = record.get("locationId");
//                System.out.println(productId,locationId);
                product.setProductId(productId);
                product.setLocationId(locationId);
                String response = makeGetCallToSearchInventory(productId, locationId);
            }
                Person person = new Person();
                Person person1 = new Person();
                Person person2 = new Person();
                person.setName("Test data");
                person.setAge(2);
                person1.setName("Test data1");
                person1.setAge(2);
                person2.setName("Test data2");
                person2.setAge(2);
                personList.add(person);
                personList.add(person1);
                personList.add(person2);

//                 Prepare new csv and add personList
                createCsv(personList);


        } catch (Exception e) {
            logger.debug("Error reading CSV file: " + e);

        }
    }

    private void createCsv(ArrayList<Person> personList) throws IOException{
        logger.info("************ Creating csv ************");
        File file = new File(outputCsvPath);
        logger.info("Output path: {}", file.getAbsolutePath());

        file.getParentFile().mkdirs();
        try (FileWriter outputfile = new FileWriter(file);
             CSVWriter writer = new CSVWriter(outputfile)) {
            String[] headerRecord = {"Name", "Age"};
            writer.writeNext(headerRecord);

            if (!personList.isEmpty()) {
                for (Person person : personList) {
                    String[] data = {person.getName(), String.valueOf(person.getAge())};
                    writer.writeNext(data);
                }
            outputfile.flush();
            } else {
                logger.warn("Person list is empty, no data to write to CSV");
            }
        }catch (Exception e){
            logger.error("No directory found for csv creation {}",e.getMessage());
        }
    }

    private String makeGetCallToSearchInventory(String productId, String locationId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = prepareUrls(productId, locationId);
        logger.info("Going to make Get api call with url: {}", url);
        String response = "";
        try {
            response = restTemplate.getForObject(url, String.class);
            logger.info("********** Get Api are generated **********");
        } catch (Exception e){
            logger.error("Exception occurred while fetching data for productId : {}, locationId : {}",productId,locationId);
        }
        return response;
    }

    public String prepareUrls(String productId, String locationId){
        logger.info("********* Preparing URLs now ************");
        return String.format(baseUrl+ "%s" + midUrl + "%s",productId,locationId);
    }
}



























