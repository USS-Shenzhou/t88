package cn.ussshenzhou.t88.task;

/**
 * @author USS_Shenzhou
 */
public interface Task {

    void tick();

    boolean shouldRemove();

    void cancel();
}
