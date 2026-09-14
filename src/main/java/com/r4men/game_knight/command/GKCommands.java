package com.r4men.game_knight.command;

import com.mojang.brigadier.Command;
import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.client.auth.LichessCallbackServer;
import com.r4men.game_knight.client.auth.LichessOAuth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
        );
    }

    private static void requestOpenBrowser(URI authorizationUri) {
        Minecraft minecraft = Minecraft.getInstance();
        String url = authorizationUri.toASCIIString();

        minecraft.setScreenAndShow(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                ConfirmLinkScreen.confirmLinkNow(null, url);
            }
        }, url, true));
    }
}