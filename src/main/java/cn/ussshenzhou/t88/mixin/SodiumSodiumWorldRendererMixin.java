package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.fixedblockentity.SectionBufferRenderTypeHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(SodiumWorldRenderer.class)
public class SodiumSodiumWorldRendererMixin {
    @Shadow
    private RenderSectionManager renderSectionManager;

    @Inject(method = "drawChunkLayer", at = @At("RETURN"),require = 0)
    private void t88DrawChunkLayer(CallbackInfo ci,
                                   @Local(argsOnly = true) RenderType renderLayer, @Local(argsOnly = true) ChunkRenderMatrices matrices, @Local(argsOnly = true, ordinal = 0) double x, @Local(argsOnly = true, ordinal = 1) double y, @Local(argsOnly = true, ordinal = 2) double z) {
        if (SectionBufferRenderTypeHelper.T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES.contains(renderLayer)) {
            this.renderSectionManager.renderLayer(matrices, SectionBufferRenderTypeHelper.Sodium.SODIUM_TERRAIN_RENDER_PASSES.get(renderLayer), x, y, z);
        }
    }
}
