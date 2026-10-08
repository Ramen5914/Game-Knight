package com.r4men.game_knight.client.auth;

public enum LichessTimeControl {
    ULTRA_BULLET("ultraBullet", "UltraBullet"),
    BULLET("bullet", "Bullet"),
    BLITZ("blitz", "Blitz"),
    RAPID("rapid", "Rapid"),
    CLASSICAL("classical", "Classical");

    private final String perfKey;
    private final String displayName;

    LichessTimeControl(String perfKey, String displayName) {
        this.perfKey = perfKey;
        this.displayName = displayName;
    }

    public String perfKey() {
        return perfKey;
    }

    public String displayName() {
        return displayName;
    }

    public static LichessTimeControl from(float minutes, float increment) {
        double seconds = minutes * 60.0 + increment * 40.0;

        if (seconds < 30) return ULTRA_BULLET;
        if (seconds < 180) return BULLET;
        if (seconds < 480) return BLITZ;
        if (seconds < 1500) return RAPID;

        return CLASSICAL;
    }
}
