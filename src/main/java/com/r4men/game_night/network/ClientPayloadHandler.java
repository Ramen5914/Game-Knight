package com.r4men.game_night.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleDataOnMain(ChessData data, IPayloadContext iPayloadContext) {
        Minecraft.getInstance().setScreen(new Screen() {
        });
    }
}
