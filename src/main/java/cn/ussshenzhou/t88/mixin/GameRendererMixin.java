package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.HudManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void t88RenderHud(CallbackInfo info, @Local GuiGraphics guiGraphics, @Local(ordinal = 0) int mouseX, @Local(ordinal = 1) int mouseY, @Local(argsOnly = true) DeltaTracker deltaTracker) {
        HudManager.renderHud(guiGraphics, mouseX, mouseY, deltaTracker);
    }
}
