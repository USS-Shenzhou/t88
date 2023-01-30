package cn.ussshenzhou.t88.gui.event;

import cn.ussshenzhou.t88.gui.widegt.TTimer;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author USS_Shenzhou
 */
public class TimerCountdownReachEvent extends Event {
    private final TTimer reachedTimer;

    public TimerCountdownReachEvent(TTimer reachedTimer) {
        this.reachedTimer = reachedTimer;
    }

    public TTimer getReachedTimer() {
        return reachedTimer;
    }
}
