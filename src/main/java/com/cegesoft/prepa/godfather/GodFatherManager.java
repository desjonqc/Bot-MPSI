package com.cegesoft.prepa.godfather;

import com.cegesoft.prepa.server.Server;

import java.io.IOException;

public class GodFatherManager {
    private final Server server;
    private GodFatherHandler godFatherHandler;
    private GodFatherFileHandler godFatherFileHandler;

    public GodFatherManager(Server server) {
        this.server = server;
    }

    public void preLoad() throws IOException {
        godFatherFileHandler = new GodFatherFileHandler("_" + this.server.getId());
    }

    public void postLoad() {
        godFatherHandler = new GodFatherHandler(this.server);
    }

    public GodFatherHandler getGodFatherHandler() {
        return godFatherHandler;
    }

    public GodFatherFileHandler getGodFatherFileHandler() {
        return godFatherFileHandler;
    }
}
