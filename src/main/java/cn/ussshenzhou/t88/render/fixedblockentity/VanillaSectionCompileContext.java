package cn.ussshenzhou.t88.render.fixedblockentity;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
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
public class VanillaSectionCompileContext implements SectionCompileContext{
    private final SectionCompiler sectionCompiler;
    private final Map<RenderType, BufferBuilder> bufferBuilders;
    private final SectionBufferBuilderPack sectionBufferBuilderPack;
    private final BlockPos pos;
    private final BlockState state;

    public RenderType renderType = RenderType.solid();
    @Nullable
    public BakedModel bakedModel = null;
    @Nullable
    public BlockState bakedModelBlockState = null;
    public boolean needRenderAdditional = false;
    public Consumer<PoseStack> beforeBakedModel = this.resetToBlock000();

    @Override
    public Consumer<PoseStack> resetToBlock000() {
        return poseStack -> IFixedModelBlockEntity.resetToBlock000(pos, renderType, poseStack);
    }

    @Override
    public Consumer<PoseStack> rotateByState() {
        return poseStack -> RenderUtil.rotateAroundBlockCenter(BlockUtil.justGetFacing(bakedModelBlockState, state), poseStack);
    }

    public VanillaSectionCompileContext(SectionCompiler sectionCompiler, Map<RenderType, BufferBuilder> bufferBuilders, SectionBufferBuilderPack sectionBufferBuilderPack, BlockPos pos, BlockState state) {
        this.sectionCompiler = sectionCompiler;
        this.bufferBuilders = bufferBuilders;
        this.sectionBufferBuilderPack = sectionBufferBuilderPack;
        this.pos = pos;
        this.state = state;
    }

    @Override
    public VanillaSectionCompileContext withRenderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    @Override
    public RenderType getBakedModelRenderType() {
        return renderType;
    }

    @Override
    public VanillaSectionCompileContext withBakedModel(BakedModel bakedModel) {
        this.bakedModel = bakedModel;
        return this;
    }

    @Nullable
    @Override
    public BakedModel getBakedModel() {
        return bakedModel;
    }

    @Override
    public VanillaSectionCompileContext withBlockState(BlockState bakedModelBlockState) {
        this.bakedModelBlockState = bakedModelBlockState;
        return this;
    }

    @Nullable
    @Override
    public BlockState getBakedModelBlockState() {
        return bakedModelBlockState;
    }

    @Override
    public VanillaSectionCompileContext withAdditionalRender() {
        this.needRenderAdditional = true;
        return this;
    }

    @Override
    public boolean hasAdditionalRender() {
        return needRenderAdditional;
    }

    @Override
    public VanillaSectionCompileContext withPrepareBakedModelRender(Consumer<PoseStack> preparer) {
        this.beforeBakedModel = preparer;
        return this;
    }

    @Override
    public Consumer<PoseStack> getPreparer() {
        return beforeBakedModel;
    }

    @Override
    public VertexConsumer getVertexConsumer(RenderType renderType) {
        return sectionCompiler.getOrBeginLayer(bufferBuilders, sectionBufferBuilderPack, renderType);
    }

}
