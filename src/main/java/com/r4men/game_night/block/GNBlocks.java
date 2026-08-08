package com.r4men.game_night.block;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.custom.ChessBlock;
import com.r4men.game_night.block.custom.MonopolyBlock;
import com.r4men.game_night.item.GNItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class GNBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GameNight.MOD_ID);

    public static final DeferredBlock<Block> CHESS = registerBlock("chess", ChessBlock::new, BlockBehaviour.Properties::noOcclusion);

    public static final DeferredBlock<Block> MONOPOLY = registerBlock("monopoly", MonopolyBlock::new, BlockBehaviour.Properties::noOcclusion);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, properties);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        GNItems.ITEMS.registerSimpleBlockItem(name, block, Item.Properties::useBlockDescriptionPrefix);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
