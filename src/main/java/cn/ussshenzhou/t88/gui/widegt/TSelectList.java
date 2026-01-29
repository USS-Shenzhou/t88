package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Vector2i;
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
public class TSelectList<Element> extends ObjectSelectionList<TSelectList<Element>.Entry> implements TWidget {
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("widget/scroller_background");
    public static final int SCROLLBAR_WIDTH = 6;
    TComponent parent = null;
    TScreen parentScreen = null;
    int foreground = 0xffffffff;
    int background = 0x80000000;
    int selectedForeGround = foreground;
    boolean visible = true;
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    int x1, y1;

    public TSelectList(int pItemHeight, int scrollbarGap) {
        super(Minecraft.getInstance(), 0, 0, 0, pItemHeight);
    }

    public TSelectList() {
        this(20, 0);
    }

    public void addElement(Element element) {
        this.addEntry(new Entry(element));
    }

    public void addElement(Element element, Consumer<TSelectList<Element>> onSelected) {
        this.addEntry(new Entry(element, onSelected));
    }

    public void addElement(Collection<Element> elements) {
        for (Element element : elements) {
            this.addEntry(new Entry(element));
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

    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean acceptableKey = event.key() == GLFW.GLFW_KEY_DOWN || event.key() == GLFW.GLFW_KEY_UP;
        if (this.isFocused() && acceptableKey) {
            Entry selected = this.getSelected();
            if (selected == null) {
                this.setSelected(0);
            } else {
                int i = this.children().indexOf(selected);
                if (event.key() == GLFW.GLFW_KEY_DOWN) {
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
    public void layout() {
        this.repositionEntries();
        for (var entry : this.children) {
            if (entry instanceof TWidget widget) {
                widget.layout();
            }
        }
    }


    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.updateScrolling(event);
        if (!this.isMouseOver(event.x(), event.y())) {
            this.setFocused(false);
        } else {
            Entry e = this.getEntryAtPosition(event.x(), event.y());
            if (e != null) {
                if (e.mouseClicked(event, doubleClick)) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isInRange(mouseX, mouseY)) {
            return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isInRange(event, 8, 8)) {
            return super.mouseDragged(event, dx, dy);
        } else {
            return false;
        }
    }

    @Override
    public void setSelected(@Nullable TSelectList<Element>.Entry pSelected) {
        super.setSelected(pSelected);
        if (pSelected != null) {
            //this.ensureVisible(pSelected);
            pSelected.onSelected();
        }
    }

    public void setSelected(int index) {
        int i = Mth.clamp(index, 0, this.children().size() - 1);
        this.setSelected(this.getEntry(i));
    }

    public TSelectList<Element>.Entry getEntry(int index) {
        return children.get(index);
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
        return width - SCROLLBAR_WIDTH;
    }

    protected void renderBackground(GuiGraphics guigraphics) {
        guigraphics.fill(x, y, x + width - 6, y + height, background);
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
        this.x1 = this.x + width - SCROLLBAR_WIDTH;
        this.y1 = this.y + height;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.x1 = this.x + width - SCROLLBAR_WIDTH;
        this.y1 = this.y + height;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
        Identifier Identifier = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier,
                this.getX(),
                this.getY(),
                (float) this.getRight(),
                (float) (this.getBottom() + (int) this.scrollAmount()),
                this.scrollable() ? this.getWidth() : this.getRowWidth(),
                this.getHeight(),
                32,
                32
        );
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
        Identifier Identifier = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        Identifier Identifier1 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier, this.getX(), this.getY() - 2, 0.0F, 0.0F,
                this.scrollable() ? this.getWidth() : this.getRowWidth(),
                2, 32, 2);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier1, this.getX(), this.getBottom(), 0.0F, 0.0F,
                this.scrollable() ? this.getWidth() : this.getRowWidth(),
                2, 32, 2);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        this.renderListBackground(guiGraphics);
        this.enableScissor(guiGraphics);
        this.renderListItems(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
        this.renderListSeparators(guiGraphics);
        //if (this.scrollable()) {
        //    int i = this.scrollBarX();
        //    int j = this.scrollerHeight();
        //    int k = this.scrollBarY();
        //    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_BACKGROUND_SPRITE, i, this.getY(), 6, this.getHeight());
        //    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, i, k, 6, j);
        //}
        this.renderScrollbar(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void enableScissor(GuiGraphics guigraphics) {
        guigraphics.enableScissor(
                this.x,
                this.y,
                this.x1,
                this.y1);
    }

    @Override
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
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

    public TSelectList<Element> setBackground(int background) {
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

    public int getItemHeight() {
        return defaultEntryHeight;
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        Element content;
        Consumer<TSelectList<Element>> consumer;
        protected Integer specialForeground = null;

        public Entry(Element content, Consumer<TSelectList<Element>> consumer) {
            this.content = content;
            this.consumer = consumer;
        }

        public Entry(Element content) {
            this.content = content;
            this.consumer = list -> {
            };
        }

        @Override
        public Component getNarration() {
            if (content instanceof Component c) {
                return c;
            }
            Language language = Language.getInstance();
            String s = content.toString();
            if (language.has(s)) {
                return Component.translatable(s);
            } else {
                return Component.literal(s);
            }
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float a) {
            Font font = Minecraft.getInstance().font;
            int color = specialForeground == null ? (getSelected() == this ? selectedForeGround : foreground) : specialForeground;
            int left = getContentX();
            int top = getContentY();
            int width = TSelectList.this.getRowWidth();
            int height = TSelectList.this.defaultEntryHeight;
            TComponent.drawStringSingleLine(TSelectList.this, graphics, font, getNarration(), horizontalAlignment, left + 1, left + width - 2, top + (height - font.lineHeight) / 2, color);
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
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return true;
        }

        public void onSelected() {
            consumer.accept(TSelectList.this);
        }

        public void setConsumer(Consumer<TSelectList<Element>> consumer) {
            this.consumer = consumer;
        }

        public Element getContent() {
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
