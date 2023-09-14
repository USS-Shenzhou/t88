package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.event.ClearEditBoxFocusEvent;
import cn.ussshenzhou.t88.gui.event.TWidgetContentUpdatedEvent;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.AccessorProxy;
import cn.ussshenzhou.t88.gui.util.VanillaWidget2TComponentHelper;
import org.joml.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TEditBox extends EditBox implements TWidget, TResponder<String> {
    TComponent parent = null;
    TScreen parentScreen = null;
    protected final LinkedList<Consumer<String>> responders = new LinkedList<>();

    @SubscribeEvent
    public void onClick(ClearEditBoxFocusEvent event) {
        this.setFocused(false);
    }

    public TEditBox(Component tipText) {
        super(Minecraft.getInstance().font, 0, 0, 0, 0, tipText);
        this.setMaxLength(32500);
        MinecraftForge.EVENT_BUS.register(this);
        setResponder(this::respond);
        this.addResponder(s -> MinecraftForge.EVENT_BUS.post(new TWidgetContentUpdatedEvent(this)));
    }

    @Override
    public void onFinalClose() {
        MinecraftForge.EVENT_BUS.unregister(this);
        TWidget.super.onFinalClose();
    }

    public TEditBox() {
        this(Component.empty());
    }

    public int getCursorX() {
        return getXT() + Minecraft.getInstance().font.width(getValue().substring(AccessorProxy.EditBoxProxy.getDisplayPos(this), getCursorPosition()));
    }

    public int getCurrentWordBeginX() {
        String s = getValue();
        int b = s.lastIndexOf(" ", getCursorPosition());
        if (b == getCursorPosition()) {
            b = s.lastIndexOf(" ", Mth.clamp(getCursorPosition() - 1, 0, Integer.MAX_VALUE));
        }
        b++;
        Font font = Minecraft.getInstance().font;
        return getXT() + font.width(s.substring(AccessorProxy.EditBoxProxy.getDisplayPos(this), b)) + font.width(" ");
    }

    @Deprecated
    @Override
    public void setResponder(Consumer<String> pResponder) {
        super.setResponder(pResponder);
    }

    @Override
    public void respond(String value) {
        responders.forEach(stringConsumer -> stringConsumer.accept(value));
    }

    @Override
    public void addResponder(Consumer<String> responder) {
        responders.add(responder);
    }

    @Override
    public void clearResponders() {
        responders.clear();
    }

    @Override
    public boolean isVisibleT() {
        return this.isVisible();
    }

    @Override
    public void setVisibleT(boolean visible) {
        this.setVisible(visible);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        VanillaWidget2TComponentHelper.setBounds(x, y, width, height, this);
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setFocused(boolean pIsFocused) {
        super.setFocused(pIsFocused);
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
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(this.width, 20);
    }

    @Override
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    @Override
    public void tickT() {
        this.tick();
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY)) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (AccessorProxy.EditBoxProxy.isEditBoxEdible(this)) {
            if (isInRange(pMouseX, pMouseY)) {
                this.setFocused(true);
                return super.mouseClicked(pMouseX, pMouseY, pButton);
            }
        }
        return false;
    }

    @Override
    public void setEditable(boolean pEnabled) {
        String s = Language.getInstance().getOrDefault("gui.t88.invalid");
        if (!pEnabled && "".equals(getValue())) {
            setValue(s);
            AccessorProxy.EditBoxProxy.setDisplayPos(this, 0);
        } else if (pEnabled && s.equals(getValue())) {
            setValue("");
        }
        super.setEditable(pEnabled);
    }

    public boolean isEditable() {
        return AccessorProxy.EditBoxProxy.isEditBoxEdible(this);
    }
}
