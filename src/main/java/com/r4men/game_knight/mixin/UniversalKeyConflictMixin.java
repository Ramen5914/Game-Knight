package com.r4men.game_knight.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.r4men.game_knight.gui.GKScreen;
import com.r4men.game_knight.util.GKKeyConflictContext;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Pseudo
@Mixin(targets = "net.neoforged.neoforge.client.settings.KeyConflictContext$1", remap = false)
public class UniversalKeyConflictMixin {
    @ModifyReturnValue(
            method = "isActive()Z",
            at = @At("RETURN"),
            remap = false
    )
    private boolean changeUniversalIsActive(boolean original) {
        return original && !(Minecraft.getInstance().screen instanceof GKScreen);
    }

    @ModifyReturnValue(
            method = "conflicts(Lnet/neoforged/neoforge/client/settings/IKeyConflictContext;)Z",
            at = @At("RETURN"),
            remap = false
    )
    private boolean changeUniversalConflicts(boolean original, IKeyConflictContext other) {
        return original && !(other instanceof GKKeyConflictContext);
    }
}
