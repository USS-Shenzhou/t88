package cn.ussshenzhou.t88.magic;

import cn.ussshenzhou.t88.T88;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author USS_Shenzhou
 */
public interface MutableDataComponent<R extends Record> {

    DataComponentType<R> componentType();

    @SuppressWarnings("unchecked")
    default <F> MutableDataComponent<R> setFirst(ItemStack stack, F value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent") var field = Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getType() == value.getClass()).findFirst().get();
            R r = (R) MagicHelper.set((Record) this, field, value);
            stack.set(componentType(), r);
            return (MutableDataComponent<R>) r;
        } catch (NoSuchElementException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    default <F> MutableDataComponent<R> set(ItemStack stack, F value, int ordinal) {
        try {
            var field = Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getType() == value.getClass()).toList().get(ordinal);
            R r = (R) MagicHelper.set((Record) this, field, value);
            stack.set(componentType(), r);
            return (MutableDataComponent<R>) r;
        } catch (NoSuchElementException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    default <F> MutableDataComponent<R> setByName(ItemStack stack, String fieldName, F value) {
        try {
            @SuppressWarnings("OptionalGetWithoutIsPresent") var field = Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getName().equals(fieldName)).findFirst().get();
            R r = (R) MagicHelper.set((Record) this, field, value);
            stack.set(componentType(), r);
            return (MutableDataComponent<R>) r;
        } catch (NoSuchElementException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
