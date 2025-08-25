package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private String uri;

    @Autowired
    public StatsClient(@Value("${client.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
        uri = serviceUrl;
    }

    public ResponseEntity<Object> createHit(StatsRequestDto statsRequestDto) {
        return post("/hit", statsRequestDto);
    }

    public List<StatsResponseDto> getStats(StatsViewRequestDto statsViewRequestDto) {
        String urisCsv = String.join(",", statsViewRequestDto.getUris());

        final String url = uri + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        try {
            ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<List<StatsResponseDto>>() {
                    },
                    Map.of(
                            "start", statsViewRequestDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            "end", statsViewRequestDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            "uris", urisCsv,
                            "unique", statsViewRequestDto.getUnique()
                    )
            );

            List<StatsResponseDto> stats = response.getBody();
            return stats != null ? stats : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}