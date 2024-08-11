package cn.ussshenzhou.t88.render;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
@OnlyIn(Dist.CLIENT)
public class SectionCompileContext {
    public final BlockAndTintGetter level;
    public final SectionCompiler sectionCompiler;
    public final Map<RenderType, BufferBuilder> bufferBuilders;
    public final SectionBufferBuilderPack sectionBufferBuilderPack;
    public final PoseStack poseStack;
    public final BlockRenderDispatcher blockDispatcher;
    public final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    public final BlockPos pos;
    public final BlockState state;
    public final BlockEntity entity;

    public RenderType renderType = RenderType.solid();
    @Nullable
    public BakedModel bakedModel = null;
    @Nullable
    public BlockState bakedModelBlockState = null;
    public boolean needRenderAdditional = false;
    public Consumer<PoseStack> beforeBakedModel = this.resetToBlock000();

    public Consumer<PoseStack> resetToBlock000() {
        return poseStack -> IFixedModelBlockEntity.resetToBlock000(pos, renderType, poseStack);
    }

    public Consumer<PoseStack> rotateByState() {
        return poseStack -> RenderUtil.rotateAroundBlockCenter(BlockUtil.justGetFacing(bakedModelBlockState, state), poseStack);
    }

    public SectionCompileContext(BlockAndTintGetter level, SectionCompiler sectionCompiler, Map<RenderType, BufferBuilder> bufferBuilders, SectionBufferBuilderPack sectionBufferBuilderPack, PoseStack poseStack, BlockRenderDispatcher blockDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BlockPos pos, BlockState state, BlockEntity entity) {
        this.level = level;
        this.sectionCompiler = sectionCompiler;
        this.bufferBuilders = bufferBuilders;
        this.sectionBufferBuilderPack = sectionBufferBuilderPack;
        this.poseStack = poseStack;
        this.blockDispatcher = blockDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.pos = pos;
        this.state = state;
        this.entity = entity;
    }

    public SectionCompileContext withRenderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public SectionCompileContext withBakedModel(BakedModel bakedModel) {
        this.bakedModel = bakedModel;
        return this;
    }

    public SectionCompileContext withBlockState(BlockState bakedModelBlockState) {
        this.bakedModelBlockState = bakedModelBlockState;
        return this;
    }

    public SectionCompileContext withAdditionalRender() {
        this.needRenderAdditional = true;
        return this;
    }

    public SectionCompileContext withPrepareBakedModelRender(Consumer<PoseStack> preparer) {
        this.beforeBakedModel = preparer;
        return this;
    }

}
