package cn.ussshenzhou.t88.gui.event;

import cn.ussshenzhou.t88.gui.widegt.TEditBox;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class EditBoxFocusedEvent extends Event {
    private final TEditBox willFocused;

    public EditBoxFocusedEvent(TEditBox willFocused) {
        this.willFocused = willFocused;
    }

    public TEditBox getWillFocused() {
        return willFocused;
    }
}
