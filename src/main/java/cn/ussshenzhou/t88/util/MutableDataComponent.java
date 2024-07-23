package cn.ussshenzhou.t88.util;

import cn.ussshenzhou.t88.T88;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * @author USS_Shenzhou
 */
public interface MutableDataComponent<T> {

    DataComponentType<T> componentType();

    default <Field> void setFirst(ItemStack stack, Field value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getType() == value.getClass())
                    .findFirst().get();
            field.setAccessible(true);
            field.set(this, value);
        } catch (IllegalAccessException | NoSuchElementException e) {
            handleException(e);
        }
        //noinspection unchecked
        stack.set(componentType(), (T) this);
    }

    default <Field> void set(ItemStack stack, Field value, int ordinal) {
        try {
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getType() == value.getClass())
                    .toList().get(ordinal);
            field.setAccessible(true);
            field.set(this, value);
        } catch (IllegalAccessException | IndexOutOfBoundsException e) {
            handleException(e);
        }
        //noinspection unchecked
        stack.set(componentType(), (T) this);
    }

    default <Field> void setByName(ItemStack stack, String fieldName, Field value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var field = Arrays.stream(this.getClass().getDeclaredFields())
                    .filter(f -> f.getName().equals(fieldName))
                    .findFirst().get();
            field.setAccessible(true);
            field.set(this, value);
        } catch (IllegalAccessException | NoSuchElementException e) {
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