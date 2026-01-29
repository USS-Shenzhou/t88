package cn.ussshenzhou.t88.gui.container;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.gui.util.RecordHelper;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

/**
 * @author USS_Shenzhou
 */
public class TVerticalAndHorizontalScrollContainer extends TPanel implements TScrollContainer {
    protected double scrollAmountX = 0, scrollAmountY = 0;
    protected double prevScrollAmountX = 0, prevScrollAmountY = 0;
    protected int bottomX = 0, bottomY = 0;
    protected boolean showScrollBarVertical = true, showScrollBarHorizontal = true;
    protected static int speedFactor = 12;
    protected int scrollbarGap = 0;
    protected int bottomMargin = 5;

    public static final int SCROLL_BAR_WIDTH = 6;
    public static final Identifier SCROLLER_VERTICAL = Identifier.fromNamespaceAndPath(T88.MOD_ID, "scroller_v");
    public static final Identifier SCROLLER_HORIZONTAL = Identifier.fromNamespaceAndPath(T88.MOD_ID, "scroller_h");

    public TVerticalAndHorizontalScrollContainer() {
        super();
        this.setBackground(0x80000000);
    }

    @Override
    public void layout() {
        initPos();
        super.layout();
    }

    protected void initPos() {
        bottomX = bottomY = 0;
        for (TWidget tWidget : children) {
            var size = tWidget.getSize();
            int x = tWidget.getXT() + size.x;
            if (bottomX < x) {
                bottomX = x;
            }
            int y = tWidget.getYT() + size.y;
            if (bottomY < y) {
                bottomY = y;
            }
        }
        bottomX += bottomMargin;
        bottomY += bottomMargin;
    }

    @Override
    public void tickT() {
        prevScrollAmountX = scrollAmountX;
        prevScrollAmountY = scrollAmountY;
        super.tickT();
    }

    @Override
    public void render(GuiGraphics guigraphics, int mouseX, int mouseY, float pPartialTick) {
        if (isScrollBarVisibleVertical()) {
            renderVerticalScrollBar(guigraphics);
        }
        if (isScrollBarVisibleHorizontal()) {
            renderHorizontalScrollBar(guigraphics);
        }
        super.render(guigraphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    protected void renderChildren(GuiGraphics guigraphics, int mouseX, int mouseY, float pPartialTick) {
        var scroll = this.getParentScroll();
        guigraphics.enableScissor(
                (int) (this.x - scroll.x),
                (int) (this.y - scroll.y),
                (int) (this.x + getUsableWidth() - scroll.x),
                (int) (this.y + getUsableHeight() - scroll.y));
        prepareTranslate(guigraphics, pPartialTick);
        super.renderChildren(guigraphics, mouseX, mouseY, pPartialTick);
        endTranslate(guigraphics, pPartialTick);
        guigraphics.disableScissor();
    }

    @Override
    public void renderTop(GuiGraphics guigraphics, int mouseX, int mouseY, float pPartialTick) {
        prepareTranslate(guigraphics, pPartialTick);
        super.renderTop(guigraphics, mouseX, mouseY, pPartialTick);
        endTranslate(guigraphics, pPartialTick);
    }

    protected void prepareTranslate(GuiGraphics guigraphics, float pPartialTick) {
        guigraphics.pose().translate((float) Mth.lerp(pPartialTick, -prevScrollAmountX, -scrollAmountX), (float) Mth.lerp(pPartialTick, -prevScrollAmountY, -scrollAmountY));
    }

    protected void endTranslate(GuiGraphics guigraphics, float pPartialTick) {
        guigraphics.pose().translate((float) -Mth.lerp(pPartialTick, -prevScrollAmountX, -scrollAmountX), (float) -Mth.lerp(pPartialTick, -prevScrollAmountY, -scrollAmountY));
    }

    @Override
    protected void renderBackground(GuiGraphics guigraphics, int mouseX, int mouseY, float pPartialTick) {
        guigraphics.fill(x, y,
                x + width - (isScrollBarVisibleVertical() ? getScrollbarGap() + SCROLL_BAR_WIDTH : 0),
                y + height - (isScrollBarVisibleHorizontal() ? getScrollbarGap() + SCROLL_BAR_WIDTH : 0),
                background
        );
    }

    protected Identifier getScrollerVerticalTexture() {
        return SCROLLER_VERTICAL;
    }

    protected Identifier getScrollerHorizontalTexture() {
        return SCROLLER_HORIZONTAL;
    }

    protected void renderVerticalScrollBar(GuiGraphics guiGraphics) {
        int x1 = getScrollBarVerticalX();
        int k = (int) ((float) (this.height * this.height) / bottomY);
        k = Mth.clamp(k, 32, this.height - 8);
        int l = (int) this.getScroll().y * (this.height - k) / this.getMaxScrollY() + this.getYT();
        if (l < this.getYT()) {
            l = this.getYT();
        }
        guiGraphics.fill(x1, this.getYT(), x1 + 6, this.getYT() + height, 0x80000000);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getScrollerVerticalTexture(), x1, l, 6, k);
    }

    protected void renderHorizontalScrollBar(GuiGraphics guiGraphics) {
        int y1 = getScrollBarVerticalY();
        int w = getUsableWidth();
        int k = (int) ((float) (w * w) / bottomX);
        k = Mth.clamp(k, 32, w - 8);
        int l = (int) this.getScroll().x * (w - k) / this.getMaxScrollX() + this.getXT();
        if (l < this.getXT()) {
            l = this.getXT();
        }
        guiGraphics.fill(this.getXT(), y1, this.getXT() + w, y1 + 6, 0x80000000);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getScrollerHorizontalTexture(), l, y1, k, 6);
    }

    protected int getScrollBarVerticalX() {
        return this.getXT() + width - 6;
    }

    protected int getScrollBarVerticalY() {
        return this.getYT() + height - 6;
    }

    public int getUsableWidth() {
        return width - 6 - scrollbarGap;
    }

    public int getUsableHeight() {
        return height - 6 - scrollbarGap;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isInRange(event.x(), event.y(), scrollbarGap, scrollbarGap)) {
            for (TWidget tWidget : children) {
                if (tWidget.mouseDragged(RecordHelper.scroll(event, scrollAmountX, scrollAmountY), dx, dy)) {
                    return true;
                }
            }
            if (event.x() > getScrollBarVerticalX() - scrollbarGap - 6) {
                double d0 = Math.max(1, this.getMaxScrollY());
                int j = Mth.clamp((int) ((float) (height * height) / (float) bottomY), 32, height);
                double d1 = Math.max(1.0D, d0 / (double) (height - j));
                this.addScrollAmountY(-dy * d1 / speedFactor);
            } else {
                this.addScrollAmountY(dy / speedFactor);
            }
            if (event.y() > getScrollBarVerticalY() - scrollbarGap - 6) {
                double d0 = Math.max(1, this.getMaxScrollX());
                int j = Mth.clamp((int) ((float) (width * width) / (float) bottomX), 32, width);
                double d1 = Math.max(1.0D, d0 / (double) (width - j));
                this.addScrollAmountX(-dx * d1 / speedFactor);
            } else {
                this.addScrollAmountX(dx / speedFactor);
            }
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isInRange(mouseX, mouseY)) {
            if (!super.mouseScrolled(mouseX + scrollAmountX, mouseY + scrollAmountY, deltaX, deltaY)) {
                this.addScrollAmountY(deltaY);
                this.addScrollAmountX(deltaX);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isInRange(event)) {
            return super.mouseClicked(RecordHelper.scroll(event, scrollAmountX, scrollAmountY), doubleClick);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (isInRange(event)) {
            return super.mouseReleased(RecordHelper.scroll(event, scrollAmountX, scrollAmountY));
        }
        return false;
    }

    protected boolean isScrollBarVisibleVertical() {
        return getMaxScrollY() > 0 && showScrollBarVertical;
    }

    protected boolean isScrollBarVisibleHorizontal() {
        return getMaxScrollX() > 0 && showScrollBarHorizontal;
    }

    protected int getMaxScrollY() {
        return Math.max(0, bottomY - getYT() - getHeight());
    }

    protected int getMaxScrollX() {
        return Math.max(0, bottomX - getXT() - getWidth());
    }

    @Override
    public Vector2d getScroll() {
        return new Vector2d(scrollAmountX, scrollAmountY);
    }

    public void addScrollAmountY(double deltaScroll) {
        deltaScroll = -deltaScroll * speedFactor;
        var result = Mth.clamp(scrollAmountY + deltaScroll, 0.0D, this.getMaxScrollY());
        if (Double.isNaN(result)) {
            return;
        }
        this.scrollAmountY = result;
    }

    public void addScrollAmountX(double deltaScroll) {
        deltaScroll = -deltaScroll * speedFactor;
        var result = Mth.clamp(scrollAmountX + deltaScroll, 0.0D, this.getMaxScrollX());
        if (Double.isNaN(result)) {
            return;
        }
        this.scrollAmountX = result;
    }

    public int getScrollbarGap() {
        return scrollbarGap;
    }

    public void setScrollbarGap(int scrollbarGap) {
        this.scrollbarGap = scrollbarGap;
    }

    public boolean isShowScrollBarVertical() {
        return showScrollBarVertical;
    }

    public void setShowScrollBarVertical(boolean showScrollBarVertical) {
        this.showScrollBarVertical = showScrollBarVertical;
    }

    public boolean isShowScrollBarHorizontal() {
        return showScrollBarHorizontal;
    }

    public void setShowScrollBarHorizontal(boolean showScrollBarHorizontal) {
        this.showScrollBarHorizontal = showScrollBarHorizontal;
    }

    public static int getSpeedFactor() {
        return speedFactor;
    }

    public static void setSpeedFactor(int speedFactor) {
        TVerticalAndHorizontalScrollContainer.speedFactor = speedFactor;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void setScrollAmountXDirectly(double scrollAmountX) {
        this.prevScrollAmountX = this.scrollAmountX;
        this.scrollAmountX = scrollAmountX;
    }

    public void setScrollAmountYDirectly(double scrollAmountY) {
        this.prevScrollAmountY = this.scrollAmountY;
        this.scrollAmountY = scrollAmountY;
    }
}
