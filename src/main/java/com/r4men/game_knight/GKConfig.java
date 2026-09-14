package com.r4men.game_knight;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.regex.Pattern;

public class GKConfig {
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ELO_SYSTEM;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_COORDINATES;
    public static final ModConfigSpec.ConfigValue<String> SELECT_COLOR;
    public static final ModConfigSpec.ConfigValue<String> MOVE_COLOR;
    public static final ModConfigSpec.ConfigValue<String> CAPTURE_SAME_COLOR;
    public static final ModConfigSpec.ConfigValue<String> CAPTURE_COLOR;

    // Server
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    // Client
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    static {
        ENABLE_ELO_SYSTEM = SERVER_BUILDER
                .comment("Where")
                .translation("game_knight.configuration.enable_elo_system")
                .define("enable_elo_system", true);

        SERVER_SPEC = SERVER_BUILDER.build();

        CLIENT_BUILDER.push("chess");

        SHOW_COORDINATES = CLIENT_BUILDER
                .comment("Show square coordinates on the edge of the board")
                .translation("game_knight.configuration.chess.show_coordinates")
                .define("show_coordinates", true);

        SELECT_COLOR = CLIENT_BUILDER
                .comment("The color to highlight the clicked square and possible moves (#RRGGBBAA)")
                .translation("game_knight.configuration.chess.select_color")
                .define("select_color", "#888888A0", GKConfig::isValidRgbaHex);

        MOVE_COLOR = CLIENT_BUILDER
                .comment("The color to highlight the moved square (#RRGGBBAA)")
                .translation("game_knight.configuration.chess.move_color")
                .define("move_color", "#888888A0", GKConfig::isValidRgbaHex);

        CAPTURE_SAME_COLOR = CLIENT_BUILDER
                .comment("The color to highlight the moved square (#RRGGBBAA)")
                .translation("game_knight.configuration.chess.move_color")
                .define("move_color", "#888888A0", GKConfig::isValidRgbaHex);

        CAPTURE_COLOR = CLIENT_BUILDER
                .comment("The color to highlight the captured square (#RRGGBBAA)")
                .translation("game_knight.configuration.chess.capture_color")
                .define("capture_color", "#F23F3FA0", GKConfig::isValidRgbaHex);

        CLIENT_BUILDER.pop();

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    public static final Pattern RGBA_HEX_PATTERN = Pattern.compile("^#[A-Fa-f0-9]{8}$");
    private static boolean isValidRgbaHex(Object value) {
        return value instanceof String string && RGBA_HEX_PATTERN.matcher(string).matches();
    }
}
