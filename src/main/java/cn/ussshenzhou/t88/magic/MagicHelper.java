package cn.ussshenzhou.t88.magic;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import sun.misc.Unsafe;

import javax.annotation.CheckReturnValue;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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

    @CheckReturnValue
    public static <R extends Record> R set(R record, Field field, Object value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = record.getClass();
        var constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        var parameters = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(f -> {
                    if (f.equals(field)) {
                        return value;
                    } else {
                        try {
                            f.setAccessible(true);
                            return f.get(record);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).toArray();
        //noinspection unchecked
        return (R) constructor.newInstance(parameters);
    }
}
