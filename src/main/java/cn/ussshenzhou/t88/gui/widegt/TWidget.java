package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.joml.Vector2d;
import org.joml.Vector2i;

import javax.annotation.Nullable;

import java.util.Optional;

import static cn.ussshenzhou.t88.T88.MOD_ID;


/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface TWidget extends Renderable, GuiEventListener {
    public static final Identifier BACKGROUND_LOCATION = Identifier.parse("textures/gui/options_background.png");
    public static final Identifier PLACEHOLDER_IMAGE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/placeholder.png");

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

    default TComponent getParentLazy() {
        var p = getParent();
        if (p == null) {
            LogUtils.getLogger().warn("Failed to get parent of {}. Things may not work well.", this);
            return new TPanel();
        }
        return p;
    }

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

    void layout();

    default void renderTop(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
    }

    default boolean isInRangeNow() {
        return isInRange(MouseHelper.getMouseX(), MouseHelper.getMouseY(), 0, 0);
    }

    default boolean isInRangeNow(double xPadding, double yPadding) {
        return isInRange(MouseHelper.getMouseX(), MouseHelper.getMouseY(), xPadding, yPadding);
    }

    default boolean isInRange(MouseButtonEvent event) {
        return isInRange(event, 0, 0);
    }

    default boolean isInRange(MouseButtonEvent event, double xPadding, double yPadding) {
        return isInRange(event.x(), event.y(), xPadding, yPadding);
    }

    default boolean isInRange(double mouseX, double mouseY) {
        return isInRange(mouseX, mouseY, 0, 0);
    }

    default boolean isInRange(double mouseX, double mouseY, double xPadding, double yPadding) {
        return mouseX >= getXT() - xPadding && mouseX <= getXT() + getSize().x + xPadding && mouseY >= getYT() - yPadding && mouseY <= getYT() + getSize().y + yPadding;
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

    default <T extends TWidget> Optional<T> getParentInstanceOfOptional(Class<T> c) {
        return Optional.ofNullable(getParentInstanceOf(c));
    }

    default @Nullable TScreen getTopParentScreen() {
        TWidget t = this;
        while (t != null) {
            if (t.getParentScreen() != null) {
                return t.getParentScreen();
            }
            t = t.getParent();
        }
        return null;
    }

    default Optional<TScreen> getTopParentScreenOptional() {
        return Optional.ofNullable(getTopParentScreen());
    }

    default <T extends TScreen> Optional<T> getTopParentScreenAsOptional(Class<T> c) {
        var s = getTopParentScreen();
        //noinspection unchecked
        return c.isInstance(s) ? Optional.of((T) s) : Optional.empty();
    }

    @Deprecated
    default double getParentScrollAmountIfExist() {
        return getParentScroll().y;
        /*TVerticalScrollContainer tVerticalScrollContainer = this.getParentInstanceOf(TVerticalScrollContainer.class);
        if (tVerticalScrollContainer != null) {
            return tVerticalScrollContainer.getScrollAmount();
        }
        return 0;*/
    }

    default boolean isOutOfParentScrollContainerScissor() {
        var scrollContainer = this.getParentInstanceOf(TScrollContainer.class);
        if (scrollContainer == null) {
            return false;
        }
        var scroll = getParentScroll();
        //noinspection RedundantIfStatement
        if (this.getYT() - scroll.y < scrollContainer.getYT()
                || this.getYT() + this.getSize().y - scroll.y > scrollContainer.getYT() + scrollContainer.getSize().y
                || this.getXT() - scroll.x < scrollContainer.getXT()
                || this.getXT() + this.getSize().x - scroll.x > scrollContainer.getXT() + scrollContainer.getSize().x) {
            return true;
        }
        return false;
    }

    default Vector2d getParentScroll() {
        Vector2d v = new Vector2d();
        getParentInstanceOfOptional(TScrollContainer.class).ifPresent(tScrollContainer -> v.add(tScrollContainer.getScroll()));
        return v;
    }
}
