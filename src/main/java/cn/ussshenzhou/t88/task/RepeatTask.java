package cn.ussshenzhou.t88.task;

import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class RepeatTask extends SimpleTask {
    protected final int coolDown;

    public RepeatTask(Consumer<Task> run, int countDown, int coolDown) {
        super(run, countDown);
        this.coolDown = coolDown;
    }

    public RepeatTask(Runnable run, int countDown, int coolDown) {
        super(run, countDown);
        this.coolDown = coolDown;
    }

    @Override
    public void tick() {
        if (cancel) {
            shouldRemove = true;
            return;
        }
        countDown--;
        if (countDown <= 0) {
            if (consumer == null) {
                //noinspection DataFlowIssue
                run.run();
            } else {
                consumer.accept(this);
            }
            countDown = coolDown;
        }
    }
}
