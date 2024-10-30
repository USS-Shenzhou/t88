package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.fixedblockentity.SectionBufferRenderTypeHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author USS_Shenzhou
 */
@Mixin(RenderType.class)
public abstract class RenderTypeMixin {

    @Inject(method = "<clinit>", at = @At(value = "HEAD"))
    private static void t88ClinitTest(CallbackInfo ci) {
        var e = new Exception();
        LogUtils.getLogger().info("----------------------------------------For T88 Debug----------------------------------------");
        LogUtils.getLogger().info("RenderType.class loaded by:");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            LogUtils.getLogger().info(stackTraceElement.toString());
        }
        LogUtils.getLogger().info("--------------------------------------------------------------------------------");
    }

    @Mutable
    @Shadow
    @Final
    public static ImmutableList<RenderType> CHUNK_BUFFER_LAYERS;

    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderType;CHUNK_BUFFER_LAYERS:Lcom/google/common/collect/ImmutableList;"))
    private static void t88InitExtendedChunkBufferRenderTypes(ImmutableList<RenderType> value, Operation<Void> original) {
        SectionBufferRenderTypeHelper.T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES = new ArrayList<>(SectionBufferRenderTypeHelper.scan());
        SectionBufferRenderTypeHelper.T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES = Lists.newArrayList(value);
        SectionBufferRenderTypeHelper.T88_EXTENDED_CHUNK_BUFFER_RENDER_TYPES.addAll(SectionBufferRenderTypeHelper.T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES);
        original.call(ImmutableList.builder().addAll(value).addAll(SectionBufferRenderTypeHelper.T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES).build());
    }
}
