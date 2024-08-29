package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.SectionBufferRenderType;
import cn.ussshenzhou.t88.render.SectionBufferRenderTypeHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.logging.LogUtils;
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
    private static ArrayList<RenderType> T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES;
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
        T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES = new ArrayList<>(SectionBufferRenderTypeHelper.scan());

        T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES = Lists.newArrayList(List.of(RenderType.solid(), RenderType.cutoutMipped(), RenderType.cutout(), RenderType.translucent(), RenderType.tripwire()));
        T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES.addAll(T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES);
    }

    @ModifyReturnValue(method = "chunkBufferLayers",at = @At("RETURN"))
    private static List<RenderType> t88extendChunkBufferRenderType(List<RenderType> original){
        if (original instanceof ImmutableList){
            return T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES;
        } else {
            original.addAll(T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES);
            return original;
        }
    }

}
