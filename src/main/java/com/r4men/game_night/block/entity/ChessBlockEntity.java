package com.r4men.game_night.block.entity;

import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.engine.chess.Board;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ChessBlockEntity extends BlockEntity implements MenuProvider {
    private String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private Board board;
    private boolean isSetup = false;
    private long timeControlSeconds = 0;
    private long incrementSeconds = 0;
    private long whiteTimeMs = 0;
    private long blackTimeMs = 0;
    private long lastMoveTimestamp = System.currentTimeMillis();
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private char winner = 0; // 'w' for white, 'b' for black, 'd' for draw, 0 for ongoing
    private Player whitePlayer = null;
    private Player blackPlayer = null;
    private String gameOverReason = "";

    public ChessBlockEntity(BlockPos pos, BlockState state) {
        super(GNBlockEntities.CHESS_BE.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("game_night.games.chess.title");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ChessMenu(i, inventory, player.level().getBlockEntity(this.worldPosition));
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.board = new Board(input.getStringOr("fen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putString("fen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public String getFen() {
        return fen;
    }

    public String getSimplifiedFen() {
        String pieceLayout =  fen.split(" ")[0];
        StringBuilder simpleFen = new StringBuilder();

        for (char c : pieceLayout.toCharArray()) {
            if (Character.isDigit(c)) {
                int i = Character.getNumericValue(c);
                simpleFen.repeat(" ", i);
            } else {
                simpleFen.append(c);
            }
        }

        return simpleFen.toString();
    }

    public void setFen(String fen) {
        this.fen = fen;
        setChanged();
    }

    public boolean getIsSetup() {
        return isSetup;
    }

    public void setIsSetup(boolean isSetup) {
        this.isSetup = isSetup;
        setChanged();
    }

    public long getTimeControlSeconds() {
        return timeControlSeconds;
    }

    public void setTimeControlSeconds(long timeControlSeconds) {
        this.timeControlSeconds = timeControlSeconds;
        setChanged();
    }

    public long getIncrementSeconds() {
        return incrementSeconds;
    }

    public void setIncrementSeconds(long incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
        setChanged();
    }

    public long getWhiteTimeMs() {
        return whiteTimeMs;
    }

    public void setWhiteTimeMs(long whiteTimeMs) {
        this.whiteTimeMs = whiteTimeMs;
        setChanged();
    }

    public long getBlackTimeMs() {
        return blackTimeMs;
    }

    public void setBlackTimeMs(long blackTimeMs) {
        this.blackTimeMs = blackTimeMs;
        setChanged();
    }

    public long getLastMoveTimestamp() {
        return lastMoveTimestamp;
    }

    public void setLastMoveTimestamp(long lastMoveTimestamp) {
        this.lastMoveTimestamp = lastMoveTimestamp;
        setChanged();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
        setChanged();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        setChanged();
    }

    public char getWinner() {
        return winner;
    }

    public void setWinner(char winner) {
        this.winner = winner;
        setChanged();
    }

    public String getGameOverReason() {
        return gameOverReason;
    }

    public void setGameOverReason(String gameOverReason) {
        this.gameOverReason = gameOverReason;
        setChanged();
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
        setChanged();
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
        setChanged();
    }
}
