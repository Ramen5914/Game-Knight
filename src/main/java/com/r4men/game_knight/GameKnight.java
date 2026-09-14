package com.r4men.game_knight;

import com.mojang.logging.LogUtils;
import com.r4men.game_knight.block.GKBlockEntities;
import com.r4men.game_knight.block.GKBlocks;
import com.r4men.game_knight.common.lib.Version;
import com.r4men.game_knight.gui.GKMenuTypes;
import com.r4men.game_knight.item.GKItems;
import com.r4men.game_knight.network.PacketHandler;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(GameKnight.MOD_ID)
public class GameKnight {
    public static final String MOD_ID = "game_knight";
    public static final String NAME = "Game Knight";
    public static final String LOG_TAG = '[' + NAME + ']';
    public static final Logger LOGGER = LogUtils.getLogger();
    public static GameKnight instance;
    public final Version versionNumber;
    private final PacketHandler packetHandler;

    public GameKnight(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;

        versionNumber = new Version(modContainer);

        modEventBus.addListener(this::commonSetup);

        GKTabs.register(modEventBus);

        GKBlocks.register(modEventBus);
        GKItems.register(modEventBus);

        GKBlockEntities.register(modEventBus);

        GKMenuTypes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, GKConfig.SERVER_SPEC);

        packetHandler = new PacketHandler(modEventBus, versionNumber);
    }

    public static PacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static Identifier id(String regName) {
        return Identifier.fromNamespaceAndPath(MOD_ID, regName);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("{} initializing!", NAME);
    }
}
