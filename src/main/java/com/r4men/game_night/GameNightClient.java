package com.r4men.game_night;

import com.mojang.blaze3d.platform.InputConstants;
import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.client.renderer.ChessBlockEntityRenderer;
import com.r4men.game_night.engine.chess.type.Move;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.gui.menu.ChessMenu2;

import com.r4men.game_night.gui.screen.ChessScreen;
import com.r4men.game_night.gui.screen.ChessScreen2;
import com.r4men.game_night.util.GNKeyConflictContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@Mod(value = GameNight.ID, dist = Dist.CLIENT)
public class GameNightClient {
    public GameNightClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modContainer.registerConfig(ModConfig.Type.CLIENT, GNConfig.CLIENT_SPEC);

        // Register mod bus events
        modEventBus.addListener(GameNightClient::registerScreens);
        modEventBus.addListener(GameNightClient::registerBER);
        modEventBus.addListener(GameNightClient::registerBindings);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
        event.register(GNMenuTypes.CHESS_MENU_2.get(), (ChessMenu2 menu, Inventory inventory, Component title) -> new ChessScreen2(menu, title));
    }

    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GNBlockEntities.CHESS_BE.get(), ChessBlockEntityRenderer::create);
    }

    public static final KeyMapping.Category CHESS_CATEGORY = new KeyMapping.Category(GameNight.getIdentifier("chess"));
    public static final Lazy<KeyMapping> FLIP_BOARD = Lazy.of(() -> new KeyMapping(
            "key.game_night.flip_board",
            GNKeyConflictContext.CHESS,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            CHESS_CATEGORY
    ));

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CHESS_CATEGORY);
        event.register(FLIP_BOARD.get());
    }
}
