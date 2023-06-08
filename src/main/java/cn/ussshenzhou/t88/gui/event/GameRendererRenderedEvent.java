package cn.ussshenzhou.t88.gui.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class GameRendererRenderedEvent extends Event {
    private final float partialTick;
    private final GuiGraphics graphics;

    public GameRendererRenderedEvent(float partialTick, GuiGraphics graphics) {
        this.partialTick = partialTick;
        this.graphics = graphics;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }
}
