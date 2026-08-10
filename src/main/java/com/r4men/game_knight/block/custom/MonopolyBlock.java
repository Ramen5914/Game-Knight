package com.r4men.game_knight.block.custom;

import com.mojang.serialization.MapCodec;
import com.r4men.game_knight.block.entity.MonopolyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MonopolyBlock extends SquareBoardBlock {
    public static final MapCodec<MonopolyBlock> CODEC = simpleCodec(MonopolyBlock::new);

    public MonopolyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends SquareBoardBlock> codec() {
        return CODEC;
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MonopolyBlockEntity(pos, state);
    }
}
