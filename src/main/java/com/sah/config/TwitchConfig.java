package com.sah.config;

import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TwitchConfig {
    @Value("${TWITCH_CLIENT_ID}")
    private String clientId;
    @Value("${TWITCH_CLIENT_SECRET}")
    private String clientSecret;

    @Bean
    public WebClient twitchWebClient() {

        WebClient authClient = WebClient.builder().baseUrl("https://id.twitch.tv").build();
        JsonNode tokenResponse = authClient.post().uri(uriBuilder -> uriBuilder
                .path("/oauth2/token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "client_credentials")
                .build()).contentType(MediaType.APPLICATION_FORM_URLENCODED).retrieve().bodyToMono(JsonNode.class).block();

        String accessToken = tokenResponse.get("access_token").asText();

        return WebClient.builder()
                .baseUrl("https://api.twitch.tv/helix")
                .defaultHeader("Client-ID", clientId)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }
}
