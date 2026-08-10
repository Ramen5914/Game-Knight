package com.r4men.game_knight.client.renderer;

import com.google.common.base.Suppliers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class RenderHelper {
    public static Supplier<ItemStack> memoizeStackModel(Identifier modelId) {
        return Suppliers.memoize(() -> {
            ItemStack stack = new ItemStack(Items.STICK);
            stack.set(DataComponents.ITEM_MODEL, modelId);
            return stack;
        });
    }
}
