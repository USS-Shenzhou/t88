package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.event.T88RenderLevelStageEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    private int ticks;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow(remap = false)
    public abstract Frustum getFrustum();

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void t88FireT88RenderLevelStageEvent(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        var stage = RenderLevelStageEvent.Stage.fromRenderType(renderType);
        if (stage != null) {
            NeoForge.EVENT_BUS.post(
                    new T88RenderLevelStageEvent(stage,
                            (LevelRenderer) (Object) this,
                            new PoseStack(), projectionMatrix, ticks,
                            minecraft.gameRenderer.getMainCamera(), getFrustum()
                    )
            );
        }
    }
}
