package com.r4men.game_night.network;

import com.r4men.game_night.GameNight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = GameNight.ID)
public class GNPayloads {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                ChessData.TYPE,
                ChessData.STREAM_CODEC,
                ServerPayloadHandler::handleDataOnMain
        );
    }
}
