package cn.ussshenzhou.t88.render.fixedblockentity;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author USS_Shenzhou
 */
public interface IFixedModelBlockEntity {

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    SectionCompileContext handleCompileContext(SectionCompileContext rawContext);

    @OnlyIn(Dist.CLIENT)
    default void renderAdditionalAsync(SectionCompileContext context, PoseStack poseStack) {
    }

    @OnlyIn(Dist.CLIENT)
    default int getPackedLight() {
        if (self().getLevel() != null) {
            return LevelRenderer.getLightColor(self().getLevel(), self().getBlockPos());
        } else {
            return 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static boolean isBasicRenderType(RenderType renderType) {
        return renderType == RenderType.solid()
                || renderType == RenderType.cutout()
                || renderType == RenderType.translucent()
                || renderType == RenderType.cutoutMipped()
                || renderType == RenderType.tripwire();
    }

    @OnlyIn(Dist.CLIENT)
    default void resetToBlock000(RenderType renderType, PoseStack poseStack) {
        resetToBlock000(self().getBlockPos(), renderType, poseStack);
    }

    @OnlyIn(Dist.CLIENT)
    static void resetToBlock000(BlockPos pos, RenderType renderType, PoseStack poseStack) {
        poseStack.setIdentity();
        if (isBasicRenderType(renderType)) {
            poseStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        } else {
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

}
