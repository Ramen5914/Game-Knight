package com.r4men.game_knight;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.Command;
import com.r4men.game_knight.block.GKBlockEntities;
import com.r4men.game_knight.client.auth.CredentialStorage;
import com.r4men.game_knight.client.auth.LichessAccountClient;
import com.r4men.game_knight.client.auth.LichessCallbackServer;
import com.r4men.game_knight.client.auth.LichessOAuth;
import com.r4men.game_knight.client.renderer.ChessBlockEntityRenderer;
import com.r4men.game_knight.util.GKKeyConflictContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.net.URI;

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
        modEventBus.addListener(GameKnightClient::registerClientCommands);
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

    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("gk")
                        .then(Commands.literal("link")
                                .then(Commands.literal("lichess")
                                        .executes(context -> {
                                            LocalPlayer player = Minecraft.getInstance().player;

                                            if (player == null) {
                                                context.getSource().sendFailure(
                                                        Component.literal(
                                                                "You must be in a world to link Lichess."
                                                        )
                                                );
                                                return 0;
                                            }

                                            try {
                                                // Must be listening before Lichess redirects the browser back.
                                                LichessCallbackServer.start();

                                                URI authorizationUri = LichessOAuth.begin(
                                                        player.getUUID(),
                                                        player.getGameProfile().name()
                                                );

                                                requestOpenBrowser(authorizationUri);

                                                context.getSource().sendSuccess(
                                                        () -> Component.literal(
                                                                "Opening your browser to link your Lichess account..."
                                                        ),
                                                        false
                                                );

                                                return Command.SINGLE_SUCCESS;
                                            } catch (Exception exception) {
                                                GameKnight.LOGGER.error(
                                                        "Could not begin Lichess OAuth authorization.",
                                                        exception
                                                );

                                                context.getSource().sendFailure(
                                                        Component.literal(
                                                                "Could not start Lichess authorization. Check the game log."
                                                        )
                                                );

                                                return 0;
                                            }
                                        })
                                )
                        )
                        .then(Commands.literal("status")
                                .then(Commands.literal("lichess")
                                        .executes(context -> {
                                            LocalPlayer player = Minecraft.getInstance().player;

                                            if (player == null) {
                                                context.getSource().sendFailure(
                                                        Component.literal(
                                                                "You must be in a world to check Lichess status."
                                                        )
                                                );
                                                return 0;
                                            }

                                            try {
                                                CredentialStorage credentialStorage = new CredentialStorage();
                                                String token = credentialStorage.loadLichessToken();

                                                if (token == null || token.isBlank()) {
                                                    context.getSource().sendFailure(
                                                            Component.literal(
                                                                    "No Lichess token is stored. Run /gk link lichess first."
                                                            )
                                                    );
                                                    return 0;
                                                }

                                                LichessAccountClient.LichessAccountStatus status =
                                                        LichessAccountClient.fetchAccountStatus(token);

                                                String rapidText = status.rapidRating() >= 0
                                                        ? Integer.toString(status.rapidRating())
                                                        : "unrated";

                                                context.getSource().sendSuccess(
                                                        () -> Component.literal(
                                                                "Lichess linked as "
                                                                        + status.username()
                                                                        + " (Rapid: "
                                                                        + rapidText
                                                                        + ")"
                                                        ),
                                                        false
                                                );
                                                return Command.SINGLE_SUCCESS;
                                            } catch (Exception exception) {
                                                GameKnight.LOGGER.error(
                                                        "Could not check Lichess status.",
                                                        exception
                                                );
                                                context.getSource().sendFailure(
                                                        Component.literal(
                                                                "Could not check Lichess status. The stored token may be invalid."
                                                        )
                                                );
                                                return 0;
                                            }
                                        })
                                )
                        )
        );
    }

    private static void requestOpenBrowser(URI authorizationUri) {
        Minecraft minecraft = Minecraft.getInstance();
        String shortUrl = "https://lichess.org/oauth";
        String realUrl = authorizationUri.toASCIIString();

        minecraft.setScreenAndShow(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getPlatform().openUri(authorizationUri);
            }
            minecraft.setScreenAndShow(null);
        }, shortUrl, true));

        minecraft.keyboardHandler.setClipboard(realUrl);
    }
}
