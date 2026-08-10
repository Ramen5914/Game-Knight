package com.r4men.game_knight.datagen.lang;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.block.GKBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class GKEnUsLanguageProvider extends LanguageProvider {
    public GKEnUsLanguageProvider(PackOutput output) {
        super(output, GameKnight.MOD_ID, "en_us");
    }

    @Override
    public @NotNull String getName() {
        return GameKnight.NAME + "'s English (US) Translations";
    }

    @Override
    protected void addTranslations() {
        // Creative Mode Tabs
        add("itemGroup.game_knight.kn_tab", "Game Night");

        // Config
        // #Server
        add("game_knight.configuration.enable_elo_system", "Enable Elo System");
        // #Client
        // ##Chess
        add("game_knight.configuration.chess", "Chess");
        add("game_knight.configuration.chess.show_coordinates", "Show Coordinates");

        // ##Go
        add("game_knight.configuration.go", "Go");

        // Block Entities
        add("game_knight.games.chess.title", "Chess");

        // Game UIs
        // #Chess
        add("game_knight.games.chess.setup.mode", "Mode");
        add("game_knight.games.chess.setup.start", "Start");

        // Blocks
        add(GKBlocks.CHESS.get(), "Chess");
        add(GKBlocks.MONOPOLY.get(), "Monopoly");
    }
}
