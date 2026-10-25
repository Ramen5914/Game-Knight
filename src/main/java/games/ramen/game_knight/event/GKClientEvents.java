package games.ramen.game_knight.event;

import games.ramen.game_knight.GameKnight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = GameKnight.MOD_ID)
public final class GKClientEvents {
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
//        GKAuth.beginVerification();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
//        GKAuth.notifyPresenceEnded();
    }
}
