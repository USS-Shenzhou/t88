package cn.ussshenzhou.t88.networkanalyzer;

import net.minecraft.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
    static long lastUpdateC, lastUpdateS;

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Pre event) {
        if (Util.getMillis() - lastUpdateS > 1000) {
            lastUpdateS = Util.getMillis();
            NetworkWatcher.clear();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (Util.getMillis() - lastUpdateC > 1000) {
            lastUpdateC = Util.getMillis();
            NetworkWatcher.clear();
        }
    }
}
