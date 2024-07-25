package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.IFixedModelBlockEntity;
import cn.ussshenzhou.t88.render.SectionCompileContext;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * @author USS_Shenzhou
 */
@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Shadow
    @Final
    private BlockEntityRenderDispatcher blockEntityRenderer;

    @Shadow
    public abstract BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> bufferLayers, SectionBufferBuilderPack sectionBufferBuilderPack, RenderType renderType);

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/chunk/SectionCompiler;handleBlockEntity(Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void t88compileFixedBlockEntity(SectionPos sectionPos, RenderChunkRegion level, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir,
                                            @Local BlockEntity blockentity, @Local PoseStack poseStack, @Local(ordinal = 2) BlockPos pos, @Local BlockState state, @Local Map<RenderType, BufferBuilder> bufferBuilders, @Local RandomSource randomsource) {
        if (blockentity instanceof IFixedModelBlockEntity fixedModelBlockEntity) {
            var context = fixedModelBlockEntity.handleCompileContext(new SectionCompileContext(level, (SectionCompiler) (Object) this, bufferBuilders, sectionBufferBuilderPack, poseStack, blockRenderer, blockEntityRenderer, pos, state, blockentity));
            if (context == null) {
                return;
            }
            var renderType = context.renderType;
            var builder = this.getOrBeginLayer(bufferBuilders, sectionBufferBuilderPack, renderType);
            if (context.bakedModel != null) {
                poseStack.pushPose();
                context.beforeBakedModel.accept(poseStack);
                var model = context.bakedModel;
                if (model != null) {
                    blockRenderer.getModelRenderer().tesselateBlock(level, model,
                            context.bakedModelBlockState == null ? state : context.bakedModelBlockState,
                            pos, poseStack, builder, true, randomsource, state.getSeed(pos), OverlayTexture.NO_OVERLAY,
                            model.getModelData(level, pos, state, level.getModelData(pos)),
                            renderType);
                }
                poseStack.popPose();
            }
            if (context.needRenderAdditional) {
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                fixedModelBlockEntity.renderAdditionalAsync(context, poseStack);
                poseStack.popPose();
            }
        }
    }

    @Redirect(method = "getOrBeginLayer", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;QUADS:Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;"))
    private VertexFormat.Mode t88ModeDecidedByRenderType(@Local(argsOnly = true) RenderType renderType) {
        return renderType.mode();
    }

    @Redirect(method = "getOrBeginLayer", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/vertex/DefaultVertexFormat;BLOCK:Lcom/mojang/blaze3d/vertex/VertexFormat;"))
    private VertexFormat t88FormatDecidedByRenderType(@Local(argsOnly = true) RenderType renderType) {
        return renderType.format();
    }
}
