package cn.ussshenzhou.t88.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
public interface IFixedModelBlockEntity {

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    ChunkCompileContext handleCompileContext(ChunkCompileContext context);

    /**
     * Use {@link IFixedModelBlockEntity#getBuilder(Set, ChunkBufferBuilderPack, RenderType)} instead of {@link ChunkBufferBuilderPack#builder(RenderType)} .
     */
    default void renderAdditional(ChunkCompileContext context, Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay) {
    }

    default int getPackedLight() {
        if (self().getLevel() != null) {
            return LevelRenderer.getLightColor(self().getLevel(), self().getBlockPos());
        } else {
            return 0;
        }
    }

    default BufferBuilder getBuilder(Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack bufferBuilderPack, RenderType type) {
        var map = bufferBuilderPack.builders;
        var builder = map.get(type);
        if (builder == null) {
            builder = new BufferBuilder(type.bufferSize());
            map.put(type, builder);
        }
        if (begunRenderTypes.add(type)) {
            builder.begin(type.mode(), type.format());
        }
        return builder;
    }

    default SimpleMultiBufferSource getSimpleMultiBufferSource(Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack bufferBuilderPack, RenderType... types) {
        var r = SimpleMultiBufferSource.of(types[0], getBuilder(begunRenderTypes, bufferBuilderPack, types[0]));
        for (int i = 1; i < types.length; i++) {
            r.put(types[i], getBuilder(begunRenderTypes, bufferBuilderPack, types[i]));
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
