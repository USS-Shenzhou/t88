package cn.ussshenzhou.t88.gui.util;

import net.minecraft.client.input.MouseButtonEvent;

/**
 * @author USS_Shenzhou
 */
public class RecordHelper {

    public static MouseButtonEvent scroll(MouseButtonEvent event, double scrollAmount) {
        return new MouseButtonEvent(event.x(), event.y() + scrollAmount, event.buttonInfo());
    }

    public static MouseButtonEvent scroll(MouseButtonEvent event, double scrollX, double scrollY) {
        return new MouseButtonEvent(event.x() + scrollX, event.y() + scrollY, event.buttonInfo());
    }

    public static MouseButtonEvent reset(MouseButtonEvent event, double x, double y) {
        return new MouseButtonEvent(x, y, event.buttonInfo());
    }
}
