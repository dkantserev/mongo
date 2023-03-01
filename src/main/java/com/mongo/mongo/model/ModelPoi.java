package com.mongo.mongo.model;




import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document
public class ModelPoi {

    @Id
    private Integer id;
    private Map<String, String> keyValueMap;

}
