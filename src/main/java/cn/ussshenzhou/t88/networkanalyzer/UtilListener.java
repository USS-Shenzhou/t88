package cn.ussshenzhou.t88.networkanalyzer;

import net.minecraft.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class UtilListener {
    static long lastUpdate = 0;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        if (Util.getMillis() - lastUpdate >= 1000) {
            lastUpdate = Util.getMillis();
            NetworkWatcher.clear();
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (Util.getMillis() - lastUpdate >= 1000) {
            lastUpdate = Util.getMillis();
            NetworkWatcher.clear();
        }
    }
}
