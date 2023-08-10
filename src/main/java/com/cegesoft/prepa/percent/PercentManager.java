package com.cegesoft.prepa.percent;

import com.cegesoft.prepa.server.Server;

import java.io.IOException;

public class PercentManager {

    private final Server server;
    private PercentHandler percentHandler;
    private PercentFileHandler percentFileHandler;

    public PercentManager(Server server) {
        this.server = server;
    }

    public void preLoad() throws IOException {
        percentFileHandler = new PercentFileHandler("_" + this.server.getId());
    }

    public void postLoad() {
        percentHandler = new PercentHandler(this.server);
    }

    public PercentHandler getPercentHandler() {
        return percentHandler;
    }

    public PercentFileHandler getPercentFileHandler() {
        return percentFileHandler;
    }
}
