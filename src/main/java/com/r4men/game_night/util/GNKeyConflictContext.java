package com.r4men.game_night.util;

import com.r4men.game_night.gui.screen.ChessScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.NotNull;

public enum GNKeyConflictContext implements IKeyConflictContext {
    CHESS {
        @Override
        public boolean isActive() {
            return Minecraft.getInstance().screen instanceof ChessScreen;
        }

        @Override
        public boolean conflicts(@NotNull IKeyConflictContext other) {
            return false;
        }
    }
}
