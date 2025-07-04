package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.util.VanillaWidget2TComponentHelper;
import org.joml.Vector2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * @author USS_Shenzhou
 */
public class TButton extends Button implements TWidget {
    public static final Vector2i RECOMMEND_SIZE = new Vector2i(52, 20);
    protected boolean visible = true;
    TComponent parent = null;
    TScreen parentScreen = null;
    protected OnPress onPress;
    protected boolean skipRenderAsBackend = false;

    public TButton(Component pMessage) {
        super(0, 0, 0, 0, pMessage, button -> {
        }, DEFAULT_NARRATION);
        this.onPress = pButton -> {
        };
    }

    public TButton(Component pMessage, OnPress pOnPress) {
        super(0, 0, 0, 0, pMessage, button -> {
        }, DEFAULT_NARRATION);
        this.onPress = pOnPress;
    }

    @Override
    public void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
        if (!skipRenderAsBackend) {
            super.renderWidget(p_281670_, p_282682_, p_281714_, p_282542_);
        }
    }

    public boolean isSkipRenderAsBackend() {
        return skipRenderAsBackend;
    }

    public void setSkipRenderAsBackend(boolean skipRenderAsBackend) {
        this.skipRenderAsBackend = skipRenderAsBackend;
    }

    public void setOnPress(OnPress onPress) {
        this.onPress = onPress;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return false;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public boolean isVisibleT() {
        return this.visible;
    }

    @Override
    public void setVisibleT(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        VanillaWidget2TComponentHelper.setBounds(x, y, width, height, this);
    }

    public void setBounds(int x, int y) {
        this.setBounds(x, y, RECOMMEND_SIZE.x, RECOMMEND_SIZE.y);
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setParent(TComponent parent) {
        this.parent = parent;
    }

    @Override
    public TComponent getParent() {
        return this.parent;
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
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
    }

    @Override
    public Vector2i getPreferredSize() {
        return RECOMMEND_SIZE;
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    @Override
    public void tickT() {

    }

    @Override
    public void layout() {

    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (isInRange(MouseHelper.getMouseX(), MouseHelper.getMouseY())) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return false;
    }
}
