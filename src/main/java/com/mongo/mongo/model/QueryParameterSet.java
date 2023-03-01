package com.mongo.mongo.model;



import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public  class QueryParameterSet {
    List<String> query = new ArrayList<>();

}
