package cn.ussshenzhou.t88.analyzer.back;

import net.minecraft.Util;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UtilListener {
    static long lastUpdate = 0;

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Util.getMillis() - lastUpdate >= 1000) {
                lastUpdate = Util.getMillis();
                NetworkWatcher.clear();
            }
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Util.getMillis() - lastUpdate >= 1000) {
                lastUpdate = Util.getMillis();
                NetworkWatcher.clear();
            }
        }
    }
}
