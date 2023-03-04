package com.mongo.mongo.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LastSearch {
    private Long n = 0L;
    private List<ModelPoi> lastSearch;

}
