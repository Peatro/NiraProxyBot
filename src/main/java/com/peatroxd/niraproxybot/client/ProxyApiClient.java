package com.peatroxd.niraproxybot.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peatroxd.niraproxybot.dto.HealthStatusDto;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class ProxyApiClient {

    private static final TypeReference<List<ProxyTelegramLinkDto>> LIST_TYPE = new TypeReference<>() {};
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${app.proxy-api-url}")
    private String apiUrl;

    @Value("${app.health-api-url}")
    private String healthApiUrl;

    public List<ProxyTelegramLinkDto> getBestLinks(int limit) {
        try {
            return fetchBestLinks(limit);
        } catch (Exception e) {
            log.error("Failed to fetch proxy links from {}", apiUrl, e);
            return List.of();
        }
    }

    public List<ProxyTelegramLinkDto> fetchBestLinks(int limit) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?limit=" + limit))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Proxy API returned status " + response.statusCode());
        }

        String body = response.body();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Proxy API returned an empty response body");
        }

        List<ProxyTelegramLinkDto> links = MAPPER.readValue(body, LIST_TYPE);
        return links == null ? List.of() : links;
    }

    public HealthStatusDto fetchHealthStatus() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthApiUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Health API status " + response.statusCode());
            }
            return MAPPER.readValue(response.body(), HealthStatusDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch health status from {}", healthApiUrl, e);
            return null;   // null = не смогли проверить; трактуем осторожно (см. scheduler)
        }
    }
}
