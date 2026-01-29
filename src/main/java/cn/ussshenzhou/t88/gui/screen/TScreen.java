package cn.ussshenzhou.t88.gui.screen;

import cn.ussshenzhou.t88.gui.event.ClearEditBoxFocusEvent;
import cn.ussshenzhou.t88.gui.widegt.TComponent;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        graphics.pose().pushMatrix();
        for (TWidget w : this.tChildren) {
            if (w.isVisibleT()) {
                w.render(graphics, mouseX, mouseY, pPartialTick);
            }
        }
        for (TWidget w : this.tChildren) {
            if (w.isVisibleT()) {
                w.renderTop(graphics, mouseX, mouseY, pPartialTick);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        NeoForge.EVENT_BUS.post(new ClearEditBoxFocusEvent(event.x(), event.y()));
        for (TWidget tWidget : reversed(tChildren)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseClicked(event, doubleClick)) {
                this.setFocused(tWidget);
                if (event.button() == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.setDragging(false);
        for (TWidget tWidget : reversed(tChildren)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseReleased(event)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        for (TWidget tWidget : reversed(tChildren)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseDragged(event, dx, dy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        for (TWidget tWidget : reversed(tChildren)) {
            if (!tWidget.isVisibleT()) {
                continue;
            }
            if (tWidget.mouseScrolled(mouseX, mouseY, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256 && this.shouldCloseOnEsc()) {
            this.onClose(true);
            return true;
        } else {
            return this.getFocused() != null && this.getFocused().keyPressed(event);
        }
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return this.getFocused() != null && this.getFocused().keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return this.getFocused() != null && this.getFocused().charTyped(event);
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
