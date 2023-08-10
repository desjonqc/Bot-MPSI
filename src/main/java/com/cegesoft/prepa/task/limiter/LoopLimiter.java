package com.cegesoft.prepa.task.limiter;

import com.cegesoft.prepa.task.ITaskLimiter;

/**
 * Created by HoxiSword on 26/05/2020 for JARVIS
 */
public class LoopLimiter implements ITaskLimiter {

    private final long loops;

    public LoopLimiter(long loops) {
        this.loops = loops;
    }

    @Override
    public void start() {}

    @Override
    public boolean isContinue(long loops) {
        return loops < this.loops;
    }
}
