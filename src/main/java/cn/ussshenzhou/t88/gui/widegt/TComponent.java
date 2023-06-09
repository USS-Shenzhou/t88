package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
public abstract class TComponent implements TWidget {
    protected int x, y, width, height;
    protected int relativeX, relativeY;
    boolean visible = true;
    //argb
    int background = 0x00000000;
    int foreground = 0xffffffff;
    protected LinkedList<TWidget> children = new LinkedList<>();
    protected Border border = null;
    TComponent parent = null;
    TScreen parentScreen = null;
    final int id = (int) (Math.random() * Integer.MAX_VALUE);
    private boolean showHudEvenLoggedOut = false;

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.relativeX = x;
        this.relativeY = y;
        if (parent != null) {
            this.x = x + parent.x;
            this.y = y + parent.y;
        } else {
            this.x = x;
            this.y = y;
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void layout() {
        for (TWidget tWidget : children) {
            if (tWidget instanceof TComponent tComponent) {
                tComponent.layout();
            }
        }
    }

    public void resizeAsHud(int screenWidth, int screenHeight) {
        layout();
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (border != null) {
            renderBorder(graphics, pMouseX, pMouseY, pPartialTick);
        }
        renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        renderChildren(graphics, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderBorder(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int thickness = border.getThickness();
        int color = border.getColor();
        graphics.fill(x - thickness, y - thickness, x + width + thickness, y, color);
        graphics.fill(x - thickness, y + height, x + width + thickness, y + height + thickness, color);
        graphics.fill(x - thickness, y, x, y + height, color);
        graphics.fill(x + width, y, x + width + thickness, y + height, color);
    }

    protected void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(x, y, x + width, y + height, background);
    }

    protected void renderChildren(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (TWidget tWidget : children) {
            if (tWidget.isVisibleT()) {
                tWidget.render(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    @Override
    public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (TWidget w : children) {
            if (w.isVisibleT()) {
                w.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    @Override
    public void tickT() {
        for (TWidget tWidget : children) {
            tWidget.tickT();
        }
    }

    @Override
    public Vec2i getPreferredSize() {
        return new Vec2i(width, height);
    }

    @Override
    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    public void addAll(TWidget... children) {
        Stream.of(children).forEach(this::add);
    }

    public void addAll(Collection<TWidget> children) {
        children.forEach(this::add);
    }

    public void add(TWidget child) {
        children.add(child);
        child.setParent(this);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseReleased(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseScrolled(pMouseX, pMouseY, pDelta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.keyReleased(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        for (TWidget tWidget : children) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.charTyped(pCodePoint, pModifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFinalClose() {
        children.forEach(TWidget::onFinalClose);
    }

    @Override
    public TComponent getParent() {
        return parent;
    }

    @Override
    public void setParent(TComponent parent) {
        this.parent = parent;
    }

    @Override
    public void setParentScreen(@Nullable TScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Nullable
    @Override
    public TScreen getParentScreen() {
        return parentScreen;
    }

    public void remove(TWidget tWidget) {
        children.remove(tWidget);
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    @Override
    public boolean isVisibleT() {
        return visible;
    }

    @Override
    public void setVisibleT(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getForeground() {
        return foreground;
    }

    public void setForeground(int foreground) {
        this.foreground = foreground;
    }

    public boolean isShowHudEvenLoggedOut() {
        return showHudEvenLoggedOut;
    }

    public void setShowHudEvenLoggedOut(boolean showHudEvenLoggedOut) {
        this.showHudEvenLoggedOut = showHudEvenLoggedOut;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof TComponent component) {
            return (component.id == id);
        }
        return false;
    }

    @Override
    public void setFocused(boolean p_265728_) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public static void blitById(GuiGraphics graphics, int id, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        blitById(graphics, id, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public static void blitById(GuiGraphics graphics, int id, int x0, int x1, int y0, int y1, int z, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        innerBlitById(graphics, id, x0, x1, y0, y1, z, (uOffset + 0.0F) / (float) textureWidth, (uOffset + (float) uWidth) / (float) textureWidth, (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight);
    }

    public static void innerBlitById(GuiGraphics graphics, int id, int x0, int x1, int y0, int y1, int z, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShaderTexture(0, id);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float) x0, (float) y0, (float) z).uv(minU, minV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) x0, (float) y1, (float) z).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) x1, (float) y0, (float) z).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
