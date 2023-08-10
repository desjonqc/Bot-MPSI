package com.cegesoft.prepa.task;

/**
 * Created by HoxiSword on 25/05/2020 for JARVIS
 */
public interface ITaskLimiter {

    void start();
    boolean isContinue(long loops);

}
