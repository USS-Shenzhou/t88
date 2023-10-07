package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.render.event.T88RenderLevelStageEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
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

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void t88FireT88RenderLevelStageEvent(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new T88RenderLevelStageEvent(RenderLevelStageEvent.Stage.fromRenderType(renderType),
                        (LevelRenderer) (Object) this,
                        poseStack, projectionMatrix, ticks,
                        minecraft.getPartialTick(), minecraft.gameRenderer.getMainCamera(), getFrustum()
                )
        );
    }
}
