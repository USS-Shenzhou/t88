package cn.ussshenzhou.t88.task;


import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class SimpleTask implements Task {
    protected final Consumer<Task> consumer;
    protected final Runnable run;
    protected int countDown;
    protected boolean shouldRemove = false;
    protected boolean cancel = false;

    public SimpleTask(Consumer<Task> consumer, int countDown) {
        this.consumer = consumer;
        this.countDown = countDown;
        this.run = null;
    }

    public SimpleTask(Runnable run, int countDown) {
        this.run = run;
        this.countDown = countDown;
        this.consumer = null;
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
            shouldRemove = true;
        }
    }

    @Override
    public boolean shouldRemove() {
        return shouldRemove;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
