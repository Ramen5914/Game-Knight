package games.ramen.game_knight.datagen.block;

import games.ramen.game_knight.block.GKBlocks;
//? if <=26.2
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
//? if 26.3
//import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GKBlockLootSubProvider extends BlockLootSubProvider {
    //? if <=26.2
    public GKBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
    //? else
    //public GKBlockLootSubProvider(LootTableSubProvider.Context context) {
        //? if <=26.2
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
        //? else
        //super(Set.of(), FeatureFlags.DEFAULT_FLAGS, context);
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
