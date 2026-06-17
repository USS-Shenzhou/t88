package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.HudManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(Gui.class)
public class GameRendererMixin {

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractDeferredSubtitles()V", shift = At.Shift.AFTER))
    public void t88RenderHud(CallbackInfo info, @Local(name = "graphics") GuiGraphicsExtractor graphics, @Local(name = "xMouse") int mouseX, @Local(name = "yMouse") int mouseY, @Local(argsOnly = true, name = "deltaTracker") DeltaTracker deltaTracker) {
        graphics.nextStratum();
        HudManager.renderHud(graphics, mouseX, mouseY, deltaTracker);
        graphics.nextStratum();
    }
}
