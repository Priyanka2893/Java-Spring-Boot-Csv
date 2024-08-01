package com.projects.CsvAndThirdPartyApi.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Person {

    private String name;

    private int age;
}
