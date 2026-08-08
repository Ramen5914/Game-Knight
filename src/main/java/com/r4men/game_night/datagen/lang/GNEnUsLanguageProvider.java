package com.r4men.game_night.datagen.lang;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class GNEnUsLanguageProvider extends LanguageProvider {
    public GNEnUsLanguageProvider(PackOutput output) {
        super(output, GameNight.MOD_ID, "en_us");
    }

    @Override
    public @NotNull String getName() {
        return GameNight.NAME + "'s English (US) Translations";
    }

    @Override
    protected void addTranslations() {
        // Creative Mode Tabs
        add("itemGroup.game_night.gn_tab", "Game Night");

        // Config
        // #Server
        add("game_night.configuration.enable_elo_system", "Enable Elo System");
        // #Client
        // ##Chess
        add("game_night.configuration.chess", "Chess");
        add("game_night.configuration.chess.show_coordinates", "Show Coordinates");

        // ##Go
        add("game_night.configuration.go", "Go");

        // Block Entities
        add("game_night.games.chess.title", "Chess");

        // Game UIs
        // #Chess
        add("game_night.games.chess.setup.mode", "Mode");
        add("game_night.games.chess.setup.start", "Start");

        // Blocks
        add(GNBlocks.CHESS.get(), "Chess");
        add(GNBlocks.MONOPOLY.get(), "Monopoly");
    }
}
