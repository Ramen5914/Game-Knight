package com.r4men.game_night.network;

import com.r4men.game_night.GameNight;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handleDataOnMain(ChessData data, IPayloadContext iPayloadContext) {
        GameNight.LOGGER.info("Received data on server: {}", data.age());
    }
}
