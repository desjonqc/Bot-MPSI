package com.cegesoft.prepa.godfather;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.percent.TenPercent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GodFatherFileHandler {

    private final File file;
    private JsonArray json;

    public GodFatherFileHandler(String baseDir) throws IOException {
        File directory = new File(System.getProperty("user.dir") + File.separatorChar + baseDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        this.file = new File(directory, "godfather.json");
        if (!file.exists()) {
            this.file.createNewFile();
            json = new JsonArray();
            this.saveFile();
        } else
            json = JsonParser.parseReader(new FileReader(this.file)).getAsJsonArray();
    }

    private void saveFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));
        writer.write(Main.gson.toJson(this.json));
        writer.flush();
        writer.close();
    }

    public void save(List<GFGroup> groups) throws IOException {
        JsonArray save = this.json;
        try {
            this.json = new JsonArray();
            for (GFGroup group : groups) {
                this.json.add(group.formatCSV());
            }
        } catch (Exception e) {
            this.json = save;
        }
        this.saveFile();
    }


    public ArrayList<GFGroup> load() {
        ArrayList<GFGroup> groups = new ArrayList<>();
        for (JsonElement element : json) {
            groups.add(new GFGroup(element.getAsString()));
        }
        return groups;
    }
}
