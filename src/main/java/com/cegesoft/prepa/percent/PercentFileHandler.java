package com.cegesoft.prepa.percent;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.quote.Quote;
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

public class PercentFileHandler {

    private final File file;
    private JsonObject json;

    public PercentFileHandler(String baseDir) throws IOException {
        File directory = new File(System.getProperty("user.dir") + File.separatorChar + baseDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        this.file = new File(directory, "percents.json");
        if (!file.exists()) {
            this.file.createNewFile();
            json = new JsonObject();
            this.saveFile();
        } else
            json = JsonParser.parseReader(new FileReader(this.file)).getAsJsonObject();
    }

    public HashMap<String, List<TenPercent>> load() {
        HashMap<String, List<TenPercent>> percents = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            List<TenPercent> percentList = new ArrayList<>();
            for (JsonElement element : array) {
                percentList.add(new TenPercent(element.getAsJsonObject()));
            }
            if (percentList.isEmpty())
                continue;
            percents.put(entry.getKey(), percentList);
        }
        return percents;
    }

    private void saveFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));
        writer.write(Main.gson.toJson(this.json));
        writer.flush();
        writer.close();
    }

    public void save(HashMap<String, List<TenPercent>> map) throws IOException {
        JsonObject save = this.json;
        try {
            json = new JsonObject();
            for (Map.Entry<String, List<TenPercent>> percents : map.entrySet()) {
                JsonArray array = new JsonArray();
                for (TenPercent percent : percents.getValue()) {
                    array.add(percent.toJson());
                }
                json.add(percents.getKey(), array);
            }
        } catch (Exception e) {
            this.json = save;
        }
        this.saveFile();
    }


}
