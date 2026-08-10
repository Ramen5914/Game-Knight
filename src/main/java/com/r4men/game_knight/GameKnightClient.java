package com.r4men.game_knight;

import com.mojang.blaze3d.platform.InputConstants;
import com.r4men.game_knight.block.GKBlockEntities;
import com.r4men.game_knight.client.renderer.ChessBlockEntityRenderer;
import com.r4men.game_knight.util.GKKeyConflictContext;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@Mod(value = GameKnight.MOD_ID, dist = Dist.CLIENT)
public class GameKnightClient {
    public static final KeyMapping.Category CHESS_CATEGORY = new KeyMapping.Category(GameKnight.id("chess"));
    public static final Lazy<KeyMapping> FLIP_BOARD = Lazy.of(() -> new KeyMapping(
            "key.game_knight.flip_board",
            GKKeyConflictContext.CHESS,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            CHESS_CATEGORY
    ));

    public GameKnightClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modContainer.registerConfig(ModConfig.Type.CLIENT, GKConfig.CLIENT_SPEC);

        // Register mod bus events
        modEventBus.addListener(GameKnightClient::registerMenuScreens);
        modEventBus.addListener(GameKnightClient::registerBER);
        modEventBus.addListener(GameKnightClient::registerBindings);
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
//        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
    }

    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GKBlockEntities.CHESS_BE.get(), ChessBlockEntityRenderer::create);
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CHESS_CATEGORY);
        event.register(FLIP_BOARD.get());
    }
}
