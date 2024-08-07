package cn.ussshenzhou.t88.util;

import cn.ussshenzhou.t88.T88;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author USS_Shenzhou
 */
public interface MutableDataComponent<T> {

    DataComponentType<T> componentType();

    default <F> void setFirst(ItemStack stack, F value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getType() == value.getClass())
                    .findFirst().get();
            setValue(field, value);
        } catch (IllegalAccessException | NoSuchElementException | NoSuchFieldException e) {
            handleException(e);
        }
        //noinspection unchecked
        stack.set(componentType(), (T) this);
    }

    private <F> void setValue(Field field, F value) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        int m = field.getModifiers();
        modifiers.setInt(field, m & ~Modifier.FINAL);
        field.set(this, value);
        modifiers.setInt(field, m);
    }

    default <F> void set(ItemStack stack, F value, int ordinal) {
        try {
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getType() == value.getClass())
                    .toList().get(ordinal);
            setValue(field, value);
        } catch (IllegalAccessException | IndexOutOfBoundsException | NoSuchFieldException e) {
            handleException(e);
        }
        //noinspection unchecked
        stack.set(componentType(), (T) this);
    }

    default <F> void setByName(ItemStack stack, String fieldName, F value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals(fieldName))
                    .findFirst().get();
            setValue(field, value);
        } catch (IllegalAccessException | NoSuchElementException | NoSuchFieldException e) {
            handleException(e);
        }
        //noinspection unchecked
        stack.set(componentType(), (T) this);
    }

    private static void handleException(Exception e) {
        if (T88.TEST) {
            throw new RuntimeException(e);
        } else {
            LogUtils.getLogger().error(e.getMessage());
        }
    }
}
