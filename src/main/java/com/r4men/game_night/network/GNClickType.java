package com.r4men.game_night.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum GNClickType {
    LEFT,
    RIGHT,
    SHIFT_LEFT;

    public static final IntFunction<GNClickType> BY_ID = ByIdMap.continuous(GNClickType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, GNClickType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GNClickType::ordinal);

    public static GNClickType left(boolean holdingShift) {
        return holdingShift ? SHIFT_LEFT : LEFT;
    }
}
