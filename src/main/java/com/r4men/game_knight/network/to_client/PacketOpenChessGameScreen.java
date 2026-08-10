package com.r4men.game_knight.network.to_client;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.gui.screen.ChessGameScreen;
import com.r4men.game_knight.network.IGameKnightPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketOpenChessGameScreen(String fen, String whitePlayer, String blackPlayer) implements IGameKnightPacket {
    public static final CustomPacketPayload.Type<PacketOpenChessGameScreen> TYPE = new CustomPacketPayload.Type<>(GameKnight.id("open_chess_game_screen"));
    public static final StreamCodec<ByteBuf, PacketOpenChessGameScreen> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(128), PacketOpenChessGameScreen::fen,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketOpenChessGameScreen::whitePlayer,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketOpenChessGameScreen::blackPlayer,
            PacketOpenChessGameScreen::new
    );

    @Override
    public @NotNull Type<PacketOpenChessGameScreen> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Minecraft.getInstance().setScreenAndShow(new ChessGameScreen(Component.literal("Chess"), fen, whitePlayer, blackPlayer));
    }
}
