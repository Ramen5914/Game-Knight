package com.r4men.game_knight;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GKConfig {
    public static final ModConfigSpec SERVER_SPEC;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ELO_SYSTEM;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_COORDINATES;
    public static final ModConfigSpec.ConfigValue<Integer> SELECT_COLOR;
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
                .comment("The color to highlight the clicked square and possible moves (#AARRGGBB)")
                .translation("game_knight.configuration.chess.select_color")
                .define("select_color", 0xA0888888);

        CLIENT_BUILDER.pop();

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }
}