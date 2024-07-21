package com.cegesoft.prepa.godfather;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GFGroup {

    private final List<String> gf = new ArrayList<>();
    private final List<String> gs = new ArrayList<>();

    public GFGroup() {
    }

    public GFGroup(String csv) {
        loadFromCSV(csv);
    }

    public void addGodFather(String gf) {
        this.gf.add(gf.toLowerCase());
    }

    public void addGodSon(String gs) {
        this.gs.add(gs.toLowerCase());
    }

    public boolean containsGodFather(String gf) {
        return this.gf.contains(gf.toLowerCase());
    }

    public boolean containsGodSon(String gs) {
        return this.gs.contains(gs.toLowerCase());
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append("**Parrains:** ");
        for (String s : gf) {
            String[] split = s.split(" ");
            for (String s1 : split) {
                builder.append(StringUtils.capitalize(s1));
                if (!s1.equals(split[split.length - 1])) {
                    builder.append(" ");
                }
            }
            if (!s.equals(gf.get(gf.size() - 1))) {
                builder.append(", ");
            }
        }
        builder.append("\n**Filleuls:** ");
        for (String s : gs) {
            String[] split = s.split(" ");
            for (String s1 : split) {
                builder.append(StringUtils.capitalize(s1));
                if (!s1.equals(split[split.length - 1])) {
                    builder.append(" ");
                }
            }
            if (!s.equals(gs.get(gs.size() - 1))) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public String formatCSV() {
        StringBuilder builder = new StringBuilder();
        for (String s : gf) {
            builder.append(s);
            if (!s.equals(gf.get(gf.size() - 1))) {
                builder.append(",");
            }
        }
        builder.append(";");
        for (String s : gs) {
            builder.append(s);
            if (!s.equals(gs.get(gs.size() - 1))) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public void loadFromCSV(String csv) {
        String[] split = csv.split(";");
        this.gf.addAll(Arrays.asList(split[0].split("( +)?,( +)?")));
        if (split.length > 1)
            this.gs.addAll(Arrays.asList(split[1].split("( +)?,( +)?")));
    }

    public List<String> getGf() {
        return gf;
    }

    public List<String> getGs() {
        return gs;
    }
}
