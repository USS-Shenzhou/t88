package cn.ussshenzhou.t88.mixin.compat;

import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(WorldRenderingPhase.class)
public abstract class IrisWorldRenderingPhaseMixin {

    @Inject(method = "fromTerrainRenderType", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"), cancellable = true, require = 0)
    private static void t88CalmDownIris(RenderType renderType, CallbackInfoReturnable<WorldRenderingPhase> cir) {
        if (RenderType.chunkBufferLayers().contains(renderType)) {
            cir.setReturnValue(WorldRenderingPhase.NONE);
        }
    }
}
