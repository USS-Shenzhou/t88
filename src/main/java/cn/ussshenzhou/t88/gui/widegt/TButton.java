package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.VanillaWidget2TComponentHelper;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * @author USS_Shenzhou
 */
public class TButton extends Button implements TWidget {
    public static final Vec2i RECOMMEND_SIZE = new Vec2i(52, 20);
    protected boolean visible = true;
    TComponent parent = null;
    TScreen parentScreen = null;
    protected OnPress onPress;

    public TButton(Component pMessage) {
        super(0, 0, 0, 0, pMessage, button -> {
        },DEFAULT_NARRATION);
        this.onPress = pButton -> {
        };
    }

    public TButton(Component pMessage, OnPress pOnPress) {
        super(0, 0, 0, 0, pMessage, button -> {
        },DEFAULT_NARRATION);
        this.onPress = pOnPress;
    }

    public void setOnPress(OnPress onPress) {
        this.onPress = onPress;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            //modified for compatibility with TScrollPanel
            double y = getParentScrollAmountIfExist() + pMouseY;
            this.isHovered = pMouseX >= this.x && y >= this.y && pMouseX < this.x + this.width && y < this.y + this.height;
            this.renderWidget(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
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
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Vec2i getPreferredSize() {
        return RECOMMEND_SIZE;
    }

    @Override
    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    @Override
    public void tickT() {

    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (isInRange(MouseHelper.getMouseX(), MouseHelper.getMouseY())) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return false;
    }
}
