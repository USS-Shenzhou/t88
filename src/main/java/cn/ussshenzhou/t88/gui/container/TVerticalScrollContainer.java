package cn.ussshenzhou.t88.gui.container;

import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.gui.widegt.TSelectList;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

/**
 * @author USS_Shenzhou
 */
public class TVerticalScrollContainer extends TPanel implements TScrollContainer {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
    protected double scrollAmount = 0;
    protected double prevScrollAmount = 0;
    protected int bottomY = 0;
    protected static int speedFactor = 12;
    protected int scrollbarGap = 0;
    protected int bottomMargin = 5;

    public TVerticalScrollContainer() {
        super();
        this.setBackground(0x80000000);
    }

    @Override
    public void layout() {
        initPos();
        super.layout();
    }

    protected void initPos() {
        bottomY = 0;
        for (TWidget tWidget : children) {
            int y = tWidget.getYT() + tWidget.getSize().y;
            if (bottomY < y) {
                bottomY = y;
            }
        }
        bottomY += bottomMargin;
    }

    @Override
    public void tickT() {
        prevScrollAmount = scrollAmount;
        super.tickT();
    }

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderScrollBar(guigraphics);
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderChildren(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        var scroll = this.getParentScroll();
        guigraphics.enableScissor(
                (int) (this.x - scroll.x),
                (int) (this.y - scroll.y),
                (int) (this.x + width - scroll.x),
                (int) (this.y + height - scroll.y));
        prepareTranslate(guigraphics, pPartialTick);
        super.renderChildren(guigraphics, pMouseX, pMouseY, pPartialTick);
        endTranslate(guigraphics, pPartialTick);
        guigraphics.disableScissor();
    }

    @Override
    public void renderTop(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        prepareTranslate(guigraphics, pPartialTick);
        super.renderTop(guigraphics, pMouseX, pMouseY, pPartialTick);
        endTranslate(guigraphics, pPartialTick);
    }

    protected void prepareTranslate(GuiGraphics guigraphics, float pPartialTick) {
        guigraphics.pose().translate(0, Mth.lerp(pPartialTick, -prevScrollAmount, -scrollAmount), 0);
    }

    protected void endTranslate(GuiGraphics guigraphics, float pPartialTick) {
        guigraphics.pose().translate(0, -Mth.lerp(pPartialTick, -prevScrollAmount, -scrollAmount), 0);
    }

    @Override
    protected void renderBackground(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (getMaxScroll() > 0) {
            guigraphics.fill(x, y, x + width - getScrollbarGap() - 6, y + height, background);
        } else {
            guigraphics.fill(x, y, x + width, y + height, background);
        }
    }

    /**
     * modified from
     *
     * @see net.minecraft.client.gui.components.AbstractSelectionList#render(GuiGraphics, int, int, float)
     */
    protected void renderScrollBar(GuiGraphics guiGraphics) {
        int k1 = this.getMaxScroll();
        if (k1 > 0) {
            int l1 = getScrollBarX();
            int k = (int) ((float) (this.height * this.height) / bottomY);
            k = Mth.clamp(k, 32, this.height - 8);
            int l = (int) this.getScrollAmount() * (this.height - k) / k1 + this.getYT();
            if (l < this.getYT()) {
                l = this.getYT();
            }

            guiGraphics.fill(l1, this.getYT(), l1 + 6, this.getYT() + height, -16777216);
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_SPRITE, l1, l, 6, k);
        }
    }

    protected int getScrollBarX() {
        return this.getXT() + width - 6;
    }

    public int getUsableWidth() {
        return width - 6 - scrollbarGap;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY, scrollbarGap, scrollbarGap)) {
            for (TWidget tWidget : reversed(children)) {
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
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double deltaX, double deltaY) {
        if (isInRange(pMouseX, pMouseY)) {
            if (!super.mouseScrolled(pMouseX, pMouseY, deltaX, deltaY)) {
                this.addScrollAmount(deltaY);
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
        return Math.max(0, bottomY - getYT() - getHeight());
    }

    public double getScrollAmount() {
        return scrollAmount;
    }

    @Override
    public Vector2d getScroll() {
        return new Vector2d(0, scrollAmount);
    }

    public void addScrollAmount(double deltaScroll) {
        deltaScroll = -deltaScroll * speedFactor;
        var result = Mth.clamp(scrollAmount + deltaScroll, 0.0D, this.getMaxScroll());
        if (Double.isNaN(result)) {
            return;
        }
        this.scrollAmount = result;
    }

    public int getScrollbarGap() {
        return scrollbarGap;
    }

    public void setScrollbarGap(int scrollbarGap) {
        this.scrollbarGap = scrollbarGap;
    }

    public void setScrollAmountDirectly(double scrollAmount) {
        this.prevScrollAmount = this.scrollAmount;
        this.scrollAmount = scrollAmount;
    }
}
