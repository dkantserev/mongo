package com.mongo.mongo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("private.properties")
@Data
public class BotConfig {

    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String token;
}
