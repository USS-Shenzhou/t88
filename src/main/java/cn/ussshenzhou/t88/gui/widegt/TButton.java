package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.util.VanillaWidget2TComponentHelper;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Vector2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author USS_Shenzhou
 */
@ParametersAreNonnullByDefault
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
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        if (!skipRenderAsBackend) {
            super.renderWidget(graphics, mouseX, mouseY, a);
        }
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        this.renderDefaultSprite(graphics);
        this.renderDefaultLabel(graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
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
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isInRange(event)) {
            return super.mouseDragged(event, dx, dy);
        }
        return false;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.onPress(this);
    }

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
    public boolean keyPressed(KeyEvent event) {
        if (isInRangeNow()) {
            return super.keyPressed(event);
        }
        return false;
    }
}
