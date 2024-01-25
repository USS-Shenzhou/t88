package cn.ussshenzhou.t88.render;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class ChunkCompileContext {
    public final BlockAndTintGetter level;
    public final PoseStack poseStack;
    public final BlockRenderDispatcher blockDispatcher;
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

    public ChunkCompileContext(BlockAndTintGetter level, PoseStack poseStack, BlockRenderDispatcher blockDispatcher, BlockPos pos, BlockState state, BlockEntity entity) {
        this.level = level;
        this.poseStack = poseStack;
        this.blockDispatcher = blockDispatcher;
        this.pos = pos;
        this.state = state;
        this.entity = entity;
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

    public ChunkCompileContext withPrepareBakedModelRender(Consumer<PoseStack> preparer) {
        this.beforeBakedModel = preparer;
        return this;
    }

}
