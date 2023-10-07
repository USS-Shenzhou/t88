package cn.ussshenzhou.t88.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;

/**
 * @author USS_Shenzhou
 */
public class BlockUtil {

    public static <T extends Comparable<T>> T justGetProperty(@Nullable BlockState first, BlockState backup, Property<T> property) {
        if (first != null) {
            return first.getOptionalValue(property).orElse(backup.getValue(property));
        } else {
            return backup.getValue(property);
        }
    }

    public static Direction justGetFacing(@Nullable BlockState first, BlockState backup) {
        return justGetProperty(first, backup, BlockStateProperties.FACING);
    }
}
