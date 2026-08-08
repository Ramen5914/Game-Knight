package com.r4men.game_night.network;

import com.r4men.game_night.common.lib.Version;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;

public class PacketHandler extends BasePacketHandler {
    // Client to server instanced packets

    // Server to client instanced packets


    public PacketHandler(IEventBus modEventBus, Version version) {
        super(modEventBus, version);

        modEventBus.addListener(RegisterConfigurationTasksEvent.class, event -> {
            ServerConfigurationPacketListener listener = event.getListener();

            // Configuration Tasks
        });
    }

    // Server to client instanced packet functions

    // Client to server packets
    @Override
    protected void registerClientToServer(PacketRegistrar registrar) {
        // registrar.play(TYPE, STREAM_CODEC);
    }

    // Server to client packets
    @Override
    protected void registerServerToClient(PacketRegistrar registrar) {

    }
}
