package cn.ussshenzhou.t88.gui.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class GameRendererRenderedEvent extends Event {
    private final float partialTick;
    private final PoseStack poseStack;

    public GameRendererRenderedEvent(float partialTick, PoseStack poseStack) {
        this.partialTick = partialTick;
        this.poseStack = poseStack;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
