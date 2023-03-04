package com.mongo.mongo.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class BotUser {

    private String name;
    @NotNull
    private Long ChatId;
    private LastSearch lastSearch;
}
