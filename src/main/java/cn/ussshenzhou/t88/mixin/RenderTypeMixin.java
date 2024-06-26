package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.SectionBufferRenderTypeHelper;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.RenderType;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * @author USS_Shenzhou
 */
@Mixin(RenderType.class)
public abstract class RenderTypeMixin {

    @Unique
    private static ArrayList<RenderType> T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES;

    @Inject(method = "<clinit>", at = @At(value = "HEAD"))
    private static void t88Test(CallbackInfo ci) {
        var e = new Exception();
        LogUtils.getLogger().info("----------------------------------------For T88 Debug----------------------------------------");
        LogUtils.getLogger().info("RenderType.class loaded by:");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            LogUtils.getLogger().info(stackTraceElement.toString());
        }
        LogUtils.getLogger().info("--------------------------------------------------------------------------------");
    }

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
