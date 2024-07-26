package cn.ussshenzhou.t88.render.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.Event;
import org.joml.Matrix4f;

/**
 * @author USS_Shenzhou
 */
public class T88RenderLevelStageEvent extends Event {
    public final RenderLevelStageEvent.Stage stage;
    public final LevelRenderer levelRenderer;
    public final PoseStack poseStack;
    public final Matrix4f projectionMatrix;
    public final int renderTick;
    public final Camera camera;
    public final Frustum frustum;

    public T88RenderLevelStageEvent(RenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.camera = camera;
        this.frustum = frustum;
    }

    public T88RenderLevelStageEvent(RenderLevelStageEvent event) {
        this(event.getStage(), event.getLevelRenderer(), event.getPoseStack(), event.getProjectionMatrix(), event.getRenderTick(), event.getCamera(), event.getFrustum());
    }
}
