package com.r4men.game_knight.datagen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GKRecipeProvider implements DataProvider {
    public static final int BUCKET = FluidType.BUCKET_VOLUME;
    public static final int BOTTLE = BUCKET / 4;
    public static final int BLOCK = 1296;
    public static final int INGOT = BLOCK / 9;
    public static final int NUGGET = INGOT / 9;

    public GKRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull String getName() {
        return "Game Knight Recipes";
    }
}
