package com.r4men.game_knight.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IGameKnightPacket extends CustomPacketPayload {
    void handle(IPayloadContext context);
}
