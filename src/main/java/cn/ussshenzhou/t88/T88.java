package cn.ussshenzhou.t88;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcherBlacklist;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import org.slf4j.Logger;

import java.util.concurrent.ForkJoinPool;

/**
 * @author USS_Shenzhou
 */
@Mod("t88")
public class T88 {
    public static final String MOD_ID = "t88";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean TEST = GameTestHooks.isGametestEnabled();
    public static final boolean SODIUM_EXIST = ModList.get().isLoaded("sodium");

    public T88(IEventBus modEventBus) {
        //NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::setup);
        ConfigHelper.loadConfig(new NetworkWatcherBlacklist());
        if (System.getProperty("t88.skip_parallelism_check") == null) {
            if (ForkJoinPool.getCommonPoolParallelism() == 1) {
                throw new RuntimeException("ForkJoinPool.getCommonPoolParallelism() should NOT be 1." +
                        " This may cause serious performance problem in Network Watcher." +
                        " If your machine has only 1 CPU core or running in a virtual environment," +
                        " you can manually designate it to be bigger than 1 by using JVM argument -Djava.util.concurrent.ForkJoinPool.common.parallelism=2." +
                        " You can also use -Dt88.skip_parallelism_check=true to skip this check.");
            }
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Welcome aboard the USS Vancouver!");
    }
}
