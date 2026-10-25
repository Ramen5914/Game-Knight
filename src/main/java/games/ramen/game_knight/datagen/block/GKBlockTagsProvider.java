package games.ramen.game_knight.datagen.block;

import games.ramen.game_knight.GameKnight;
import games.ramen.game_knight.block.GKBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GKBlockTagsProvider extends BlockTagsProvider {
    public GKBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, GameKnight.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                //? if <26.2 {
                /*.add(GKBlocks.CHESS.get())
                .add(GKBlocks.MONOPOLY.get());
                *///?} else if <=26.2 {
                .add(GKBlocks.CHESS.getKey())
                .add(GKBlocks.MONOPOLY.getKey());
                //? else {
                /*.add(GKBlocks.CHESS.key())
                .add(GKBlocks.MONOPOLY.key());
                *///?}
    }
}
