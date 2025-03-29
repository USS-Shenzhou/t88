package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.AccessorProxy;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TSelectList<E> extends ObjectSelectionList<TSelectList<E>.Entry> implements TWidget {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
    private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    public static final int SCROLLBAR_WIDTH = 6;
    TComponent parent = null;
    TScreen parentScreen = null;
    int foreground = 0xffffffff;
    int background = 0x80000000;
    int selectedForeGround = foreground;
    boolean visible = true;
    int scrollbarGap = 0;
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    int x1, y1;

    public TSelectList(int pItemHeight, int scrollbarGap) {
        super(Minecraft.getInstance(), 0, 0, 0, pItemHeight);
        this.renderHeader = false;
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
        this.updateScrolling(pMouseX, pMouseY, pButton);
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
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double deltaX, double deltaY) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseScrolled(pMouseX, pMouseY, deltaX, deltaY);
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

    protected int getScrollbarPosition() {
        return width + x - 6;
    }

    @Override
    public int getRowLeft() {
        return x;
    }

    @Override
    public int getRowWidth() {
        return width - scrollbarGap - SCROLLBAR_WIDTH;
    }

    protected void renderBackground(GuiGraphics guigraphics) {
        guigraphics.fill(x, y, x + width - scrollbarGap - 6, y + height, background);
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
            this.x = x + parent.x;
            this.y = y + parent.y;
        } else {
            this.x = x;
            this.y = y;
        }
        this.x1 = this.x + width - scrollbarGap - SCROLLBAR_WIDTH;
        this.y1 = this.y + height;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.x1 = this.x + width - scrollbarGap - SCROLLBAR_WIDTH;
        this.y1 = this.y + height;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
        ResourceLocation resourcelocation = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        guiGraphics.blit(
                RenderType::guiTextured,
                resourcelocation,
                this.getX(),
                this.getY(),
                (float) this.getRight(),
                (float) (this.getBottom() + (int) this.scrollAmount()),
                this.scrollbarVisible() ? this.getWidth() : this.getRowWidth(),
                this.getHeight(),
                32,
                32
        );
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
        ResourceLocation resourcelocation = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        ResourceLocation resourcelocation1 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        guiGraphics.blit(RenderType::guiTextured, resourcelocation, this.getX(), this.getY() - 2, 0.0F, 0.0F,
                this.scrollbarVisible() ? this.getWidth() : this.getRowWidth(),
                2, 32, 2);
        guiGraphics.blit(RenderType::guiTextured, resourcelocation1, this.getX(), this.getBottom(), 0.0F, 0.0F,
                this.scrollbarVisible() ? this.getWidth() : this.getRowWidth(),
                2, 32, 2);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        AccessorProxy.AbstractSelectionListProxy.setHovered(this, this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null);
        this.renderListBackground(guiGraphics);
        this.enableScissor(guiGraphics);
        if (AccessorProxy.AbstractSelectionListProxy.isRenderHeader(this)) {
            int i = this.getRowLeft();
            int j = this.getY() + 4 - (int) this.scrollAmount();
            this.renderHeader(guiGraphics, i, j);
        }

        this.renderListItems(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
        this.renderListSeparators(guiGraphics);
        if (this.scrollbarVisible()) {
            int i = this.scrollBarX();
            int j = this.scrollerHeight();
            int k = this.scrollBarY();
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_BACKGROUND_SPRITE, i, this.getY(), 6, this.getHeight());
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_SPRITE, i, k, 6, j);
        }
        this.renderDecorations(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void enableScissor(GuiGraphics guigraphics) {
        var scroll = this.getParentScroll();
        guigraphics.enableScissor(
                (int) (this.x - scroll.x),
                (int) (this.y - scroll.y),
                (int) (this.x1 + width - scroll.x),
                (int) (this.y1 + height - scroll.y));
    }

    @Override
    protected void renderSelection(GuiGraphics guigraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
        //modified due to scrollbarGap
        int i = this.x + (this.width - pWidth - 6 - scrollbarGap) / 2;
        int j = this.x + (this.width + pWidth - 6 - scrollbarGap) / 2;
        guigraphics.fill(i, pTop - 2, j, pTop + pHeight + 2, pOuterColor);
        guigraphics.fill(i + 1, pTop - 1, j - 1, pTop + pHeight + 1, pInnerColor);
    }

    @Override
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
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

    public TSelectList<E> setBackground(int background) {
        this.background = background;
        return this;
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
            TComponent.drawStringSingleLine(TSelectList.this, guigraphics, font, getNarration(), horizontalAlignment, pLeft + 1, pLeft + pWidth - 1, pTop + (pHeight - font.lineHeight) / 2, color);
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
