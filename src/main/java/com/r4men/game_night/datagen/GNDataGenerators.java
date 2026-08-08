package com.r4men.game_night.datagen;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.datagen.block.GNBlockLootSubProvider;
import com.r4men.game_night.datagen.block.GNBlockTagsProvider;
import com.r4men.game_night.datagen.lang.GNEnUsLanguageProvider;
import com.r4men.game_night.datagen.models.GNModelProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = GameNight.MOD_ID)
public class GNDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(GNModelProvider::new);
        event.createProvider(GNEnUsLanguageProvider::new);
        event.createProvider((packOutput, lookupProvider) -> new LootTableProvider(
                packOutput,
                Set.of(),
                List.of(
                        new SubProviderEntry(
                                GNBlockLootSubProvider::new,
                                LootContextParamSets.BLOCK
                        )
                ),
                lookupProvider
        ));
        event.createProvider(GNBlockTagsProvider::new);
    }
}
