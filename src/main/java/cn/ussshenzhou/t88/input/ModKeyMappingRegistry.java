package cn.ussshenzhou.t88.input;

import cn.ussshenzhou.t88.T88;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappingRegistry {
    @SubscribeEvent
    public static void onRegisterKey(RegisterKeyMappingsEvent event) {
        if (T88.TEST){
            event.register(ModKeyInput.GUI_TEST);
        }
        event.register(ModKeyInput.OPEN_ANALYZER);
        event.register(ModKeyInput.CLEAR_ANALYZER);
    }
}
