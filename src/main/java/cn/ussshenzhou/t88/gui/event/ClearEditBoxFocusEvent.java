package cn.ussshenzhou.t88.gui.event;

import net.neoforged.bus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class ClearEditBoxFocusEvent extends Event {
    public final double pMouseX;
    public final double pMouseY;

    public ClearEditBoxFocusEvent(double pMouseX, double pMouseY) {
        this.pMouseX = pMouseX;
        this.pMouseY = pMouseY;
    }
}
