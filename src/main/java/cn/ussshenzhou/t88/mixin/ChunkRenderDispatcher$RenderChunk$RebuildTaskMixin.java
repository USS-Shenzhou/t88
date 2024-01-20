package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.ChunkCompileContext;
import cn.ussshenzhou.t88.render.IFixedModelBlockEntity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
@Mixin(net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class ChunkRenderDispatcher$RenderChunk$RebuildTaskMixin {


    @SuppressWarnings("MissingUnique")
    //needtest
    @Surrogate
    private void t88CompileFixedBlockEntity(float pX, float pY, float pZ, ChunkBufferBuilderPack pChunkBufferBuilderPack,
                                            CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir,
                                            ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compileResults, int i, BlockPos from, BlockPos to, VisGraph visgraph,
                                            RenderChunkRegion renderchunkregion, PoseStack poseStack,
                                            Set<RenderType> renderTypes, RandomSource random,
                                            BlockRenderDispatcher blockDispatcher, Iterator<BlockPos> posIterator,
                                            BlockPos pos,
                                            BlockState state, BlockEntity entity) {
        if (entity instanceof IFixedModelBlockEntity fixedModelBlockEntity) {
            t88compileFixedBlockEntityInternal(pChunkBufferBuilderPack, poseStack, renderTypes, random, blockDispatcher, state, entity, fixedModelBlockEntity, renderchunkregion, pos);
        }
    }

    /**
     * O p t i b a d.
     */
    @SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "InvalidInjectorMethodSignature"})
    @Inject(method = "compile", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;handleBlockEntity(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void t88CompileFixedBlockEntity(float pX, float pY, float pZ, ChunkBufferBuilderPack pChunkBufferBuilderPack,
                                            CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir,
                                            ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compileResults, int i, BlockPos from, BlockPos to, VisGraph visgraph,
                                            PoseStack poseStack, @Coerce Object chunkCacheOF, @Coerce Object singleIterableOF, boolean wtf0OF, boolean wtf1OF,
                                            Set<RenderType> renderTypes, RandomSource random,
                                            BlockRenderDispatcher blockDispatcher, Iterator<BlockPos> posIterator,
                                            @Coerce Object posOF,
                                            BlockState state, BlockEntity entity) {
        if (entity instanceof IFixedModelBlockEntity fixedModelBlockEntity) {
            var renderChunkRegion = (BlockAndTintGetter) chunkCacheOF;
            var pos = (BlockPos) posOF;
            t88compileFixedBlockEntityInternal(pChunkBufferBuilderPack, poseStack, renderTypes, random, blockDispatcher, state, entity, fixedModelBlockEntity, renderChunkRegion, pos);
        }
    }

    @Unique
    private void t88compileFixedBlockEntityInternal(ChunkBufferBuilderPack pChunkBufferBuilderPack, PoseStack poseStack, Set<RenderType> renderTypes, RandomSource random, BlockRenderDispatcher blockDispatcher, BlockState state, BlockEntity entity, IFixedModelBlockEntity fixedModelBlockEntity, BlockAndTintGetter renderChunkRegion, BlockPos pos) {
        var context = fixedModelBlockEntity.handleCompileContext(new ChunkCompileContext(renderChunkRegion, poseStack, blockDispatcher, pos, state, entity));
        if (context == null) {
            return;
        }
        var renderType = context.renderType;
        var builder = pChunkBufferBuilderPack.builder(renderType);
        if (renderTypes.add(renderType)) {
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        }
        if (context.bakedModel != null) {
            poseStack.pushPose();
            context.beforeBakedModel.accept(poseStack);
            var model = context.bakedModel;
            if (model != null) {
                blockDispatcher.getModelRenderer().tesselateBlock(renderChunkRegion, model,
                        context.bakedModelBlockState == null ? state : context.bakedModelBlockState,
                        pos, poseStack, builder, true, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY,
                        model.getModelData(renderChunkRegion, pos, state, ((ChunkRenderDispatcher.RenderChunk.RebuildTask) (Object) this).getModelData(pos)),
                        renderType);
            }
            poseStack.popPose();
        }
        if (context.needRenderAdditional) {
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            fixedModelBlockEntity.renderAdditional(context, renderTypes, pChunkBufferBuilderPack, poseStack, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
