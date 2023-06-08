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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.LinkedList;

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
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        renderBackGround(graphics, pMouseX, pMouseY, pPartialTick);
        LinkedList<Renderable> renderTop = new LinkedList<>();
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
    }

    public void add(TWidget tWidget) {
        tChildren.add(tWidget);
        tWidget.setParentScreen(this);
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

    protected void renderBackGround(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(graphics);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        MinecraftForge.EVENT_BUS.post(new ClearEditBoxFocusEvent());
        for (TWidget tWidget : tChildren) {
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
        for (TWidget tWidget : tChildren) {
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
        for (TWidget tWidget : tChildren) {
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
        for (TWidget tWidget : tChildren) {
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
            tChildren.forEach(TWidget::onFinalClose);
        }
        ForgeHooksClient.popGuiLayer(Minecraft.getInstance());
    }
}
