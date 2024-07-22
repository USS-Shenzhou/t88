package cn.ussshenzhou.t88.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
public class InventoryHelper {

    public static List<ItemStack> getAllAsList(Inventory inventory) {
        return Stream.of(inventory.items, inventory.armor, inventory.offhand)
                .flatMap(List::stream)
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();
    }

    public static Stream<ItemStack> getAllAsStream(Inventory inventory) {
        return Stream.of(inventory.items, inventory.armor, inventory.offhand)
                .flatMap(List::stream)
                .filter(itemStack -> !itemStack.isEmpty());
    }

    public static boolean consume(Inventory inventory, ItemStack need) {
        if (inventory.countItem(need.getItem()) >= need.getCount()) {
            var n = need.getCount();
            var list = getAllAsList(inventory);
            for (ItemStack item : list) {
                if (item.getItem() == need.getItem()) {
                    if (item.getCount() >= n) {
                        item.shrink(n);
                        break;
                    } else {
                        n -= item.getCount();
                        item.setCount(0);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean consumeExact(Inventory inventory, ItemStack need) {
        var o = getAllAsStream(inventory)
                .filter(itemStack -> ItemStack.isSameItemSameComponents(itemStack, need))
                .findFirst();
        if (o.isPresent()) {
            o.get().setCount(0);
            return true;
        }
        return false;
    }
}
