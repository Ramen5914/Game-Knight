package com.r4men.game_night.gui.menu;

import com.r4men.game_night.block.GNBlocks;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.gui.GNMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChessMenu2 extends AbstractContainerMenu {
    private final ChessBlockEntity blockEntity;
    ContainerLevelAccess access;

    // Client Constructor
    public ChessMenu2(int containerId, FriendlyByteBuf extraData) {
        assert Minecraft.getInstance().level != null;
        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());

        this(containerId, ContainerLevelAccess.NULL, be);
    }

    // Server Constructor
    public ChessMenu2(int containerId, ContainerLevelAccess access, BlockEntity be) {
        super(GNMenuTypes.CHESS_MENU_2.get(), containerId);

        this.access = access;

        if (be instanceof ChessBlockEntity chessBe) {
            this.blockEntity = chessBe;
        } else {
            throw new IllegalStateException("ChessMenu opened without a ChessBlockEntity");
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, GNBlocks.CHESS.get());
    }

    public String getSimplifiedFen() {
        return blockEntity.getSimplifiedFen();
    }

    public List<Integer> getLegalMovesForPiece(int piece) {
        return blockEntity.get8x8MovesForPiece(piece);
    }

    public void makeMove(int from8x8, int to8x8) {
        blockEntity.makeMove(from8x8, to8x8);
    }
}
