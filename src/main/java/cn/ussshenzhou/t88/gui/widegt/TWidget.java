package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import org.joml.Vector2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;


/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface TWidget extends Renderable, GuiEventListener {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");

    boolean isVisibleT();

    void setVisibleT(boolean visible);

    void setBounds(int x, int y, int width, int height);

    void setAbsBounds(int x, int y, int width, int height);

    default void setBounds(int x, int y, Vector2i size) {
        setBounds(x, y, size.x, size.y);
    }

    default void setAbsBounds(int x, int y, Vector2i size) {
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

    Vector2i getPreferredSize();

    Vector2i getSize();

    void tickT();

    default void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
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
        TScrollContainer tScrollContainer = this.getParentInstanceOf(TScrollContainer.class);
        if (tScrollContainer != null) {
            return tScrollContainer.getScrollAmount();
        }
        return 0;
    }

    default boolean isOutOfParentScrollContainerScissor() {
        var scrollContainer = this.getParentInstanceOf(TScrollContainer.class);
        if (scrollContainer == null) {
            return false;
        }
        var scroll = scrollContainer.getScrollAmount();
        //noinspection RedundantIfStatement
        if (this.getYT() - scroll < scrollContainer.getYT()
                || this.getYT() + this.getSize().y - scroll > scrollContainer.getYT() + scrollContainer.height) {
            return true;
        }
        return false;
    }
}
