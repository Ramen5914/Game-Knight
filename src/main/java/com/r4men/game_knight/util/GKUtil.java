package com.r4men.game_knight.util;

import com.r4men.game_knight.GKConfig;

public class GKUtil {
    public static int getArgbIntFromRgbaString(String str) {
        if (str == null || !GKConfig.RGBA_HEX_PATTERN.matcher(str).matches()) {
            throw new IllegalArgumentException(
                    "Expected color in #RRGGBBAA format, got: " + str
            );
        }

        int red = Integer.parseInt(str.substring(1, 3), 16);
        int green = Integer.parseInt(str.substring(3, 5), 16);
        int blue = Integer.parseInt(str.substring(5, 7), 16);
        int alpha = Integer.parseInt(str.substring(7, 9), 16);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
