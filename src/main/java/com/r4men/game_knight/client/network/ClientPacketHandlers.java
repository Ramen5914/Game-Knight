package com.r4men.game_knight.client.network;

import com.r4men.game_knight.network.to_client.PacketOpenChessSetupScreen;
import net.ethrocky.pane.core.Component;
import net.ethrocky.pane.core.State;
import net.ethrocky.pane.runtime.PaneScreen;
import net.ethrocky.pane.widget.ColorPicker;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleOpenChessSetupScreen(
            PacketOpenChessSetupScreen payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            State<Integer> color = State.of(0xFFFFFF);

            PaneScreen screen = new PaneScreen(
                    "Chess Setup",
                    new Component() {}
                            .add(ColorPicker.of(color))
            );

            Minecraft.getInstance().setScreenAndShow(screen);
        });
    }
}
