package com.cegesoft.prepa.task;

import com.cegesoft.prepa.Main;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HoxiSword on 25/05/2020 for JARVIS
 */
public class TaskManager {

    private final ConcurrentHashMap<TaskId, Task> tasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TaskId, Date> program = new ConcurrentHashMap<>();
    private Thread thread;
    private boolean started;

    public TaskManager() {

    }

    public void start() {
        if (started)
            return;
        this.thread = new Thread(() -> {
            while (started) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    List<TaskId> launched = new ArrayList<>();
                    for (Map.Entry<TaskId, Date> entry : program.entrySet()) {
                        Calendar current = Calendar.getInstance();
                        current.setTime(entry.getValue());
                        if (calendar.after(current)) {
                            tasks.get(entry.getKey()).start();
                            launched.add(entry.getKey());
                        }
                    }
                    launched.forEach(program::remove);
                    this.tasks.entrySet().stream().filter(entry -> entry.getValue().getInfo().getState() == Task.TaskState.INTERRUPT).forEach(entry -> {
                        this.tasks.remove(entry.getKey());
                    });
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    started = false;
                }
            }
        }, "TASK-LAUNCHER");
        this.started = true;
        this.thread.start();
    }

    public static Task getTask(TaskId id) {
        if (id == null)
            return null;
        return Main.taskManager.tasks.getOrDefault(id, null);
    }

    public static TaskInfo getTaskInfo(TaskId id) {
        return getTask(id).getInfo();
    }

    public void stop() {
        if (!started || this.thread == null)
            return;
        this.thread.interrupt();
    }

    public static TaskId scheduleTaskAt(Runnable runnable, Date date) {
        return scheduleTaskAt(runnable, null, date);
    }

    public static TaskId scheduleTaskLaterAt(Runnable runnable, long delay, Date date) {
        return scheduleTaskLaterAt(runnable, null, delay, date);
    }

    public static TaskId scheduleTaskTimerAt(Runnable runnable, long delay, long timer, Date date) {
        return scheduleTaskTimerAt(runnable, null, delay, timer, date);
    }

    public static TaskId scheduleTaskUntilAt(Runnable runnable, long delay, long timer, ITaskLimiter until, Date date) {
        return scheduleTaskUntilAt(runnable, null, delay, timer, until, date);
    }

    public static TaskId scheduleTask(TaskParameter parameter) {
        return scheduleTask(parameter, null);
    }

    public static TaskId scheduleTask(Runnable runnable) {
        return scheduleTaskAt(runnable, new Date());
    }

    public static TaskId scheduleTaskLater(Runnable runnable, long delay) {
        return scheduleTaskLaterAt(runnable, delay, new Date());
    }

    public static TaskId scheduleTaskTimer(Runnable runnable, long delay, long timer) {
        return scheduleTaskTimerAt(runnable, delay, timer, new Date());
    }

    public static TaskId scheduleTaskUntil(Runnable runnable, long delay, long timer, ITaskLimiter until) {
        return scheduleTaskUntilAt(runnable, delay, timer, until, new Date());
    }

    public static TaskId scheduleTaskAt(Runnable runnable, String name, Date date) {
        TaskManager manager = Main.taskManager;
        TaskId id = TaskId.generate();
        manager.tasks.put(id, new Task(id, runnable, 0).setTaskName(name));
        manager.program.put(id, date);
        return id;
    }

    public static TaskId scheduleTaskLaterAt(Runnable runnable, String name, long delay, Date date) {
        TaskManager manager = Main.taskManager;
        TaskId id = TaskId.generate();
        manager.tasks.put(id, new Task(id, runnable, delay).setTaskName(name));
        manager.program.put(id, date);
        return id;
    }

    public static TaskId scheduleTaskTimerAt(Runnable runnable, String name, long delay, long timer, Date date) {
        TaskManager manager = Main.taskManager;
        TaskId id = TaskId.generate();
        manager.tasks.put(id, new Task(id, runnable, delay, timer).setTaskName(name));
        manager.program.put(id, date);
        return id;
    }

    public static TaskId scheduleTaskUntilAt(Runnable runnable, String name, long delay, long timer, ITaskLimiter until, Date date) {
        TaskManager manager = Main.taskManager;
        TaskId id = TaskId.generate();
        manager.tasks.put(id, new Task(id, runnable, delay, timer, until).setTaskName(name));
        manager.program.put(id, date);
        return id;
    }

    public static TaskId scheduleTask(TaskParameter parameter, String name) {
        TaskManager manager = Main.taskManager;
        TaskId id = TaskId.generate();
        manager.tasks.put(id, parameter.toTask(id).setTaskName(name));
        manager.program.put(id, parameter.getDate());
        return id;
    }

    public static TaskId scheduleTask(Runnable runnable, String name) {
        return scheduleTaskAt(runnable, name, new Date());
    }

    public static TaskId scheduleTaskLater(Runnable runnable, String name, long delay) {
        return scheduleTaskLaterAt(runnable, name, delay, new Date());
    }

    public static TaskId scheduleTaskTimer(Runnable runnable, String name, long delay, long timer) {
        return scheduleTaskTimerAt(runnable, name, delay, timer, new Date());
    }

    public static TaskId scheduleTaskUntil(Runnable runnable, String name, long delay, long timer, ITaskLimiter until) {
        return scheduleTaskUntilAt(runnable, name, delay, timer, until, new Date());
    }
}
