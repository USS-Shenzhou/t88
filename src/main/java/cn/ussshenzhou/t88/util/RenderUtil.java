package cn.ussshenzhou.t88.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;

/**
 * @author USS_Shenzhou
 */
public class RenderUtil {

    public static int getPackedLight(int sourceLight, BlockAndTintGetter level, BlockPos pos) {
        return getPackedLight(sourceLight, LevelRenderer.getLightColor(level, pos));
    }

    public static int getPackedLight(int sourceLight, int packedLight) {
        return (packedLight & 0b00000000_00000000_00000000_11110000) > sourceLight ? packedLight : (packedLight & 0b00000000_11110000_00000000_00000000 | sourceLight << 4);
    }

    public static void rotateAroundBlockCenter(Direction direction, PoseStack poseStack) {
        switch (direction) {
            case NORTH -> {
            }
            case SOUTH -> poseStack.rotateAround(Axis.YP.rotation((float) Math.PI), 0.5f, 0.5f, 0.5f);
            case EAST -> poseStack.rotateAround(Axis.YP.rotation((float) -Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case WEST -> poseStack.rotateAround(Axis.YP.rotation((float) Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case UP -> poseStack.rotateAround(Axis.XP.rotation((float) -Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case DOWN -> poseStack.rotateAround(Axis.XP.rotation((float) Math.PI / 2), 0.5f, 0.5f, 0.5f);
        }
    }

}
