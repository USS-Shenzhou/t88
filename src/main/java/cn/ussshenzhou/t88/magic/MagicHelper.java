package cn.ussshenzhou.t88.magic;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import sun.misc.Unsafe;

/**
 * @author USS_Shenzhou
 */
public class MagicHelper {
    public static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LogUtils.getLogger().error("Failed to initialize UnSafe. This should not happen.");
            throw new RuntimeException(e);
        }
    }
}
