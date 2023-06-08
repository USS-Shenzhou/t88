package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.event.GameRendererRenderedEvent;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author USS_Shenzhou
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getToasts()Lnet/minecraft/client/gui/components/toasts/ToastComponent;")
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void afterGameRendererRenderedT88(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci, int i, int j, Window window, Matrix4f matrix4f, PoseStack posestack, GuiGraphics guigraphics) {
        MinecraftForge.EVENT_BUS.post(new GameRendererRenderedEvent(pPartialTicks, guigraphics));
    }
}
