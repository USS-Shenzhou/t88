package cn.ussshenzhou.t88.input;

import cn.ussshenzhou.t88.T88;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappingRegistry {
    @SubscribeEvent
    public static void onRegisterKey(RegisterKeyMappingsEvent event) {
        if (T88.TEST) {
            event.register(ModKeyInput.GUI_TEST);
        }
        List.of(ModKeyInput.OPEN_ANALYZER,
                ModKeyInput.CLEAR_ANALYZER,
                ModKeyInput.OPEN_WATCHER
        ).forEach(event::register);
    }
}
