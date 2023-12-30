package cn.ussshenzhou.t88;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHooks;
import org.slf4j.Logger;

/**
 * @author USS_Shenzhou
 */
@Mod("t88")
public class T88 {
    public static final String MOD_ID = "t88";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean TEST = GameTestHooks.isGametestEnabled();

    public T88(IEventBus modEventBus) {
        //NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Welcome aboard the USS Vancouver!");
    }
}
