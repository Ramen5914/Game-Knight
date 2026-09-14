package com.r4men.game_knight.client.auth;

import java.util.concurrent.ConcurrentHashMap;

public final class PendingLichessLinks {
    private static final ConcurrentHashMap<String, PendingLichessLink> PENDING = new ConcurrentHashMap<>();

    private PendingLichessLinks() {}

    public static void put(String state, PendingLichessLink link) {
        PENDING.put(state, link);
    }

    public static PendingLichessLink remove(String state) {
        return PENDING.remove(state);
    }
}