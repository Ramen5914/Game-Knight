package com.r4men.game_knight.block;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.block.entity.ChessBlockEntity;
import com.r4men.game_knight.block.entity.MonopolyBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GKBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GameKnight.MOD_ID);

    public static final Supplier<BlockEntityType<ChessBlockEntity>> CHESS_BE =
            BLOCK_ENTITIES.register("chess_be", () -> new BlockEntityType<>(
                    ChessBlockEntity::new, GKBlocks.CHESS.get()));

    public static final Supplier<BlockEntityType<MonopolyBlockEntity>> MONOPOLY_BE =
            BLOCK_ENTITIES.register("monopoly_be", () -> new BlockEntityType<>(
                    MonopolyBlockEntity::new, GKBlocks.MONOPOLY.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
