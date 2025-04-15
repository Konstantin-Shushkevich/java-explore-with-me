package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class RestStatClient implements StatClient {

    private final RestClient restClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public RestStatClient(@Value("${stats-server.url}") String serverUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Override
    public ResponseEntity<Object> addHit(HitDto hitDto) {
        try {
            ResponseEntity<HitDto> response = restClient.post()
                    .uri("/hit")
                    .body(hitDto)
                    .retrieve()
                    .toEntity(HitDto.class);

            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());
        }
    }

    @Override
    public List<StatsDto> readStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        try {
            ResponseEntity<List<StatsDto>> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start.format(formatter))
                            .queryParam("end", end.format(formatter))
                            .queryParam("uris", uris != null ? uris.toArray() : new String[0])
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<StatsDto>>() {});

            return response.getBody() != null ? response.getBody() : Collections.emptyList();

        } catch (RestClientResponseException e) {
            return Collections.emptyList();
        }
    }
}
