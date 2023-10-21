package cn.ussshenzhou.t88.input;

import cn.ussshenzhou.t88.T88;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
