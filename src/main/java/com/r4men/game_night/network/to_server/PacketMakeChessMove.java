package com.r4men.game_night.network.to_server;

import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.common.network.PacketUtils;
import com.r4men.game_night.engine.chess.type.Move;
import com.r4men.game_night.network.IGameNightPacket;
import com.r4men.game_night.network.to_client.PacketUpdateChessGameScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketMakeChessMove(BlockPos pos, Move move) implements IGameNightPacket {
    public static final CustomPacketPayload.Type<PacketMakeChessMove> TYPE = new CustomPacketPayload.Type<>(com.r4men.game_night.GameNight.id("make_chess_move"));
    public static final StreamCodec<FriendlyByteBuf, PacketMakeChessMove> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketMakeChessMove::pos,
            Move.STREAM_CODEC, PacketMakeChessMove::move,
            PacketMakeChessMove::new
    );

    @Override
    public @NotNull Type<PacketMakeChessMove> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();

        if (PacketUtils.blockEntity(context, pos) instanceof ChessBlockEntity be) {
            be.makeMove(move);
            PacketDistributor.sendToAllPlayers(new PacketUpdateChessGameScreen("Test"));
        }
    }
}
