package com.r4men.game_knight.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum GKClickType {
    LEFT,
    RIGHT,
    SHIFT_LEFT;

    public static final IntFunction<GKClickType> BY_ID = ByIdMap.continuous(GKClickType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, GKClickType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GKClickType::ordinal);

    public static GKClickType left(boolean holdingShift) {
        return holdingShift ? SHIFT_LEFT : LEFT;
    }
}
