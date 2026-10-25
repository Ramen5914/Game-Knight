package games.ramen.game_knight.server;

import games.ramen.game_knight.engine.GameParticipant;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class GKServerUtil {
    public static String getDisplayName(MinecraftServer server, GameParticipant participant) {
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(participant.uuid());

        return onlinePlayer != null ? onlinePlayer.getGameProfile().name() : participant.lastKnownName();
    }
}
