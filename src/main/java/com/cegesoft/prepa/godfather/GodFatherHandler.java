package com.cegesoft.prepa.godfather;

import com.cegesoft.prepa.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GodFatherHandler {

    private final Server server;
    private final List<GFGroup> group;

    public GodFatherHandler(Server server) {
        this.server = server;
        this.group = server.getGodFatherManager().getGodFatherFileHandler().load();
    }

    public void saveGroups() {
        try {
            this.server.getGodFatherManager().getGodFatherFileHandler().save(this.group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGroup(GFGroup group) {
        this.group.add(group);
        this.saveGroups();
    }

    public void removeGroup(GFGroup group) {
        this.group.remove(group);
        this.saveGroups();
    }

    public List<GFGroup> getGroups() {
        return group;
    }

    public Optional<GFGroup> getByGF(String gf) {
        for (GFGroup gfGroup : group) {
            if (gfGroup.containsGodFather(gf)) {
                return Optional.of(gfGroup);
            }
        }
        return Optional.empty();
    }

    public Optional<GFGroup> getByGS(String gs) {
        for (GFGroup gfGroup : group) {
            if (gfGroup.containsGodSon(gs)) {
                return Optional.of(gfGroup);
            }
        }
        return Optional.empty();
    }
}
