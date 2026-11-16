package cn.ussshenzhou.t88.magic;

import com.mojang.logging.LogUtils;
import sun.misc.Unsafe;

import javax.annotation.CheckReturnValue;
import java.lang.reflect.*;
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
    public static <R extends Record> R setRecordField(R record, Field field, Object value) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = record.getClass();
        Constructor<? extends Record> constructor;
        try {
            //make sure to get the canonical constructor
            constructor = clazz.getDeclaredConstructor(Arrays.stream(clazz.getRecordComponents()).map(RecordComponent::getType).toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            LogUtils.getLogger().error("Failed to find constructor of " + clazz + "This should not happen.");
            throw new RuntimeException(e);
        }
        constructor.setAccessible(true);
        var parameters = Arrays.stream(clazz.getRecordComponents())
                .map(component -> {
                    if (component.getName().equals(field.getName())) {
                        return value;
                    }
                    try {
                        var componentField = clazz.getDeclaredField(component.getName());
                        componentField.setAccessible(true);
                        return componentField.get(record);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray();
        //noinspection unchecked
        return (R) constructor.newInstance(parameters);
    }

    @CheckReturnValue
    public static <R extends Record> R setRecordFields(R record, Object[] fields) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = record.getClass();
        Constructor<? extends Record> constructor;
        try {
            //make sure to get the canonical constructor
            constructor = clazz.getDeclaredConstructor(Arrays.stream(clazz.getRecordComponents()).map(RecordComponent::getType).toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            LogUtils.getLogger().error("Failed to find constructor of " + clazz + "This should not happen.");
            throw new RuntimeException(e);
        }
        //noinspection unchecked
        return (R) constructor.newInstance(fields);
    }
}
