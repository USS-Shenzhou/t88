package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.ColorManager;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class TComponent implements TWidget {
    protected int x, y, width, height;
    protected int relativeX, relativeY;
    protected boolean visible = true;
    //argb
    protected int background = 0x00000000;
    protected int foreground = ColorManager.get().defaultForeground();
    protected LinkedList<TWidget> children = new LinkedList<>();
    protected Border border = null;
    TComponent parent = null;
    TScreen parentScreen = null;
    final int id = (int) (Math.random() * Integer.MAX_VALUE);
    protected boolean showHudEvenLoggedOut = false;
    @Nullable
    private WidgetTooltipHolder tooltip;

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
        renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        if (border != null) {
            renderBorder(graphics, pMouseX, pMouseY, pPartialTick);
        }
        renderChildren(graphics, pMouseX, pMouseY, pPartialTick);
        var t = this.getTooltip();
        if (t != null) {
            var scroll = getParentScroll();
            //FIXME multi-scroller
            var inRange = getParentInstanceOfOptional(TScrollContainer.class)
                    .map(tScrollContainer -> tScrollContainer.isInRange(pMouseX, pMouseY))
                    .orElse(true) && this.isInRange(pMouseX + scroll.x, pMouseY + scroll.y);
            t.refreshTooltipForNextRenderPass(inRange, this.isFocused(), this.getRectangle());
        }
    }

    protected void renderBorder(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int thickness = border.getThickness();
        int color = border.getColor();
        Border.renderBorder(graphics, color, thickness, x, y, width, height);
    }

    protected void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(x, y, x + width, y + height, background);
    }

    protected void renderChildren(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (TWidget tWidget : children) {
            if (tWidget.isVisibleT()) {
                graphics.pose().translate(0, 0, 0.1);
                tWidget.render(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    @Override
    public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (TWidget w : children) {
            if (w.isVisibleT()) {
                graphics.pose().translate(0, 0, 0.1);
                w.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    public TComponent setTooltip(@Nullable WidgetTooltipHolder tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public TComponent setTooltip(@Nullable Tooltip tooltip) {
        var widgetTooltipHolder = new WidgetTooltipHolder();
        widgetTooltipHolder.set(tooltip);
        return setTooltip(widgetTooltipHolder);
    }

    @Nullable
    public WidgetTooltipHolder getTooltip() {
        return this.tooltip;
    }

    public TComponent setTooltipDelay(int pTooltipMsDelay) {
        if (this.tooltip != null) {
            this.tooltip.setDelay(Duration.ofMillis(pTooltipMsDelay));
        }
        return this;
    }

    @Override
    public void tickT() {
        tickChildren();
    }

    public void tickChildren() {
        for (TWidget tWidget : children) {
            tWidget.tickT();
        }
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(width, height);
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public TComponent addAll(TWidget... children) {
        Stream.of(children).forEach(this::add);
        return this;
    }

    public TComponent addAll(Collection<TWidget> children) {
        children.forEach(this::add);
        return this;
    }

    public TComponent add(TWidget child) {
        children.add(child);
        child.setParent(this);
        return this;
    }

    public ImmutableList<TWidget> getChildren() {
        return ImmutableList.copyOf(this.children);
    }

    public static <T> Iterable<T> reversed(List<T> list) {
        return () -> new Iterator<>() {
            private final ListIterator<T> iter = list.listIterator(list.size());

            @Override
            public boolean hasNext() {
                return iter.hasPrevious();
            }

            @Override
            public T next() {
                return iter.previous();
            }
        };
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (TWidget tWidget : reversed(children)) {
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
        for (TWidget tWidget : reversed(children)) {
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
        for (TWidget tWidget : reversed(children)) {
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
    public boolean mouseScrolled(double pMouseX, double pMouseY, double deltaX, double deltaY) {
        for (TWidget tWidget : reversed(children)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseScrolled(pMouseX, pMouseY, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (TWidget tWidget : reversed(children)) {
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
        for (TWidget tWidget : reversed(children)) {
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
        for (TWidget tWidget : reversed(children)) {
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
        reversed(children).forEach(TWidget::onFinalClose);
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

    public void setBorder(@Nullable Border border) {
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

    public TComponent setBackground(int background) {
        this.background = background;
        return this;
    }

    public int getForeground() {
        return foreground;
    }

    public TComponent setForeground(int foreground) {
        this.foreground = foreground;
        return this;
    }

    public boolean isShowHudEvenLoggedOut() {
        return showHudEvenLoggedOut;
    }

    public TComponent setShowHudEvenLoggedOut(boolean showHudEvenLoggedOut) {
        this.showHudEvenLoggedOut = showHudEvenLoggedOut;
        return this;
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

    public void drawStringSingleLine(GuiGraphics graphics, Font font, Component text, float fontSize, HorizontalAlignment align, int minX, int maxX, int minY, @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming") int maxYOnlyForScissor, int color) {
        drawStringSingleLine(this, graphics, font, text, fontSize, align, minX, maxX, minY, maxYOnlyForScissor, color);
    }

    public static void drawStringSingleLine(TWidget thiz, GuiGraphics graphics, Font font, Component text, float fontSize, HorizontalAlignment align, int minX, int maxX, int minY, @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming") int maxYOnlyForScissor, int color) {
        graphics.pose().pushPose();
        float scaleFactor = fontSize / TLabel.STD_FONT_SIZE;
        int need = font.width(text);
        int available = maxX - minX;
        int extra = need - available;
        if (extra > 0) {
            var scroll = thiz.getParentScroll();
            graphics.enableScissor((int) (minX - scroll.x), (int) (minY - scroll.y), (int) (maxX - scroll.x), (int) (maxYOnlyForScissor - scroll.y));
            graphics.pose().scale(scaleFactor, scaleFactor, 1);
            minX = (int) (minX / scaleFactor);
            maxX = (int) (maxX / scaleFactor);
            minY = (int) (minY / scaleFactor);
            maxYOnlyForScissor = (int) (maxYOnlyForScissor / scaleFactor);
            renderScrollingString(extra, graphics, font, text, minX, maxX, minY, maxYOnlyForScissor, color);
            graphics.disableScissor();
        } else {
            graphics.pose().scale(scaleFactor, scaleFactor, 1);
            minX = (int) (minX / scaleFactor);
            minY = (int) (minY / scaleFactor);
            switch (align) {
                case LEFT -> graphics.drawString(font, text, minX, minY, color);
                case CENTER -> graphics.drawString(font, text, minX - extra / 2, minY, color);
                case RIGHT -> graphics.drawString(font, text, minX - extra, minY, color);
            }
        }
        graphics.pose().popPose();
    }

    public static void drawStringSingleLine(TWidget thiz, GuiGraphics graphics, Font font, Component text, HorizontalAlignment align, int minX, int maxX, int minY, int color) {
        drawStringSingleLine(thiz, graphics, font, text, TLabel.STD_FONT_SIZE, align, minX, maxX, minY, minY + 9, color);
    }

    public static void drawStringSingleLine(TWidget thiz, GuiGraphics graphics, Font font, Component text, int minX, int maxX, int minY, @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming") int maxYOnlyForScissor, int color) {
        drawStringSingleLine(thiz, graphics, font, text, TLabel.STD_FONT_SIZE, HorizontalAlignment.LEFT, minX, maxX, minY, maxYOnlyForScissor, color);
    }

    public static void drawStringSingleLine(TWidget thiz, GuiGraphics graphics, Font font, Component text, int minX, int maxX, int minY, int color) {
        drawStringSingleLine(thiz, graphics, font, text, TLabel.STD_FONT_SIZE, HorizontalAlignment.LEFT, minX, maxX, minY, minY + 9, color);
    }

    public void drawStringSingleLine(GuiGraphics graphics, Font font, Component text, HorizontalAlignment align, int minX, int maxX, int minY, int color) {
        drawStringSingleLine(this, graphics, font, text, TLabel.STD_FONT_SIZE, align, minX, maxX, minY, minY + 9, color);
    }

    public void drawStringSingleLine(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int minY, @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming") int maxYOnlyForScissor, int color) {
        drawStringSingleLine(this, graphics, font, text, TLabel.STD_FONT_SIZE, HorizontalAlignment.LEFT, minX, maxX, minY, maxYOnlyForScissor, color);
    }

    public void drawStringSingleLine(GuiGraphics graphics, Font font, Component text, int minX, int maxX, int minY, int color) {
        drawStringSingleLine(this, graphics, font, text, TLabel.STD_FONT_SIZE, HorizontalAlignment.LEFT, minX, maxX, minY, minY + 9, color);
    }

    public static void renderScrollingString(int extra, GuiGraphics graphics, Font font, Component text, int minX, int maxX, int minY, int maxY, int color) {
        double d0 = (double) Util.getMillis() / 1000.0D;
        double d1 = Math.max((double) extra * 0.5D, 3.0D);
        double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
        double d3 = Mth.lerp(d2, 0.0D, extra);
        graphics.drawString(font, text, minX - (int) d3, minY, color);
    }
}
