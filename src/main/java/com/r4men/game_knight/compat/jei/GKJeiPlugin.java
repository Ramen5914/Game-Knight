package com.r4men.game_knight.compat.jei;

import com.r4men.game_knight.GameKnight;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class GKJeiPlugin implements IModPlugin {
    @Override
    public @NotNull Identifier getPluginUid() {
        return GameKnight.id("jei_plugin");
    }
}
