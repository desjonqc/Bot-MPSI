package com.cegesoft.prepa.time;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.quote.Quote;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TimeFileHandler {

    private final File file;
    private JsonArray json;

    public TimeFileHandler() throws IOException {
        this.file = new File(System.getProperty("user.dir"), "time.json");
        if (!file.exists()) {
            this.file.createNewFile();
            json = new JsonArray();
            this.saveFile();
        } else
            json = JsonParser.parseReader(new FileReader(this.file)).getAsJsonArray();
    }

    public void load() {
        for (JsonElement task : json) {
            TimeTask.loadTask(task.getAsJsonObject());
        }
    }

    private void saveFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));
        writer.write(Main.gson.toJson(this.json));
        writer.flush();
        writer.close();
    }

    public void save(Collection<TimeTask> map) throws IOException {
        JsonArray save = this.json.deepCopy();
        try {
            json = new JsonArray();
            for (TimeTask task : map) {
                json.add(task.toJson());
            }
        } catch (Exception e) {
            this.json = save;
        }
        this.saveFile();
    }
}
