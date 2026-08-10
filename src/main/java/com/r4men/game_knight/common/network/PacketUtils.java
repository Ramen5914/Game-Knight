package com.r4men.game_knight.common.network;

import com.r4men.game_knight.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public class PacketUtils {
    private PacketUtils() {}

    @Nullable
    public static BlockEntity blockEntity(IPayloadContext context, BlockPos pos) {
        return WorldUtils.getTileEntity(context.player().level(), pos);
    }
}
