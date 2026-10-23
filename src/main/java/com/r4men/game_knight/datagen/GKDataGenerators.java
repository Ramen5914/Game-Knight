package com.r4men.game_knight.datagen;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.datagen.block.GKBlockLootSubProvider;
import com.r4men.game_knight.datagen.block.GKBlockTagsProvider;
import com.r4men.game_knight.datagen.lang.GKEnUsLanguageProvider;
import com.r4men.game_knight.datagen.models.GKModelProvider;
//? if <=26.2
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
//? if 26.3
//import net.minecraft.util.context.ContextKeySet;
//? if <26.3
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
//?if 26.3
//import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

//? if <=26.2 {
import java.util.List;
import java.util.Set;
//?}

@EventBusSubscriber(modid = GameKnight.MOD_ID)
public class GKDataGenerators {
    //? if 26.3 {
    /*@SubscribeEvent
    public static void t(GatherDataRegistryEntriesEvent event) {
        event.lootTable(new SubProviderEntry(
                GKBlockLootSubProvider::new,
                ContextKeySet.EMPTY
        ));
    }
    *///?}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(GKModelProvider::new);
        event.createProvider(GKEnUsLanguageProvider::new);
        //? if <=26.2 {
        event.createProvider((packOutput, lookupProvider) -> new LootTableProvider(
                packOutput,
                Set.of(),
                List.of(
                        new SubProviderEntry(
                                GKBlockLootSubProvider::new,
                                LootContextParamSets.BLOCK
                        )
                ),
                lookupProvider
        ));
        //?}
        event.createProvider(GKBlockTagsProvider::new);
    }
}
