package ru.practicum.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import ru.practicum.client.RestStatClient;
import ru.practicum.client.StatClient;

@org.springframework.context.annotation.Configuration
public class StatClientConfig {
    @Bean
    public StatClient statClient(@Value("${client.url}") String statServerUrl) {
        return new RestStatClient(statServerUrl);
    }
}
