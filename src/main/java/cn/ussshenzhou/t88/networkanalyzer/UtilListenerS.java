package cn.ussshenzhou.t88.networkanalyzer;

import net.minecraft.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber
public class UtilListenerS {
    static long lastUpdateS;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        if (Util.getMillis() - lastUpdateS > 1000) {
            lastUpdateS = Util.getMillis();
            NetworkWatcher.clear();
        }
    }
}
