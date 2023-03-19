package cn.ussshenzhou.t88.gui.widegt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * @author USS_Shenzhou
 */
public class TScrollPanel extends TPanel {
    protected double scrollAmount = 0;
    protected double prevScrollAmount = 0;
    protected int bottomY = 0;
    protected static int speedFactor = 6;
    protected int scrollbarGap = 0;

    public TScrollPanel() {
        super();
        this.setBackground(0x80000000);
    }

    @Override
    public void layout() {
        initPos();
        super.layout();
    }

    protected void initPos() {
        for (TWidget tWidget : children) {
            int y = tWidget.getY() + tWidget.getSize().y;
            bottomY = 0;
            if (bottomY < y) {
                bottomY = y;
            }
        }
        bottomY += 5;
    }

    @Override
    public void tickT() {
        prevScrollAmount = scrollAmount;
        super.tickT();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderScrollBar();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderChildren(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        prepareRender(pPoseStack, pPartialTick);
        super.renderChildren(pPoseStack, pMouseX, pMouseY, pPartialTick);
        pPoseStack.popPose();
        RenderSystem.disableScissor();
    }

    @Override
    public void renderTop(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        prepareRender(pPoseStack,pPartialTick);
        super.renderTop(pPoseStack, pMouseX, pMouseY, pPartialTick);
        pPoseStack.popPose();
        RenderSystem.disableScissor();
    }

    protected void prepareRender(PoseStack pPoseStack, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        double scale = minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int) (x * scale),
                (int) (minecraft.getWindow().getHeight() - (y + height) * scale),
                (int) (width * scale),
                (int) (height * scale));
        pPoseStack.pushPose();
        pPoseStack.translate(0, Mth.lerp(pPartialTick, -prevScrollAmount, -scrollAmount), 0);
    }

    @Override
    protected void renderBackground(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (getMaxScroll() > 0) {
            fill(pPoseStack, x, y, x + width - getScrollbarGap() - 6, y + height, background);
        } else {
            fill(pPoseStack, x, y, x + width, y + height, background);
        }
    }

    /**
     * modified from
     *
     * @see net.minecraft.client.gui.components.AbstractSelectionList#render(PoseStack, int, int, float)
     */
    protected void renderScrollBar() {
        int k1 = this.getMaxScroll();
        if (k1 > 0) {
            int i = getScrollBarX();
            int j = i + 6;
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int l1 = (int) ((float) (height * height) / (float) bottomY);
            l1 = Mth.clamp(l1, 32, getY() + height - getY() - 8);
            int i2 = (int) this.getScrollAmount() * (getY() + height - getY() - l1) / k1 + getY();
            if (i2 < getY()) {
                i2 = getY();
            }

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex(i, getY() + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, getY() + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, getY(), 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, getY(), 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, (i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, (i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, i2, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, (i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((j - 1), (i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((j - 1), i2, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }

    protected int getScrollBarX() {
        return this.getX() + width - 6;
    }

    public int getUsableWidth() {
        return width - 6 - scrollbarGap;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY, scrollbarGap, scrollbarGap)) {
            for (TWidget tWidget : children) {
                if (tWidget.mouseDragged(pMouseX, pMouseY + scrollAmount, pButton, pDragX, pDragY)) {
                    return true;
                }
            }
            if (pMouseX > getScrollBarX() - scrollbarGap - 6) {
                double d0 = Math.max(1, this.getMaxScroll());
                int j = Mth.clamp((int) ((float) (height * height) / (float) bottomY), 32, height);
                double d1 = Math.max(1.0D, d0 / (double) (height - j));
                this.addScrollAmount(-pDragY * d1 / speedFactor);
            } else {
                this.addScrollAmount(pDragY / speedFactor);
                return true;
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isInRange(pMouseX, pMouseY)) {
            if (!super.mouseScrolled(pMouseX, pMouseY, pDelta)){
                this.addScrollAmount(pDelta);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseClicked(pMouseX, pMouseY + scrollAmount, pButton);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseReleased(pMouseX, pMouseY + scrollAmount, pButton);
        }
        return false;
    }

    public int getMaxScroll() {
        return Math.max(0, bottomY - getY() - getHeight());
    }

    public double getScrollAmount() {
        return scrollAmount;
    }

    public void addScrollAmount(double deltaScroll) {
        deltaScroll = -deltaScroll * speedFactor;
        this.scrollAmount = Mth.clamp(scrollAmount + deltaScroll, 0.0D, this.getMaxScroll());
    }

    public int getScrollbarGap() {
        return scrollbarGap;
    }

    public void setScrollbarGap(int scrollbarGap) {
        this.scrollbarGap = scrollbarGap;
    }
}
