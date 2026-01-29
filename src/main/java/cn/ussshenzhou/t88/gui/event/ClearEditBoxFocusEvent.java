package cn.ussshenzhou.t88.gui.event;

import net.neoforged.bus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class ClearEditBoxFocusEvent extends Event {
    public final double mouseX;
    public final double mouseY;

    public ClearEditBoxFocusEvent(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
