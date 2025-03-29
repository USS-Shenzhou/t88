package cn.ussshenzhou.t88.mixin.compat;

import cn.ussshenzhou.t88.render.fixedblockentity.SectionBufferRenderTypeHelper;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(SodiumPrograms.class)
public class IrisSodiumProgramsMixin {

    @Inject(method = "mapTerrainRenderPass", at = @At("HEAD"), require = 0, remap = false, cancellable = true)
    private void t88AdditionalTerrainRenderPass(TerrainRenderPass pass, CallbackInfoReturnable<SodiumPrograms.Pass> cir) {
        if (SectionBufferRenderTypeHelper.Sodium.SODIUM_TERRAIN_RENDER_PASSES.containsValue(pass)) {
            cir.setReturnValue(SodiumPrograms.Pass.TERRAIN);
        }
    }
}
