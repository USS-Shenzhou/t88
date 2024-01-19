package cn.ussshenzhou.t88.task;

import com.google.errorprone.annotations.DoNotCall;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaskHelper {
    private static final LinkedList<Task> SERVER_ADD = new LinkedList<>();
    private static final LinkedList<Task> CLIENT_ADD = new LinkedList<>();
    private static final LinkedList<Task> SERVER_TASKS = new LinkedList<>();
    private static final LinkedList<Task> CLIENT_TASKS = new LinkedList<>();
    private static final LinkedList<Task> REMOVE = new LinkedList<>();

    @DoNotCall
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            REMOVE.clear();
            CLIENT_TASKS.addAll(CLIENT_ADD);
            CLIENT_ADD.clear();
            for (Task task : CLIENT_TASKS) {
                task.tick();
                if (task.shouldRemove()) {
                    REMOVE.add(task);
                }
            }
            CLIENT_TASKS.removeAll(REMOVE);
        }
    }

    @DoNotCall
    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            REMOVE.clear();
            SERVER_TASKS.addAll(SERVER_ADD);
            SERVER_ADD.clear();
            for (Task task : SERVER_TASKS) {
                task.tick();
                if (task.shouldRemove()) {
                    REMOVE.add(task);
                }
            }
            SERVER_TASKS.removeAll(REMOVE);
        }
    }

    public static Task addServerTask(Task task) {
        SERVER_ADD.add(task);
        return task;
    }

    public static Task addClientTask(Task task) {
        CLIENT_ADD.add(task);
        return task;
    }

    public static SimpleTask addServerTask(Consumer<Task> task, int delay) {
        var t = new SimpleTask(task, delay + 1);
        SERVER_ADD.add(t);
        return t;
    }

    public static SimpleTask addClientTask(Consumer<Task> task, int delay) {
        var t = new SimpleTask(task, delay + 1);
        CLIENT_ADD.add(t);
        return t;
    }

    public static RepeatTask addServerRepeatTask(Consumer<Task> task, int delay, int cd) {
        var t = new RepeatTask(task, delay + 1, cd);
        SERVER_ADD.add(t);
        return t;
    }

    public static RepeatTask addClientRepeatTask(Consumer<Task> task, int delay, int cd) {
        var t = new RepeatTask(task, delay + 1, cd);
        CLIENT_ADD.add(t);
        return t;
    }

    public static SimpleTask addServerTask(Runnable task, int delay) {
        var t = new SimpleTask(task, delay + 1);
        SERVER_ADD.add(t);
        return t;
    }

    public static SimpleTask addClientTask(Runnable task, int delay) {
        var t = new SimpleTask(task, delay + 1);
        CLIENT_ADD.add(t);
        return t;
    }

    public static RepeatTask addServerRepeatTask(Runnable task, int delay, int cd) {
        var t = new RepeatTask(task, delay + 1, cd);
        SERVER_ADD.add(t);
        return t;
    }

    public static RepeatTask addClientRepeatTask(Runnable task, int delay, int cd) {
        var t = new RepeatTask(task, delay + 1, cd);
        CLIENT_ADD.add(t);
        return t;
    }
}
