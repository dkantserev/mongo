package com.mongo.mongo.model;

import lombok.Data;



@Data
public class BotUser {
    private String name;
    private Long ChatId;
    private LastSearch lastSearch;
}
