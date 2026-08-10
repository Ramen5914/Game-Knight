package com.r4men.game_knight.network;

import com.r4men.game_knight.common.lib.Version;
import com.r4men.game_knight.network.to_client.PacketChessData;
import com.r4men.game_knight.network.to_client.PacketOpenChessGameScreen;
import com.r4men.game_knight.network.to_client.PacketOpenChessSetupScreen;
import com.r4men.game_knight.network.to_client.PacketUpdateChessGameScreen;
import com.r4men.game_knight.network.to_server.PacketChessBoardOpen;
import com.r4men.game_knight.network.to_server.PacketMakeChessMove;
import com.r4men.game_knight.network.to_server.PacketOpenChessScreenRequest;
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
         registrar.play(PacketChessBoardOpen.TYPE, PacketChessBoardOpen.STREAM_CODEC);
         registrar.play(PacketOpenChessScreenRequest.TYPE, PacketOpenChessScreenRequest.STREAM_CODEC);
         registrar.play(PacketMakeChessMove.TYPE, PacketMakeChessMove.STREAM_CODEC);
    }

    // Server to client packets
    @Override
    protected void registerServerToClient(PacketRegistrar registrar) {
        registrar.play(PacketChessData.TYPE, PacketChessData.STREAM_CODEC);
        registrar.play(PacketOpenChessGameScreen.TYPE, PacketOpenChessGameScreen.STREAM_CODEC);
        registrar.play(PacketOpenChessSetupScreen.TYPE, PacketOpenChessSetupScreen.STREAM_CODEC);
        registrar.play(PacketUpdateChessGameScreen.TYPE, PacketUpdateChessGameScreen.STREAM_CODEC);
    }
}
