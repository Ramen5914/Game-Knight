package com.r4men.game_night.network;

import com.r4men.game_night.GameNight;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ChessData(String name, int age) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChessData> TYPE = new CustomPacketPayload.Type<>(GameNight.id("chess_data"));

    public static final StreamCodec<ByteBuf, ChessData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ChessData::name,
            ByteBufCodecs.VAR_INT,
            ChessData::age,
            ChessData::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
