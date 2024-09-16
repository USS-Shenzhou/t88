package cn.ussshenzhou.t88.render.fixedblockentity;

import cn.ussshenzhou.t88.render.event.T88RenderChunkBufferTypePrepareEvent;
import cn.ussshenzhou.t88.render.event.T88RenderLevelStageEvent;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ChunkBufferRenderListener {

    @SubscribeEvent
    public static void convert(RenderLevelStageEvent event) {
        var s = event.getStage();
        if (s == RenderLevelStageEvent.Stage.AFTER_SKY
                || s == RenderLevelStageEvent.Stage.AFTER_ENTITIES
                || s == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES
                || s == RenderLevelStageEvent.Stage.AFTER_PARTICLES
                || s == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            NeoForge.EVENT_BUS.post(new T88RenderLevelStageEvent(event));
        }
    }

    @SubscribeEvent
    public static void renderChunkBufferRenderType(T88RenderLevelStageEvent event) {

        var types = SectionBufferRenderTypeHelper.renderTypes.get(event.stage);
        if (types != null) {
            types.forEach(renderTypeWithPriority -> renderChunkBufferType(event, renderTypeWithPriority.renderType));
        }
    }

    private static void renderChunkBufferType(T88RenderLevelStageEvent event, RenderType type) {
        var prepareEvent = new T88RenderChunkBufferTypePrepareEvent(type, event);
        var poseStack = event.poseStack;
        var camera = event.camera;
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(camera.getRoll()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        if (!NeoForge.EVENT_BUS.post(prepareEvent).isCanceled()) {
            double x = camera.getPosition().x;
            double y = camera.getPosition().y;
            double z = camera.getPosition().z;
            poseStack.translate(-x, -y, -z);
            event.levelRenderer.renderSectionLayer(type, x, y, z, poseStack.last().pose(), event.projectionMatrix);
            event.levelRenderer.renderBuffers.bufferSource().endBatch(type);
        }
        poseStack.popPose();
    }
}
