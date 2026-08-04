package com.r4men.game_night.network;

import com.r4men.game_night.GameNight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = GameNight.ID)
public class ClientEvent {
    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                ChessData.TYPE,
                ClientPayloadHandler::handleDataOnMain
        );
    }
}
