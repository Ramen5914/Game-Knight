package com.r4men.game_night.block.custom;

import com.mojang.serialization.MapCodec;
import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.network.to_server.PacketOpenChessScreenRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChessBlock extends SquareBoardBlock {
    public static final MapCodec<ChessBlock> CODEC = simpleCodec(ChessBlock::new);

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
        if (level.isClientSide()) {
            ClientPacketDistributor.sendToServer(new PacketOpenChessScreenRequest(pos));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, GNBlockEntities.CHESS_BE.get(), ChessBlockEntity::tick);
    }
}
