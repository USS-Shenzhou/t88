package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.fixedblockentity.IFixedModelBlockEntity;
import cn.ussshenzhou.t88.render.fixedblockentity.SodiumSectionCompileContext;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(ChunkBuilderMeshingTask.class)
public abstract class SodiumChunkBuilderMeshingTaskMixin {

    @Inject(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;",
                    shift = At.Shift.AFTER),
            require = 0, remap = false)
    private void t88compileFixedBlockEntity(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
                                            @Local BlockEntity blockentity, @Local(ordinal = 0) BlockPos.MutableBlockPos pos, @Local BlockState blockState,
                                            @Local BlockRenderer blockRenderer, @Local(ordinal = 1) BlockPos.MutableBlockPos modelOffset, @Local TranslucentGeometryCollector collector) {
        if (blockentity instanceof IFixedModelBlockEntity fixedModelBlockEntity) {
            var context = fixedModelBlockEntity.handleCompileContext(new SodiumSectionCompileContext(buildContext, pos, blockState, collector));
            if (context == null) {
                return;
            }
            var renderType = context.getBakedModelRenderType();
            var model = context.getBakedModel();
            PoseStack poseStack = new PoseStack();
            if (model != null) {
                poseStack.pushPose();
                context.getPreparer().accept(poseStack);
                blockRenderer.renderModel(model, blockState, pos, modelOffset);
                poseStack.popPose();
            }
            if (context.hasAdditionalRender()) {
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                fixedModelBlockEntity.renderAdditionalAsync(context, poseStack);
                poseStack.popPose();
            }
        }
    }
}
