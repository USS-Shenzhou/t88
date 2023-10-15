package cn.ussshenzhou.t88.util;

import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.function.Consumer;

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

    public static ShapeHelper cube16(float x0, float x1, float y0, float y1, float z0, float z1) {
        return new ShapeHelper(new Vector3f(x0, y0, z0), new Vector3f(x1, y1, z1));
    }

    public static class ShapeHelper {
        Vector3f v1;
        Vector3f v2;

        public ShapeHelper(Vector3f corner1, Vector3f corner2) {
            this.v1 = corner1;
            this.v2 = corner2;
        }

        public ShapeHelper doo(Consumer<Vector3f> doer) {
            doer.accept(v1);
            doer.accept(v2);
            return this;
        }

        public VoxelShape getShape() {
            var min = new Vector3f(v1).min(v2);
            var max = new Vector3f(v1).max(v2);
            return Block.box(min.x, min.y, min.z, max.x, max.y, max.z);
        }
    }
}
