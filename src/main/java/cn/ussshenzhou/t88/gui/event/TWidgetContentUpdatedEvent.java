package cn.ussshenzhou.t88.gui.event;

import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.neoforged.bus.api.Event;

/**
 * @author Tony Yu
 */
public class TWidgetContentUpdatedEvent extends Event {
    private final TWidget updated;

    public TWidgetContentUpdatedEvent(TWidget updated) {
        this.updated = updated;
    }

    public TWidget getUpdated() {
        return updated;
    }
}
