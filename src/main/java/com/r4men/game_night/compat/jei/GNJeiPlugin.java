package com.r4men.game_night.compat.jei;

import com.r4men.game_night.GameNight;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class GNJeiPlugin implements IModPlugin {
    @Override
    public @NotNull Identifier getPluginUid() {
        return GameNight.id("jei_plugin");
    }
}
