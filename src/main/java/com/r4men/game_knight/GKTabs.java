package com.r4men.game_knight;

import com.r4men.game_knight.block.GKBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GKTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GameKnight.MOD_ID);

    public static final Supplier<CreativeModeTab> GAME_KNIGHT_TAB = CREATIVE_MODE_TABS.register(
            "gk_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.game_knight.gk_tab"))
                    .icon(() -> new ItemStack(GKBlocks.CHESS.get()))
                    .displayItems((params, output) -> {
                        output.accept(GKBlocks.CHESS.get());
                        output.accept(GKBlocks.MONOPOLY.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
