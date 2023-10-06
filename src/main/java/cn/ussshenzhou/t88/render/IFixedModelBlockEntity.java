package cn.ussshenzhou.t88.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
@OnlyIn(Dist.CLIENT)
public interface IFixedModelBlockEntity {

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    @Nullable ChunkCompileContext getCompileContext();

    /**
     * Use {@link IFixedModelBlockEntity#getBuilder(Set, ChunkBufferBuilderPack, RenderType)} instead of {@link ChunkBufferBuilderPack#builder(RenderType)} .
     */
    default void renderAdditional(Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay) {
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

    public static class ChunkCompileContext {
        @ApiStatus.Internal
        public RenderType renderType = RenderType.solid();
        @ApiStatus.Internal
        public BakedModel bakedModel = null;
        @ApiStatus.Internal
        public BlockState bakedModelBlockState = null;
        @ApiStatus.Internal
        public boolean needRenderAdditional = false;

        public ChunkCompileContext() {
        }

        public ChunkCompileContext withRenderType(RenderType renderType) {
            this.renderType = renderType;
            return this;
        }

        public ChunkCompileContext withBakedModel(BakedModel bakedModel) {
            this.bakedModel = bakedModel;
            return this;
        }

        public ChunkCompileContext withBlockState(BlockState bakedModelBlockState) {
            this.bakedModelBlockState = bakedModelBlockState;
            return this;
        }

        public ChunkCompileContext withAdditionalRender() {
            this.needRenderAdditional = true;
            return this;
        }

    }

}
