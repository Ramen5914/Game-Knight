package com.r4men.game_night;

import com.mojang.logging.LogUtils;
import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.block.GNBlocks;
import com.r4men.game_night.common.lib.Version;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.item.GNItems;
import com.r4men.game_night.network.PacketHandler;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(GameNight.MOD_ID)
public class GameNight {
    public static final String MOD_ID = "game_night";
    public static final String NAME = "Game Night";
    public static final String LOG_TAG = '[' + NAME + ']';
    public static final Logger LOGGER = LogUtils.getLogger();
    public static GameNight instance;
    public final Version versionNumber;
    private final PacketHandler packetHandler;

    public GameNight(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;

        versionNumber = new Version(modContainer);

        modEventBus.addListener(this::commonSetup);

        GNTabs.register(modEventBus);

        GNBlocks.register(modEventBus);
        GNItems.register(modEventBus);

        GNBlockEntities.register(modEventBus);

        GNMenuTypes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, GNConfig.SERVER_SPEC);

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
