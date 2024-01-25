package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.SectionBufferRenderTypeHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author USS_Shenzhou
 */
@Mixin(RenderType.class)
public abstract class RenderTypeMixin {

    @Unique
    private static ArrayList<RenderType> T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderType;CHUNK_BUFFER_LAYERS:Lcom/google/common/collect/ImmutableList;"))
    private static void t88InitExtendedChunkBufferRenderTypes(CallbackInfo ci) {
        T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES = Lists.newArrayList(List.of(RenderType.solid(), RenderType.cutoutMipped(), RenderType.cutout(), RenderType.translucent(), RenderType.tripwire()));
        T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES.addAll(SectionBufferRenderTypeHelper.scan());
    }

    @Inject(method = "chunkBufferLayers", at = @At("HEAD"), cancellable = true)
    private static void t88extendChunkBufferRenderType(CallbackInfoReturnable<List<RenderType>> cir) {
        cir.setReturnValue(T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES);
    }


}
