package com.r4men.game_knight.network.to_server;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.block.entity.ChessBlockEntity;
import com.r4men.game_knight.common.network.PacketUtils;
import com.r4men.game_knight.network.IGameKnightPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketSetupChessBoard(
        BlockPos pos,
        Float minutes,
        Float increment
) implements IGameKnightPacket {
    public static final CustomPacketPayload.Type<PacketSetupChessBoard> TYPE = new CustomPacketPayload.Type<>(GameKnight.id("setup_chess_board"));
    public static final StreamCodec<FriendlyByteBuf, PacketSetupChessBoard> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketSetupChessBoard::pos,
            ByteBufCodecs.FLOAT, PacketSetupChessBoard::minutes,
            ByteBufCodecs.FLOAT, PacketSetupChessBoard::increment,
            PacketSetupChessBoard::new
    );

    @Override
    public @NotNull Type<PacketSetupChessBoard> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (PacketUtils.blockEntity(context, pos) instanceof ChessBlockEntity be) {
            be.setTimeControlSeconds(minutes * 60);
            be.setIncrementSeconds(increment);
            be.setIsSetup(true);
        }
    }
}
