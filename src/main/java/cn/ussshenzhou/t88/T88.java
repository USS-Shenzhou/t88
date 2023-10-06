package cn.ussshenzhou.t88;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.slf4j.Logger;

/**
 * @author USS_Shenzhou
 */
@Mod("t88")
public class T88 {
    public static final String MOD_ID = "t88";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean TEST = ForgeGameTestHooks.isGametestEnabled();

    public T88() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Welcome aboard the USS Vancouver!");
    }
}
