package com.r4men.game_knight.client.auth;

public record PendingLichessAuthorization(
        String state,
        String codeVerifier,
        long expiresAtMillis
) {
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAtMillis;
    }
}
