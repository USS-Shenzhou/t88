package cn.ussshenzhou.t88.analyzer;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.analyzer.back.T88AnalyzerClient;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TestListener {

    @SubscribeEvent
    public static void recordTick(TickEvent.ClientTickEvent event) {
        if (T88.TEST) {
            if (event.phase == TickEvent.Phase.END) {
                T88AnalyzerClient.record("FPS", Minecraft.getInstance().getFps());
                T88AnalyzerClient.record("random", Math.random() * 1000);
            }
        }
    }
}
