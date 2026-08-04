package com.r4men.game_night.datagen.block;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GNBlockTagsProvider extends BlockTagsProvider {
    public GNBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, GameNight.ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider lookupProvider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(GNBlocks.CHESS.get())
                .add(GNBlocks.MONOPOLY.get());
    }
}
