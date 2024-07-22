package cn.ussshenzhou.t88.gui.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class GameRendererRenderedEvent extends Event {
    private final DeltaTracker deltaTracker;
    private final GuiGraphics graphics;

    public GameRendererRenderedEvent(DeltaTracker deltaTracker, GuiGraphics graphics) {
        this.deltaTracker = deltaTracker;
        this.graphics = graphics;
    }

    public DeltaTracker getDeltaTracker() {
        return deltaTracker;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }
}
