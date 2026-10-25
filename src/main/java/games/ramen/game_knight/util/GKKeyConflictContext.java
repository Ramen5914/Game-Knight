package games.ramen.game_knight.util;

import games.ramen.game_knight.gui.screen.ChessGameScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.NotNull;

public enum GKKeyConflictContext implements IKeyConflictContext {
    CHESS {
        @Override
        public boolean isActive() {
            //? if <26.2 {
            //return Minecraft.getInstance().screen instanceof ChessGameScreen;
            //?} else {
            return Minecraft.getInstance().gui.screen() instanceof ChessGameScreen;
            //?}
        }

        @Override
        public boolean conflicts(@NotNull IKeyConflictContext other) {
            return false;
        }
    }
}
