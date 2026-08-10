package com.r4men.game_knight.block.entity;

import com.r4men.game_knight.block.GKBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MonopolyBlockEntity extends BlockEntity {
    public MonopolyBlockEntity(BlockPos pos, BlockState blockState) {
        super(GKBlockEntities.MONOPOLY_BE.get(), pos, blockState);
    }
}
