package com.r4men.game_knight.client.auth;

import com.r4men.game_knight.GameKnight;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

public final class LichessOAuth {
    public static final String CLIENT_ID = "game-knight";
    public static final String REDIRECT_URI = "http://127.0.0.1:3001/auth/lichess/callback";

    private static final SecureRandom RANDOM = new SecureRandom();

    private LichessOAuth() {}

    public static URI begin(UUID minecraftUuid, String minecraftUsername) {
        String verifier = randomUrlSafeString(64);
        String challenge = createCodeChallenge(verifier);
        String state = randomUrlSafeString(32);

        LichessAuthorizationState.set(
                new PendingLichessAuthorization(
                        state,
                        verifier,
                        System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()
                )
        );

        String scopes = "preference:read";

        String authorizationUrl = "https://lichess.org/oauth"
                + "?response_type=code"
                + "&client_id=" + encode(CLIENT_ID)
                + "&redirect_uri=" + encode(LichessCallbackServer.REDIRECT_URI)
                + "&scope=" + encode(scopes)
                + "&state=" + encode(state)
                + "&code_challenge=" + encode(challenge)
                + "&code_challenge_method=S256";

        return URI.create(authorizationUrl);
    }

    public static String createCodeChallenge(String verifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(verifier.getBytes(StandardCharsets.US_ASCII));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create PKCE challenge", e);
        }
    }

    private static String randomUrlSafeString(int byteCount) {
        byte[] bytes = new byte[byteCount];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
