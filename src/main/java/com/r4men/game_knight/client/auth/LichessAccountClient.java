package com.r4men.game_knight.client.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public final class LichessAccountClient {
    private static final String ACCOUNT_ENDPOINT = "https://lichess.org/api/account";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private LichessAccountClient() {
    }

    public static LichessAccountStatus fetchAccountStatus(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ACCOUNT_ENDPOINT))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "Lichess account request failed with HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String username = readString(json, "username", "unknown");

        return new LichessAccountStatus(username, readRatings(json));
    }

    private static String readString(JsonObject json, String property, String fallback) {
        if (!json.has(property) || json.get(property).isJsonNull()) {
            return fallback;
        }
        return json.get(property).getAsString();
    }

    private static java.util.Map<LichessTimeControl, Integer> readRatings(
            JsonObject json
    ) {
        var ratings =
                new java.util.EnumMap<LichessTimeControl, Integer>(
                        LichessTimeControl.class
                );

        if (!json.has("perfs") || !json.get("perfs").isJsonObject()) {
            return ratings;
        }

        JsonObject perfs = json.getAsJsonObject("perfs");

        for (LichessTimeControl control : LichessTimeControl.values()) {
            if (!perfs.has(control.perfKey())
                    || !perfs.get(control.perfKey()).isJsonObject()) {
                continue;
            }

            JsonObject perf = perfs.getAsJsonObject(control.perfKey());

            if (perf.has("rating") && !perf.get("rating").isJsonNull()) {
                ratings.put(control, perf.get("rating").getAsInt());
            }
        }

        return ratings;
    }

    public record LichessAccountStatus(
            String username,
            Map<LichessTimeControl, Integer> ratings
    ) {
        public LichessAccountStatus {
            ratings = Map.copyOf(ratings);
        }

        public int rating(LichessTimeControl control) {
            return ratings.getOrDefault(control, -1);
        }

        public int rapidRating() {
            return rating(LichessTimeControl.RAPID);
        }
    }
}
