package com.r4men.game_knight.network.to_server;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.block.entity.ChessBlockEntity;
import com.r4men.game_knight.common.network.PacketUtils;
import com.r4men.game_knight.network.IGameKnightPacket;
import com.r4men.game_knight.network.to_client.PacketOpenChessGameScreen;
import com.r4men.game_knight.network.to_client.PacketOpenChessSetupScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketOpenChessScreenRequest(BlockPos pos) implements IGameKnightPacket {
    public static final CustomPacketPayload.Type<PacketOpenChessScreenRequest> TYPE = new CustomPacketPayload.Type<>(GameKnight.id("open_chess_board_request"));
    public static final StreamCodec<ByteBuf, PacketOpenChessScreenRequest> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketOpenChessScreenRequest::pos,
            PacketOpenChessScreenRequest::new
    );

    @Override
    public @NotNull Type<PacketOpenChessScreenRequest> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (PacketUtils.blockEntity(context, pos) instanceof ChessBlockEntity be) {
            ServerPlayer player = (ServerPlayer) context.player();

            String name = player.getName().getString();

            // TODO remove this line
            be.setIsSetup(true);

            if (be.getIsSetup()) {
                PacketDistributor.sendToPlayer(player, new PacketOpenChessGameScreen(be.getFen(), name, name));
            } else {
                PacketDistributor.sendToPlayer(player, new PacketOpenChessSetupScreen(be.getFen(), name, name));
            }
        }
    }
}
