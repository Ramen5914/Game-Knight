package com.r4men.game_knight.client.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.r4men.game_knight.GameKnight;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LichessCallbackServer {
    public static final int CALLBACK_PORT = 3001;
    public static final String REDIRECT_URI = "http://127.0.0.1:" + CALLBACK_PORT + "/auth/lichess/callback";
    public static final String TOKEN_ENDPOINT = "https://lichess.org/api/token";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final CredentialStorage CREDENTIAL_STORAGE = new CredentialStorage();

    private static HttpServer server;
    private static ExecutorService executor;

    private LichessCallbackServer() {}

    public static synchronized void start() throws IOException {
        if (server != null) return;

        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "GameKnight-Lichess-OAuth");
            thread.setDaemon(true);
            return thread;
        });

        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", CALLBACK_PORT),
                0
        );

        server.createContext("/auth/lichess/callback", LichessCallbackServer::handleCallback);

        server.setExecutor(executor);
        server.start();

        GameKnight.LOGGER.info("Lichess OAuth callback server listening at {}",
                REDIRECT_URI
        );
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }

        LichessAuthorizationState.clear();
    }

    private static void handleCallback(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendHtml(
                    exchange,
                    405,
                    page("Method not allowed", "Only GET is allowed here.")
            );
            return;
        }

        try {
            Map<String, String> query = parseQuery(exchange.getRequestURI());

            String authorizationError = query.get("error");
            if (authorizationError != null) {
                LichessAuthorizationState.clear();

                String description = query.getOrDefault(
                        "error_description",
                        authorizationError
                );

                GameKnight.LOGGER.warn(
                        "Lichess authorization was denied or failed: {}",
                        description
                );

                sendHtml(
                        exchange,
                        400,
                        page(
                                "Lichess linking was cancelled",
                                "You may close this browser tab and return to Minecraft."
                        )
                );

                sendMinecraftMessage(
                        "Lichess linking was cancelled or denied."
                );
                return;
            }

            String code = query.get("code");
            String state = query.get("state");

            if (code == null || code.isBlank() || state == null || state.isBlank()) {
                sendHtml(
                        exchange,
                        400,
                        page(
                                "Invalid Lichess callback",
                                "The callback did not contain a valid code and state."
                        )
                );
                return;
            }

            PendingLichessAuthorization pending =
                    LichessAuthorizationState.consume(state);

            if (pending == null) {
                GameKnight.LOGGER.warn(
                        "Rejected Lichess OAuth callback: invalid, expired, or reused state."
                );

                sendHtml(
                        exchange,
                        400,
                        page(
                                "Expired or invalid link request",
                                "Run /gk link lichess again from Minecraft."
                        )
                );

                sendMinecraftMessage(
                        "Lichess link request expired or was invalid. Run /gk link lichess again."
                );
                return;
            }

            LichessTokenResponse tokenResponse = exchangeCodeForToken(
                    code,
                    pending.codeVerifier()
            );

            CREDENTIAL_STORAGE.saveLichessToken(
                    tokenResponse.accessToken()
            );

            sendHtml(
                    exchange,
                    200,
                    page(
                            "Lichess linked successfully",
                            "You may close this browser tab and return to Minecraft."
                    )
            );

            sendMinecraftMessage(
                    "Your Lichess account was linked successfully."
            );
        } catch (Exception exception) {
            GameKnight.LOGGER.error(
                    "Failed to complete the Lichess OAuth callback.",
                    exception
            );

            sendHtml(
                    exchange,
                    500,
                    page(
                            "Could not link Lichess",
                            "Return to Minecraft and check the game log."
                    )
            );

            sendMinecraftMessage(
                    "Lichess linking failed. Check the game log."
            );
        }
    }

    private static LichessTokenResponse exchangeCodeForToken(
            String authorizationCode,
            String codeVerifier
    ) throws IOException, InterruptedException {
        String form = formEncode(Map.of(
                "grant_type", "authorization_code",
                "code", authorizationCode,
                "code_verifier", codeVerifier,
                "redirect_uri", REDIRECT_URI,
                "client_id", LichessOAuth.CLIENT_ID
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_ENDPOINT))
                .timeout(Duration.ofSeconds(15))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded; charset=UTF-8"
                )
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "Lichess token exchange failed with HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }

        JsonObject json = JsonParser.parseString(response.body())
                .getAsJsonObject();

        if (!json.has("access_token") || json.get("access_token").isJsonNull()) {
            throw new IOException(
                    "Lichess token response did not contain access_token."
            );
        }

        return new LichessTokenResponse(
                json.get("access_token").getAsString(),
                json.has("token_type")
                        ? json.get("token_type").getAsString()
                        : "Bearer",
                json.has("scope")
                        ? json.get("scope").getAsString()
                        : ""
        );
    }

    private static void sendMinecraftMessage(String message) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.execute(() -> {
            if (minecraft.player instanceof LocalPlayer player) {
                player.sendSystemMessage(Component.literal(message));
            }
        });
    }

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> values = new HashMap<>();

        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return values;
        }

        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";

            values.put(key, value);
        }

        return values;
    }

    private static String formEncode(Map<String, String> values) {
        StringBuilder form = new StringBuilder();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!form.isEmpty()) {
                form.append('&');
            }

            form.append(encode(entry.getKey()));
            form.append('=');
            form.append(encode(entry.getValue()));
        }

        return form.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static void sendHtml(
            HttpExchange exchange,
            int status,
            String html
    ) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
                "Cache-Control",
                "no-store"
        );

        exchange.sendResponseHeaders(status, body.length);

        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static String page(String heading, String message) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>GameKnight</title>
                </head>
                <body style="font-family: sans-serif; max-width: 42rem; margin: 4rem auto; padding: 0 1rem;">
                  <h1>%s</h1>
                  <p>%s</p>
                </body>
                </html>
                """.formatted(
                escapeHtml(heading),
                escapeHtml(message)
        );
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record LichessTokenResponse(
            String accessToken,
            String tokenType,
            String scope
    ) {
    }
}
