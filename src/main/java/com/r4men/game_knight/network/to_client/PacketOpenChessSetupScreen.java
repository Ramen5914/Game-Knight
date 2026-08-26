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
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketOpenChessSetupScreen(String fen, String whitePlayer,
                                         String blackPlayer) implements IGameKnightPacket {
    public static final Type<PacketOpenChessSetupScreen> TYPE = new Type<>(GameKnight.id("open_chess_setup_screen"));
    public static final StreamCodec<ByteBuf, PacketOpenChessSetupScreen> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(128), PacketOpenChessSetupScreen::fen,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketOpenChessSetupScreen::whitePlayer,
            ByteBufCodecs.stringUtf8(SharedConstants.MAX_PLAYER_NAME_LENGTH), PacketOpenChessSetupScreen::blackPlayer,
            PacketOpenChessSetupScreen::new
    );

    @Override
    public @NotNull Type<PacketOpenChessSetupScreen> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        Minecraft.getInstance().setScreenAndShow(new ChessGameScreen(Component.literal("Chess"), fen, whitePlayer, blackPlayer));
    }
}
