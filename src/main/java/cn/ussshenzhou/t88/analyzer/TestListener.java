package cn.ussshenzhou.t88.analyzer;

import cn.ussshenzhou.t88.analyzer.back.T88AnalyzerClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TestListener {

    @SubscribeEvent
    public static void recordTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            T88AnalyzerClient.record("FPS", Minecraft.getInstance().getFps());
        }
    }
}
