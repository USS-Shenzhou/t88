package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.AccessorProxy;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
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

    public void removeElement(Entry entry) {
        this.removeEntry(entry);
    }

    public void clearElement() {
        super.children().clear();
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.isFocused() && (pKeyCode == GLFW.GLFW_KEY_DOWN || pKeyCode == GLFW.GLFW_KEY_UP)) {
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
                    if (i == 0) {
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
        this.setSelected(this.getEntry(index));
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
    protected void renderBackground(PoseStack pPoseStack) {
        fill(pPoseStack, x0, y0, x0 + width - scrollbarGap - 6, y0 + height, background);
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
     * @see net.minecraft.client.gui.components.AbstractSelectionList#render(PoseStack, int, int, float)
     */
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        int i = this.getScrollbarPosition();
        int j = i + 6;
        AccessorProxy.AbstractSelectionListProxy.setHovered(this, this.isMouseOver(pMouseX, pMouseY) ? this.getEntryAtPosition(pMouseX, pMouseY) : null);
        if (AccessorProxy.AbstractSelectionListProxy.isRenderBackground(this)) {
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
            int k = 32;
            blit(pPoseStack, this.x0, this.y0, (float) this.x1, (float) (this.y1 + (int) this.getScrollAmount()), this.x1 - this.x0, this.y1 - this.y0, 32, 32);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        int l1 = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();
        this.enableScissor();
        if (AccessorProxy.AbstractSelectionListProxy.isRenderHeader(this)) {
            this.renderHeader(pPoseStack, l1, l);
        }

        this.renderList(pPoseStack, pMouseX, pMouseY, pPartialTick);
        disableScissor();
        if (AccessorProxy.AbstractSelectionListProxy.isRenderTopAndBottom(this)) {
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            int i1 = 32;
            RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
            blit(pPoseStack, this.x0, 0, 0.0F, 0.0F, this.width, this.y0, 32, 32);
            blit(pPoseStack, this.x0, this.y1, 0.0F, (float) this.y1, this.width, this.height - this.y1, 32, 32);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int j1 = 4;
            fillGradient(pPoseStack, this.x0, this.y0, this.x1, this.y0 + 4, -16777216, 0);
            fillGradient(pPoseStack, this.x0, this.y1 - 4, this.x1, this.y1, 0, -16777216);
        }

        int i2 = this.getMaxScroll();
        if (i2 > 0) {
            int j2 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            j2 = Mth.clamp(j2, 32, this.y1 - this.y0 - 8);
            int k1 = (int) this.getScrollAmount() * (this.y1 - this.y0 - j2) / i2 + this.y0;
            if (k1 < this.y0) {
                k1 = this.y0;
            }

            //modified for compatibility with TScrollPanel
            double scrollAmount = -getParentScrollAmountIfExist();

            fill(pPoseStack, i, (int) (scrollAmount + this.y0), j, (int) (scrollAmount + this.y1), -16777216);
            fill(pPoseStack, i, (int) (scrollAmount + k1), j, (int) (scrollAmount + k1 + j2), -8355712);
            fill(pPoseStack, i, (int) (scrollAmount + k1), j - 1, (int) (scrollAmount + k1 + j2 - 1), -4144960);
        }

        this.renderDecorations(pPoseStack, pMouseX, pMouseY);
        RenderSystem.disableBlend();
    }

    /*@Override
    protected void renderList(PoseStack pPoseStack, int pX, int pY, int pMouseX, int pMouseY, float pPartialTick) {
        int i = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        for (int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = k + this.itemHeight;
            //modified not to render out of box
            float up = k + (itemHeight - 10) / 2f;
            float low = k + 10;
            if (up >= this.y0 && low <= this.y1) {
                int i1 = pY + j * this.itemHeight + this.headerHeight;
                int j1 = this.itemHeight - 4;
                Entry e = this.getEntry(j);
                int k1 = this.getRowWidth();
                if (this.isSelectedItem(j)) {
                    //modified for compatibility with TScrollPanel
                    double scrollAmount = -getParentScrollAmountIfExist();
                    //modified due to scrollbarGap
                    int l1 = this.x0 + (this.width - 6 - scrollbarGap) / 2 - k1 / 2;
                    int i2 = this.x0 + (this.width - 6 - scrollbarGap) / 2 + k1 / 2;
                    RenderSystem.setShader(GameRenderer::getPositionShader);
                    float f = this.isFocused() ? 1.0F : 0.5F;
                    RenderSystem.setShaderColor(f, f, f, 1.0F);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                    bufferbuilder.vertex((double) l1, scrollAmount + (double) (i1 + j1 + 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double) i2, scrollAmount + (double) (i1 + j1 + 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double) i2, scrollAmount + (double) (i1 - 2), 0.0D).endVertex();
                    bufferbuilder.vertex((double) l1, scrollAmount + (double) (i1 - 2), 0.0D).endVertex();
                    tesselator.end();
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                    bufferbuilder.vertex((double) (l1 + 1), scrollAmount + (double) (i1 + j1 + 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double) (i2 - 1), scrollAmount + (double) (i1 + j1 + 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double) (i2 - 1), scrollAmount + (double) (i1 - 1), 0.0D).endVertex();
                    bufferbuilder.vertex((double) (l1 + 1), scrollAmount + (double) (i1 - 1), 0.0D).endVertex();
                    tesselator.end();
                }

                int j2 = this.getRowLeft();
                e.render(pPoseStack, j, k, j2, k1, j1, pMouseX, pMouseY, Objects.equals(getHovered(), e), pPartialTick);
            }
        }
        super.renderList(pPoseStack, pX, pY, pMouseX, pMouseY, pPartialTick);
    }*/

    @Override
    protected void renderSelection(PoseStack pPoseStack, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
        //modified due to scrollbarGap
        int i = this.x0 + (this.width - pWidth - 6 - scrollbarGap) / 2;
        int j = this.x0 + (this.width + pWidth - 6 - scrollbarGap) / 2;
        //modified for compatibility with TScrollPanel
        double scrollAmount = -getParentScrollAmountIfExist();
        fill(pPoseStack, i, (int) (pTop - 2 + scrollAmount), j, (int) (pTop + pHeight + 2 + scrollAmount), pOuterColor);
        fill(pPoseStack, i + 1, (int) (pTop - 1 + scrollAmount), j - 1, (int) (pTop + pHeight + 1 + scrollAmount), pInnerColor);
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
    public Vec2i getPreferredSize() {
        return null;
    }

    @Override
    public Vec2i getSize() {
        return new Vec2i(width, height);
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

    protected TSelectList<E> getThis() {
        return this;
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
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            Font font = Minecraft.getInstance().font;
            int color = specialForeground == null ? (getSelected() == this ? selectedForeGround : foreground) : specialForeground;
            switch (horizontalAlignment) {
                case LEFT:
                    drawString(pPoseStack, font, getNarration(), pLeft + 1, pTop + (pHeight - font.lineHeight) / 2, color);
                    break;
                case RIGHT:
                    drawString(pPoseStack, font, getNarration(), pLeft + width - font.width(getNarration()) - 1, pTop + (pHeight - font.lineHeight) / 2, color);
                    break;
                default:
                    drawCenteredString(pPoseStack, font, getNarration(), pLeft + pWidth / 2, pTop + (pHeight - font.lineHeight) / 2, color);
            }
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return true;
        }

        public void onSelected() {
            consumer.accept(getThis());
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
