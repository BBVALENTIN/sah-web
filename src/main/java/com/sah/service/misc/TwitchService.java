package com.sah.service.misc;

import com.fasterxml.jackson.databind.JsonNode;
import com.sah.dto.misc.StreamDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class TwitchService {

    private final WebClient twitchWebClient;

    public TwitchService(WebClient twitchWebClient) {
        this.twitchWebClient = twitchWebClient;
    }

    private String getChessGameId() {
        JsonNode response = twitchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("name", "Chess")
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return response.get("data").get(0).get("id").asText();
    }

    public StreamDTO getMostPopularChessStream() {
        String gameId = getChessGameId();

        JsonNode response = twitchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/streams")
                        .queryParam("game_id", gameId)
                        .queryParam("first", 1)
                        .build()).retrieve().bodyToMono(JsonNode.class).block();

        JsonNode data = response.get("data");

        if(data == null || data.isEmpty())
            return null;

        JsonNode stream = data.get(0);
        return formatStream(stream);
    }

    public List<StreamDTO> getChessStreams() {

        String gameId = getChessGameId();

        JsonNode response = twitchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/streams")
                        .queryParam("game_id", gameId)
                        .queryParam("first", 30)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<StreamDTO> result = new ArrayList<>();

        for (JsonNode stream : response.get("data")) {

            StreamDTO dto = formatStream(stream);

            result.add(dto);
        }

        return result;
    }

    private StreamDTO formatStream(JsonNode stream) {
        StreamDTO dto = new StreamDTO();

        dto.setUserName(stream.path("user_name").asText());
        dto.setTitle(stream.path("title").asText());
        dto.setViewerCount(stream.path("viewer_count").asInt());
        dto.setThumbnailUrl(
                stream.path("thumbnail_url").asText()
                        .replace("{width}", "320")
                        .replace("{height}", "180")
        );

        return dto;
    }
}
