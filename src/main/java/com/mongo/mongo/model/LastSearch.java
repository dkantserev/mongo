package com.mongo.mongo.model;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LastSearch {
    @PositiveOrZero
    private Long n = 0L;
    private List<ModelPoi> lastSearch;

}
