package com.r4men.game_knight.engine;

import java.util.UUID;

public record GameParticipant(UUID uuid, String lastKnownName) {}
