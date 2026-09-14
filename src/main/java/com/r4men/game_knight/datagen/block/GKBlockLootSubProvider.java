package com.r4men.game_knight.datagen.block;

import com.r4men.game_knight.block.GKBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GKBlockLootSubProvider extends BlockLootSubProvider {
    public GKBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(GKBlocks.CHESS.get());
        this.dropSelf(GKBlocks.MONOPOLY.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return GKBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }
}
