package com.r4men.game_night.network.to_server;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.common.network.PacketUtils;
import com.r4men.game_night.network.IGameNightPacket;
import com.r4men.game_night.network.to_client.PacketChessData;
import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketChessBoardOpen(BlockPos pos, String name) implements IGameNightPacket {
    public static final CustomPacketPayload.Type<PacketChessBoardOpen> TYPE = new CustomPacketPayload.Type<>(GameNight.id("chess_board_open"));
    public static final StreamCodec<ByteBuf, PacketChessBoardOpen> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketChessBoardOpen::pos,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketChessBoardOpen::name,
            PacketChessBoardOpen::new
    );

    @Override
    public @NotNull Type<PacketChessBoardOpen> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!name.isEmpty() && PacketUtils.blockEntity(context, pos) instanceof ChessBlockEntity be) {
            ServerPlayer player = (ServerPlayer) context.player();

            be.setWhitePlayer(player);

            GameNight.LOGGER.info("Opening chess board for {}", name);

            PacketDistributor.sendToPlayer(player, new PacketChessData(player.getName().getString(), player.getName().getString()));
        }
    }
}
