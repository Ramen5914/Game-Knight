package games.ramen.game_knight.client.network;

import games.ramen.game_knight.gui.screen.ChessGameScreen;
import games.ramen.game_knight.gui.screen.ChessSetupPane;
import games.ramen.game_knight.network.to_client.PacketOpenChessGameScreen;
import games.ramen.game_knight.network.to_client.PacketOpenChessSetupScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleOpenChessSetupScreen(
            PacketOpenChessSetupScreen payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreenAndShow(new ChessSetupPane(payload.pos()));
        });
    }

    public static void handleOpenChessGameScreen(
            PacketOpenChessGameScreen payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreenAndShow(new ChessGameScreen(net.minecraft.network.chat.Component.literal("Chess"), "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "Dev1", "Dev2", true));
        });
    }
}
