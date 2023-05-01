package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import javax.annotation.Nullable;


/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface TWidget extends Renderable, GuiEventListener {

    boolean isVisibleT();

    void setVisibleT(boolean visible);

    void setBounds(int x, int y, int width, int height);

    void setAbsBounds(int x, int y, int width, int height);

    default void setBounds(int x, int y, Vec2i size) {
        setBounds(x, y, size.x, size.y);
    }

    default void setAbsBounds(int x, int y, Vec2i size) {
        setAbsBounds(x, y, size.x, size.y);
    }

    void setParent(TComponent parent);

    @Nullable
    TComponent getParent();

    void setParentScreen(@Nullable TScreen parentScreen);

    @Nullable
    TScreen getParentScreen();

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    int getXT();

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    int getYT();

    Vec2i getPreferredSize();

    Vec2i getSize();

    void tickT();

    default void renderTop(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    }

    default boolean isInRange(double pMouseX, double pMouseY) {
        return isInRange(pMouseX, pMouseY, 0, 0);
    }

    default boolean isInRange(double pMouseX, double pMouseY, double xPadding, double yPadding) {
        return pMouseX >= getXT() - xPadding && pMouseX <= getXT() + getSize().x + xPadding && pMouseY >= getYT() - yPadding && pMouseY <= getYT() + getSize().y + yPadding;
    }

    default void onFinalClose() {
    }

    @SuppressWarnings("unchecked")
    default @Nullable <T extends TWidget> T getParentInstanceOf(Class<T> c) {
        TWidget son = this;
        while (son.getParent() != null) {
            TWidget parent = son.getParent();
            if (c.isInstance(parent)) {
                return (T) parent;
            } else {
                son = parent;
            }
        }
        return null;
    }

    default double getParentScrollAmountIfExist() {
        TScrollPanel tScrollPanel = this.getParentInstanceOf(TScrollPanel.class);
        if (tScrollPanel != null) {
            return tScrollPanel.getScrollAmount();
        }
        return 0;
    }
}
