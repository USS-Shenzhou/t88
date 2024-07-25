package cn.ussshenzhou.t88.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
public interface IFixedModelBlockEntity {

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    @Nullable
    SectionCompileContext handleCompileContext(SectionCompileContext rawContext);

    default void renderAdditionalAsync(SectionCompileContext context,PoseStack poseStack) {
    }

    default int getPackedLight() {
        if (self().getLevel() != null) {
            return LevelRenderer.getLightColor(self().getLevel(), self().getBlockPos());
        } else {
            return 0;
        }
    }

    default BufferBuilder getBuilder(SectionCompileContext context, RenderType type) {
        return context.sectionCompiler.getOrBeginLayer(context.bufferBuilders, context.sectionBufferBuilderPack, type);
    }

    default SimpleMultiBufferSource getSimpleMultiBufferSource(SectionCompileContext context, RenderType... types) {
        var r = SimpleMultiBufferSource.of(types[0], getBuilder(context, types[0]));
        for (int i = 1; i < types.length; i++) {
            r.put(types[i], getBuilder(context, types[i]));
        }
        return r;
    }

    static boolean isBasicRenderType(RenderType renderType) {
        return renderType == RenderType.solid()
                || renderType == RenderType.cutout()
                || renderType == RenderType.translucent()
                || renderType == RenderType.cutoutMipped()
                || renderType == RenderType.tripwire();
    }

    default void resetToBlock000(RenderType renderType, PoseStack poseStack) {
        resetToBlock000(self().getBlockPos(), renderType, poseStack);
    }

    //TODO may need camera compensate
    static void resetToBlock000(BlockPos pos, RenderType renderType, PoseStack poseStack) {
        poseStack.setIdentity();
        if (isBasicRenderType(renderType)) {
            poseStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        } else {
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

}
