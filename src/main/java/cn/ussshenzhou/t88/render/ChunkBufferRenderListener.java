package cn.ussshenzhou.t88.render;

import cn.ussshenzhou.t88.render.event.T88RenderChunkBufferTypePrepareEvent;
import cn.ussshenzhou.t88.render.event.T88RenderLevelStageEvent;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChunkBufferRenderListener {

    @SubscribeEvent
    public static void convert(RenderLevelStageEvent event) {
        var s = event.getStage();
        if (s == RenderLevelStageEvent.Stage.AFTER_SKY
                || s == RenderLevelStageEvent.Stage.AFTER_ENTITIES
                || s == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES
                || s == RenderLevelStageEvent.Stage.AFTER_PARTICLES
                || s == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            MinecraftForge.EVENT_BUS.post(new T88RenderLevelStageEvent(event));
        }
    }

    @SubscribeEvent
    public static void renderChunkBufferRenderType(T88RenderLevelStageEvent event) {
        var types = ChunkBufferRenderTypeHelper.renderTypes.get(event.stage);
        if (types != null) {
            types.forEach(renderTypeWithPriority -> renderChunkBufferType(event, renderTypeWithPriority.renderType));
        }
    }

    private static void renderChunkBufferType(T88RenderLevelStageEvent event, RenderType type) {
        event.poseStack.pushPose();
        if (!MinecraftForge.EVENT_BUS.post(new T88RenderChunkBufferTypePrepareEvent(type, event))) {
            double x = event.camera.getPosition().x;
            double y = event.camera.getPosition().y;
            double z = event.camera.getPosition().z;
            event.levelRenderer.renderChunkLayer(type, event.poseStack, x, y, z, event.projectionMatrix);
            event.levelRenderer.renderBuffers.bufferSource().endBatch(type);
        }
        event.poseStack.popPose();
    }
}
