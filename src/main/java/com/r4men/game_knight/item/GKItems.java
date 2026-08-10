package com.r4men.game_knight.item;

import com.r4men.game_knight.GameKnight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GKItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GameKnight.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
