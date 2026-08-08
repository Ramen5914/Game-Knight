package com.r4men.game_night.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IGameNightPacket extends CustomPacketPayload {
    void handle(IPayloadContext context);
}
