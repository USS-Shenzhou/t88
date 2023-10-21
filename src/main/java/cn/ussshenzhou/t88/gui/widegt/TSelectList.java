package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.AccessorProxy;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import com.google.common.collect.ImmutableList;
import org.joml.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TSelectList<E> extends ObjectSelectionList<TSelectList<E>.Entry> implements TWidget {
    public static final int SCROLLBAR_WIDTH = 6;
    TComponent parent = null;
    TScreen parentScreen = null;
    int foreground = 0xffffffff;
    int background = 0x80000000;
    int selectedForeGround = foreground;
    boolean visible = true;
    int scrollbarGap = 0;
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;

    public TSelectList(int pItemHeight, int scrollbarGap) {
        super(Minecraft.getInstance(), 0, 0, 0, 0, pItemHeight);
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.setRenderHeader(false, 0);
        this.scrollbarGap = scrollbarGap;
    }

    public TSelectList() {
        this(20, 0);
    }

    public void addElement(E element) {
        this.addEntry(new Entry(element));
    }

    public void addElement(E element, Consumer<TSelectList<E>> onSelected) {
        this.addEntry(new Entry(element, onSelected));
    }

    public void addElement(Collection<E> elements) {
        for (E e : elements) {
            this.addEntry(new Entry(e));
        }
    }

    public ImmutableList<Entry> getElements() {
        return ImmutableList.copyOf(this.children());
    }

    public void removeElement(Entry entry) {
        this.removeEntry(entry);
    }

    public void clearElement() {
        this.setSelected(null);
        super.children().clear();
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean acceptableKey = pKeyCode == GLFW.GLFW_KEY_DOWN || pKeyCode == GLFW.GLFW_KEY_UP;
        if (this.isFocused() && acceptableKey) {
            Entry selected = this.getSelected();
            if (selected == null) {
                this.setSelected(0);
            } else {
                int i = this.children().indexOf(selected);
                if (pKeyCode == GLFW.GLFW_KEY_DOWN) {
                    if (i >= this.children().size() - 1) {
                        this.setSelected(0);
                    } else {
                        this.setSelected(i + 1);
                    }
                } else {
                    if (i <= 0) {
                        this.setSelected(this.children().size() - 1);
                    } else {
                        this.setSelected(i - 1);
                    }
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public void tickT() {
    }


    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.updateScrollingState(pMouseX, pMouseY, pButton);
        if (!this.isMouseOver(pMouseX, pMouseY)) {
            this.setFocused(false);
        } else {
            Entry e = this.getEntryAtPosition(pMouseX, pMouseY);
            if (e != null) {
                if (e.mouseClicked(pMouseX, pMouseY, pButton)) {
                    this.setFocused(e);
                    this.setDragging(true);
                    this.setSelected(e);
                    return true;
                }
            } else if (pButton == 0) {
                this.clickedHeader((int) (pMouseX - (double) (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int) (pMouseY - (double) this.y0) + (int) this.getScrollAmount() - 4);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseScrolled(pMouseX, pMouseY, pDelta);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY, 8, 8)) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        } else {
            return false;
        }
    }

    @Override
    public void setSelected(@Nullable TSelectList<E>.Entry pSelected) {
        super.setSelected(pSelected);
        if (pSelected != null) {
            this.ensureVisible(pSelected);
            pSelected.onSelected();
        }
    }

    public void setSelected(int index) {
        int i = Mth.clamp(index, 0, this.children().size() - 1);
        this.setSelected(this.getEntry(i));
    }

    @Override
    protected int getScrollbarPosition() {
        return width + x0 - 6;
    }

    @Override
    public int getRowLeft() {
        return x0;
    }

    @Override
    public int getRowWidth() {
        return width - scrollbarGap - 6;
    }

    @Override
    protected void renderBackground(GuiGraphics guigraphics) {
        guigraphics.fill(x0, y0, x0 + width - scrollbarGap - 6, y0 + height, background);
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
    public void setBounds(int x, int y, int width, int height) {
        if (parent != null) {
            this.x0 = x + parent.x;
            this.y0 = y + parent.y;
        } else {
            this.x0 = x;
            this.y0 = y;
        }
        this.x1 = x0 + width - scrollbarGap - 6;
        this.y1 = y0 + height;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x0 = x;
        this.y0 = y;
        this.x1 = x0 + width - scrollbarGap - 6;
        this.y1 = y0 + height;
        this.width = width;
        this.height = height;
    }

    /**
     * modified for compatibility with TScrollPanel
     *
     * @see net.minecraft.client.gui.components.AbstractSelectionList#render(GuiGraphics, int, int, float)
     */
    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guigraphics);
        int i = this.getScrollbarPosition();
        int j = i + 6;
        AccessorProxy.AbstractSelectionListProxy.setHovered(this, this.isMouseOver(pMouseX, pMouseY) ? this.getEntryAtPosition(pMouseX, pMouseY) : null);
        if (AccessorProxy.AbstractSelectionListProxy.isRenderBackground(this)) {
            RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
            guigraphics.blit(BACKGROUND_LOCATION, this.x0, this.y0, (float) this.x1, (float) (this.y1 + (int) this.getScrollAmount()), this.x1 - this.x0, this.y1 - this.y0, 32, 32);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        int l1 = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();
        this.enableScissor(guigraphics);
        if (AccessorProxy.AbstractSelectionListProxy.isRenderHeader(this)) {
            this.renderHeader(guigraphics, l1, l);
        }

        this.renderList(guigraphics, pMouseX, pMouseY, pPartialTick);
        guigraphics.disableScissor();
        if (AccessorProxy.AbstractSelectionListProxy.isRenderTopAndBottom(this)) {
            RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
            guigraphics.blit(BACKGROUND_LOCATION, this.x0, 0, 0.0F, 0.0F, this.width, this.y0, 32, 32);
            guigraphics.blit(BACKGROUND_LOCATION, this.x0, this.y1, 0.0F, (float) this.y1, this.width, this.height - this.y1, 32, 32);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guigraphics.fillGradient(this.x0, this.y0, this.x1, this.y0 + 4, -16777216, 0);
            guigraphics.fillGradient(this.x0, this.y1 - 4, this.x1, this.y1, 0, -16777216);
        }

        int i2 = this.getMaxScroll();
        if (i2 > 0) {
            int j2 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            j2 = Mth.clamp(j2, 32, this.y1 - this.y0 - 8);
            int k1 = (int) this.getScrollAmount() * (this.y1 - this.y0 - j2) / i2 + this.y0;
            if (k1 < this.y0) {
                k1 = this.y0;
            }
            guigraphics.fill(i, this.y0, j, +this.y1, -16777216);
            guigraphics.fill(i, k1, j, k1 + j2, -8355712);
            guigraphics.fill(i, k1, j - 1, k1 + j2 - 1, -4144960);
        }

        this.renderDecorations(guigraphics, pMouseX, pMouseY);
        RenderSystem.disableBlend();
    }

    @Override
    protected void enableScissor(GuiGraphics guigraphics) {
        guigraphics.enableScissor(this.x0, (int) (this.y0 - this.getParentScrollAmountIfExist()), this.x1, (int) (this.y1 - this.getParentScrollAmountIfExist()));
    }

    @Override
    protected void renderSelection(GuiGraphics guigraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
        //modified due to scrollbarGap
        int i = this.x0 + (this.width - pWidth - 6 - scrollbarGap) / 2;
        int j = this.x0 + (this.width + pWidth - 6 - scrollbarGap) / 2;
        guigraphics.fill(i, pTop - 2, j, pTop + pHeight + 2, pOuterColor);
        guigraphics.fill(i + 1, pTop - 1, j - 1, pTop + pHeight + 1, pInnerColor);
    }

    @Override
    public int getXT() {
        return x0;
    }

    @Override
    public int getYT() {
        return y0;
    }

    public void setScrollbarGap(int scrollbarGap) {
        this.scrollbarGap = scrollbarGap;
    }

    public int getScrollbarGap() {
        return scrollbarGap;
    }

    @Override
    public void setParent(TComponent parent) {
        this.parent = parent;
    }

    @Override
    public TComponent getParent() {
        return parent;
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

    @Override
    public Vector2i getPreferredSize() {
        return null;
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public int getForeground() {
        return foreground;
    }

    public void setForeground(int foreground) {
        this.foreground = foreground;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getSelectedForeGround() {
        return selectedForeGround;
    }

    public void setSelectedForeGround(int selectedForeGround) {
        this.selectedForeGround = selectedForeGround;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        E content;
        Consumer<TSelectList<E>> consumer;
        protected Integer specialForeground = null;

        public Entry(E content, Consumer<TSelectList<E>> consumer) {
            this.content = content;
            this.consumer = consumer;
        }

        public Entry(E content) {
            this.content = content;
            this.consumer = list -> {
            };
        }

        @Override
        public Component getNarration() {
            Language language = Language.getInstance();
            String s = content.toString();
            if (language.has(s)) {
                return Component.translatable(s);
            } else {
                return Component.literal(s);
            }
        }

        @Override
        public void render(GuiGraphics guigraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Font font = Minecraft.getInstance().font;
            int color = specialForeground == null ? (getSelected() == this ? selectedForeGround : foreground) : specialForeground;
            TComponent.drawStringSingleLine(TSelectList.this, guigraphics, font, getNarration(), horizontalAlignment, pLeft + 1, pLeft + width - 2, pTop + (pHeight - font.lineHeight) / 2, color);
            /*switch (horizontalAlignment) {
                case LEFT:
                    guigraphics.drawString(font, getNarration(), pLeft + 1, pTop + (pHeight - font.lineHeight) / 2, color);
                    break;
                case RIGHT:
                    guigraphics.drawString(font, getNarration(), pLeft + width - font.width(getNarration()) - 1, pTop + (pHeight - font.lineHeight) / 2, color);
                    break;
                default:
                    guigraphics.drawCenteredString(font, getNarration(), pLeft + pWidth / 2, pTop + (pHeight - font.lineHeight) / 2, color);
            }*/
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return true;
        }

        public void onSelected() {
            consumer.accept(TSelectList.this);
        }

        public void setConsumer(Consumer<TSelectList<E>> consumer) {
            this.consumer = consumer;
        }

        public E getContent() {
            return content;
        }

        public int getSpecialForeground() {
            return specialForeground;
        }

        public void setSpecialForeground(int specialForeground) {
            this.specialForeground = specialForeground;
        }

        public void clearSpecialForeground() {
            specialForeground = null;
        }
    }

}
