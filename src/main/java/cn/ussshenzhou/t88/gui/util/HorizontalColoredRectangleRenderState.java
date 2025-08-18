package cn.ussshenzhou.t88.gui.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

import javax.annotation.Nullable;

public record HorizontalColoredRectangleRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x0,
        int y0,
        int x1,
        int y1,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public HorizontalColoredRectangleRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2f pose,
            int x0,
            int y0,
            int x1,
            int y1,
            int col1,
            int col2,
            @Nullable ScreenRectangle scissor
    ) {
        this(
                pipeline,
                textureSetup,
                pose,
                x0,
                y0,
                x1,
                y1,
                col1,
                col2,
                scissor,
                getBounds(x0, y0, x1, y1, pose, scissor)
        );
    }

    @Override
    public void buildVertices(VertexConsumer consumer, float z) {
        consumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), z).setColor(this.col1());
        consumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), z).setColor(this.col1());
        consumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), z).setColor(this.col2());
        consumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), z).setColor(this.col2());

    }

    @Nullable
    private static ScreenRectangle getBounds(
            int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea
    ) {
        ScreenRectangle screenrectangle = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenrectangle) : screenrectangle;
    }
}
