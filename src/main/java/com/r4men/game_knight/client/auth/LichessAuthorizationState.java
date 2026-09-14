package com.r4men.game_knight.client.auth;

import com.r4men.game_knight.GameKnight;

import java.util.concurrent.atomic.AtomicReference;

public final class LichessAuthorizationState {
    private static final AtomicReference<PendingLichessAuthorization> PENDING =
            new AtomicReference<>();

    private LichessAuthorizationState() {
    }

    public static void set(PendingLichessAuthorization authorization) {
        PENDING.set(authorization);

        GameKnight.LOGGER.info(
                "Created pending Lichess authorization: state={}, expiresIn={}s",
                statePreview(authorization.state()),
                (authorization.expiresAtMillis() - System.currentTimeMillis()) / 1000
        );
    }

    public static PendingLichessAuthorization consume(String callbackState) {
        PendingLichessAuthorization pending = PENDING.getAndSet(null);

        if (pending == null) {
            GameKnight.LOGGER.warn(
                    "Rejected Lichess callback: no pending authorization. callbackState={}",
                    statePreview(callbackState)
            );
            return null;
        }

        if (pending.isExpired()) {
            GameKnight.LOGGER.warn(
                    "Rejected Lichess callback: authorization expired. expectedState={}, callbackState={}",
                    statePreview(pending.state()),
                    statePreview(callbackState)
            );
            return null;
        }

        if (!pending.state().equals(callbackState)) {
            GameKnight.LOGGER.warn(
                    "Rejected Lichess callback: state mismatch. expectedState={}, callbackState={}",
                    statePreview(pending.state()),
                    statePreview(callbackState)
            );
            return null;
        }

        GameKnight.LOGGER.info(
                "Accepted Lichess OAuth callback for state={}",
                statePreview(callbackState)
        );

        return pending;
    }

    public static void clear() {
        PENDING.set(null);
    }

    private static String statePreview(String state) {
        if (state == null) {
            return "<null>";
        }

        return state.length() <= 12
                ? state
                : state.substring(0, 12) + "...";
    }
}