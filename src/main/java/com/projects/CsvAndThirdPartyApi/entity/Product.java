package com.projects.CsvAndThirdPartyApi.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Product {
    @CsvBindByName(column = "productId")
    private String productId;

    @CsvBindByName(column = "locationId")
    private String locationId;
}
