package com.r4men.game_night.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class WorldUtils {
    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, BlockPos pos) {
        return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable BlockGetter world, BlockPos pos) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelReader reader) {
            if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
                return false;
            }

            return isChunkLoaded(reader, pos);
        }

        return true;
    }

    @Contract("null, _, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelAccessor accessor) {
            if (!(accessor instanceof Level level) || !level.isClientSide()) {
                return accessor.hasChunk(chunkX, chunkZ);
            }
        }
        return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    @Contract("null, _ -> null")
    public static BlockEntity getTileEntity(@Nullable BlockGetter world, BlockPos pos) {
        if (!isBlockLoaded(world, pos)) {
            return null;
        }

        return world.getBlockEntity(pos);
    }
}
