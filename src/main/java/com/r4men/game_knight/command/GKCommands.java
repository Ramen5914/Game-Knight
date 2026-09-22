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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.net.URI;

@EventBusSubscriber(modid = GameKnight.MOD_ID)
public final class GKCommands {
    private GKCommands() {

    }
}