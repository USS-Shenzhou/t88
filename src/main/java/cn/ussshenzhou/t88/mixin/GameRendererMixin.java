package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.gui.event.GameRendererRenderedEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void afterGameRendererRenderedT88(float pPartialTicks, long pNanoTime, boolean pRenderLevel, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new GameRendererRenderedEvent(pPartialTicks, new PoseStack()));
    }
}
