package com.r4men.game_night.block.custom;

import com.ibm.icu.text.MessagePatternUtil;
import com.mojang.serialization.MapCodec;
import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.gui.menu.ChessMenu;
import com.r4men.game_night.gui.menu.ChessMenu2;
import com.r4men.game_night.network.ChessData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChessBlock extends SquareBoardBlock {
    public static final MapCodec<ChessBlock> CODEC = simpleCodec(ChessBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("game_night.games.chess.title");
    
    public ChessBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends SquareBoardBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ChessBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ChessData("Ramen5914", 20));

            serverPlayer.openMenu(getMenuProvider(state, level, pos), buf -> buf.writeBlockPos(pos));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, GNBlockEntities.CHESS_BE.get(), ChessBlockEntity::tick);
    }
}
