package cn.ussshenzhou.t88.render.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.Event;
import org.joml.Matrix4f;

/**
 * @author USS_Shenzhou
 */
public class T88RenderChunkBufferTypePrepareEvent extends Event implements ICancellableEvent {
    public final RenderType renderType;
    public final LevelRenderer levelRenderer;
    public final PoseStack poseStack;
    public final Matrix4f frustumMatrix;
    public final Matrix4f projectionMatrix;
    public final int renderTick;
    public final Camera camera;
    public final Frustum frustum;


    public T88RenderChunkBufferTypePrepareEvent(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        this.renderType = renderType;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.frustumMatrix = frustumMatrix;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.camera = camera;
        this.frustum = frustum;
    }

    public T88RenderChunkBufferTypePrepareEvent(RenderType renderType, T88RenderLevelStageEvent event) {
        this(renderType, event.levelRenderer, event.poseStack, event.frustumMatrix, event.projectionMatrix, event.renderTick, event.camera, event.frustum);
    }
}
