package com.r4men.game_night.gui;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GNMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, GameNight.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ChessMenu>> CHESS_MENU =
            MENUS.register("chess_menu", () -> IMenuTypeExtension.create((windowId, inv, extraData) -> new ChessMenu(windowId, inv, extraData)));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
