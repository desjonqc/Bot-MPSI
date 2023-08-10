package com.cegesoft.prepa.task;

import java.util.Date;

/**
 * Created by HoxiSword on 26/05/2020 for JARVIS
 */
public class TaskParameter {

    private long delayed;
    private Date programmedDate;
    private long timer;
    private com.cegesoft.prepa.task.ITaskLimiter limiter;
    private final Runnable runnable;

    public TaskParameter(Runnable runnable) {
        programmedDate = new Date();
        this.runnable = runnable;
    }

    public TaskParameter setDelayed(long delayed) {
        this.delayed = delayed;
        return this;
    }

    public TaskParameter setProgrammedDate(Date programmedDate) {
        this.programmedDate = programmedDate;
        return this;
    }

    public TaskParameter setTimer(long timer) {
        this.timer = timer;
        return this;
    }

    public TaskParameter setLimiter(ITaskLimiter limiter) {
        this.limiter = limiter;
        return this;
    }

    public Task toTask(TaskId id) {
        if (timer == 0 && limiter == null)
            return new Task(id, runnable, delayed);
        if (limiter == null)
            return new Task(id, runnable, delayed, timer);
        return new Task(id, runnable, delayed, timer, limiter);
    }

    public Date getDate() {
        return programmedDate;
    }

}
