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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author USS_Shenzhou
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void t88AfterGameRendererRendered(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci, int i, int j, Window window, Matrix4f matrix4f, PoseStack posestack, GuiGraphics guigraphics) {
        MinecraftForge.EVENT_BUS.post(new GameRendererRenderedEvent(pPartialTicks, guigraphics));
        guigraphics.flush();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0, expect = 0)
    private void t88AfterGameRendererRendered$OptifineCompatibility(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci, int i, int j, Window window, float guiFarPlane, Matrix4f matrix4f, PoseStack posestack, float guiOffsetZ, GuiGraphics guigraphics) {
        MinecraftForge.EVENT_BUS.post(new GameRendererRenderedEvent(pPartialTicks, guigraphics));
        guigraphics.flush();
    }
}
