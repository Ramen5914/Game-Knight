package com.r4men.game_knight.client.auth;

import java.util.UUID;

public record PendingLichessLink(
        UUID minecraftUuid,
        String minecraftUsername,
        String state,
        String codeVerifier
) {}