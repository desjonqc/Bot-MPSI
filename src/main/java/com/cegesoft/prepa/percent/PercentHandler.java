package com.cegesoft.prepa.percent;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.server.Server;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PercentHandler {

    private final Server server;
    private final HashMap<String, List<TenPercent>> percents = new HashMap<>();

    public PercentHandler(Server server) {
        this.server = server;
        this.percents.putAll(this.server.getPercentManager().getPercentFileHandler().load());
    }

    public HashMap<String, List<TenPercent>> getPercents() {
        return percents;
    }

    public List<TenPercent> getFrom(String student) {
        return percents.get(student.toLowerCase());
    }

    public void addPercents(TenPercent percent) {
        if (!percents.containsKey(percent.getStudent().toLowerCase())) {
            percents.put(percent.getStudent().toLowerCase(), new ArrayList<>());
        }
        percents.get(percent.getStudent().toLowerCase()).add(percent);
        try {
            server.getPercentManager().getPercentFileHandler().save(this.percents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePercents(int id) {
        this.percents.values().forEach(list -> new ArrayList<>(list).stream().filter(percent -> percent.getId() == id).forEach(list::remove));
        try {
            server.getPercentManager().getPercentFileHandler().save(this.percents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalFrom(List<TenPercent> percents) {
        int i = 0;
        for (TenPercent percent : percents) {
            i += percent.getValue();
        }
        return i;
    }

    public List<String> getClassement() {
        List<String> keys = this.percents.entrySet().stream().sorted(Comparator.comparingInt(entry -> getTotalFrom(((Map.Entry<String, List<TenPercent>>)entry).getValue())).reversed()).sorted().map(Map.Entry::getKey).limit(10).collect(Collectors.toCollection(ArrayList::new));
        List<String> returner = new ArrayList<>();
        for (String s : keys) {
            List<TenPercent> key = this.percents.get(s);
            if (key.isEmpty())
                continue;
            returner.add(key.get(0).getStudent() + " avec **-" + getTotalFrom(key) + "%**");
        }
        return returner;
    }

}
