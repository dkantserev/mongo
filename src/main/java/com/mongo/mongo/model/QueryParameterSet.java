package com.mongo.mongo.model;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public  class QueryParameterSet {
    @NotBlank
    List<String> query = new ArrayList<>();

}
