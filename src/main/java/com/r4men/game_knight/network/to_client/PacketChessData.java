package com.r4men.game_knight.network.to_client;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.gui.screen.ChessGameScreen;
import com.r4men.game_knight.network.IGameKnightPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketChessData(String whitePlayer, String blackPlayer) implements IGameKnightPacket {
    public static final CustomPacketPayload.Type<PacketChessData> TYPE = new CustomPacketPayload.Type<>(GameKnight.id("chess_data"));
    public static final StreamCodec<ByteBuf, PacketChessData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketChessData::whitePlayer,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketChessData::blackPlayer,
            PacketChessData::new
    );

    @Override
    public @NotNull Type<PacketChessData> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Screen currentScreen = Minecraft.getInstance().gui.screen();
            GameKnight.LOGGER.info("Setting players for chess screen");

            if (currentScreen instanceof ChessGameScreen chessGameScreen) {
                GameKnight.LOGGER.info("White player: {}", whitePlayer);
                GameKnight.LOGGER.info("Black player: {}", blackPlayer);

                chessGameScreen.setPlayers(whitePlayer + "-w", blackPlayer + "-b");
            }
        }).exceptionally(error -> {
            GameKnight.LOGGER.error("Failed to handle chess data packet", error);
            return Void.TYPE.cast(0);
        });
    }
}
