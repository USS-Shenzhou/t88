package cn.ussshenzhou.t88.networkanalyzer;

import net.minecraft.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class UtilListenerC {
    static long lastUpdateC;

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (Util.getMillis() - lastUpdateC > 1000) {
            lastUpdateC = Util.getMillis();
            NetworkWatcher.clear();
        }
    }
}
