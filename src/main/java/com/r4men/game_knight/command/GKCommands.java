package com.r4men.game_knight.command;

import com.mojang.brigadier.Command;
import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.client.auth.CredentialStorage;
import com.r4men.game_knight.client.auth.LichessAccountClient;
import com.r4men.game_knight.client.auth.LichessCallbackServer;
import com.r4men.game_knight.client.auth.LichessOAuth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.net.URI;

@EventBusSubscriber(modid = GameKnight.MOD_ID)
public final class GKCommands {
    private GKCommands() {
    }

    @SubscribeEvent
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