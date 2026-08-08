package com.r4men.game_night.item;

import com.r4men.game_night.GameNight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GNItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GameNight.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
