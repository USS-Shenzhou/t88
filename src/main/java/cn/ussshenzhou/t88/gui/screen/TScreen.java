package cn.ussshenzhou.t88.gui.screen;

import cn.ussshenzhou.t88.gui.event.ClearEditBoxFocusEvent;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
public abstract class TScreen extends Screen {
    protected boolean needRelayout = true;
    protected LinkedList<TWidget> tChildren = new LinkedList<>();

    public TScreen(Component pTitle) {
        super(pTitle);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        needRelayout = true;
    }

    @Override
    public void tick() {
        if (needRelayout) {
            layout();
            needRelayout = false;
        }
        for (TWidget child : tChildren) {
            child.tickT();
        }
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.pose().pushMatrix();
        for (TWidget w : this.tChildren) {
            if (w.isVisibleT()) {
                w.render(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
        for (TWidget w : this.tChildren) {
            if (w.isVisibleT()) {
                w.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
            }
        }
        graphics.pose().pushMatrix();
    }

    public void add(TWidget tWidget) {
        tChildren.add(tWidget);
        tWidget.setParentScreen(this);
    }

    public void addAll(TWidget... children) {
        Stream.of(children).forEach(this::add);
    }

    public void addAll(Collection<TWidget> children) {
        children.forEach(this::add);
    }

    public void remove(TWidget tWidget) {
        tChildren.remove(tWidget);
        tWidget.setParentScreen(null);
    }


    public void layout() {
        for (TWidget w : this.tChildren) {
            if (w instanceof TComponent t) {
                t.layout();
            }
        }
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
        NeoForge.EVENT_BUS.post(new ClearEditBoxFocusEvent(pMouseX, pMouseY));
        for (TWidget tWidget : reversed(tChildren)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseClicked(pMouseX, pMouseY, pButton)) {
                this.setFocused(tWidget);
                if (pButton == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.setDragging(false);
        for (TWidget tWidget : reversed(tChildren)) {
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
        for (TWidget tWidget : reversed(tChildren)) {
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
        for (TWidget tWidget : reversed(tChildren)) {
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
        if (pKeyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose(true);
            return true;
        } else {
            return this.getFocused() != null && this.getFocused().keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return this.getFocused() != null && this.getFocused().keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return this.getFocused() != null && this.getFocused().charTyped(pCodePoint, pModifiers);
    }

    @Deprecated
    @Override
    public void onClose() {
        onClose(true);
    }

    public void onClose(boolean isFinal) {
        if (isFinal) {
            reversed(tChildren).forEach(TWidget::onFinalClose);
        }
        ClientHooks.popGuiLayer(Minecraft.getInstance());
    }
}
