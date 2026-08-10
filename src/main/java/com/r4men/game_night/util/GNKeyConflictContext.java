package com.r4men.game_night.util;

import com.r4men.game_night.gui.screen.ChessGameScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.NotNull;

public enum GNKeyConflictContext implements IKeyConflictContext {
    CHESS {
        @Override
        public boolean isActive() {
            return Minecraft.getInstance().gui.screen() instanceof ChessGameScreen;
        }

        @Override
        public boolean conflicts(@NotNull IKeyConflictContext other) {
            return false;
        }
    }
}
