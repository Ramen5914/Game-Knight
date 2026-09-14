package com.r4men.game_knight.network.to_client;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.network.IGameKnightPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketUpdateChessGameScreen(String t) implements IGameKnightPacket {
    public static final Type<PacketUpdateChessGameScreen> TYPE = new CustomPacketPayload.Type<>(GameKnight.id("update_chess_game_screen"));
    public static final StreamCodec<ByteBuf, PacketUpdateChessGameScreen> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(100), PacketUpdateChessGameScreen::t,
            PacketUpdateChessGameScreen::new
    );

    @Override
    public @NotNull Type<PacketUpdateChessGameScreen> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {

    }
}
