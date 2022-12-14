package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.event.EditBoxFocusedEvent;
import cn.ussshenzhou.t88.gui.event.TWidgetContentUpdatedEvent;
import cn.ussshenzhou.t88.gui.util.AccessorProxy;
import cn.ussshenzhou.t88.gui.util.MWidget2TComponentHelper;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TEditBox extends EditBox implements TWidget, TResponder<String> {
    TComponent parent = null;
    protected final LinkedList<Consumer<String>> responders = new LinkedList<>();

    @SubscribeEvent
    public void onEditBoxFocused(EditBoxFocusedEvent event) {
        if (event.getWillFocused() != this) {
            this.setFocus(false);
        }
    }

    public TEditBox(Component tipText) {
        super(Minecraft.getInstance().font, 0, 0, 0, 0, tipText);
        MinecraftForge.EVENT_BUS.register(this);
        setResponder(this::respond);
        this.addResponder(s -> MinecraftForge.EVENT_BUS.post(new TWidgetContentUpdatedEvent(this)));
    }

    public TEditBox() {
        this(new TextComponent(""));
    }

    public int getCursorX() {
        return getX() + Minecraft.getInstance().font.width(getValue().substring(AccessorProxy.EditBoxProxy.getDisplayPos(this), getCursorPosition()));
    }

    public int getCurrentWordBeginX() {
        String s = getValue();
        int b = s.lastIndexOf(" ", getCursorPosition());
        if (b == getCursorPosition()) {
            b = s.lastIndexOf(" ", Mth.clamp(getCursorPosition() - 1, 0, Integer.MAX_VALUE));
        }
        b++;
        Font font = Minecraft.getInstance().font;
        return getX() + font.width(s.substring(AccessorProxy.EditBoxProxy.getDisplayPos(this), b)) + font.width(" ");
    }

    @Override
    public void renderHighlight(int pStartX, int pStartY, int pEndX, int pEndY) {
        double scroll = -this.getParentScrollAmountIfExist();
        super.renderHighlight(pStartX, (int) (pStartY + scroll), pEndX, (int) (pEndY + scroll));
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
        MWidget2TComponentHelper.setBounds(x, y, width, height, this);
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setFocus(boolean pIsFocused) {
        if (pIsFocused) {
            MinecraftForge.EVENT_BUS.post(new EditBoxFocusedEvent(this));
        }
        super.setFocus(pIsFocused);
    }

    @Override
    public void onFinalClose() {
        MinecraftForge.EVENT_BUS.unregister(this);
        TWidget.super.onFinalClose();
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
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Vec2i getPreferredSize() {
        return new Vec2i(this.width, 20);
    }

    @Override
    public Vec2i getSize() {
        return new Vec2i(width, height);
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
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        } else {
            return false;
        }
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
