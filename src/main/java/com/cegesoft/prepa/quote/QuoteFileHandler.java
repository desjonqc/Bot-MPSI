package com.cegesoft.prepa.quote;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.server.Server;
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

public class QuoteFileHandler {

    private final Server server;
    private final File file;
    private JsonObject json;

    public QuoteFileHandler(String baseDir, Server server) throws IOException {
        this.server = server;
        File directory = new File(System.getProperty("user.dir") + File.separatorChar + baseDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        this.file = new File(directory, "quotes.json");
        if (!file.exists()) {
            this.file.createNewFile();
            json = new JsonObject();
            saveFile();
        } else
            json = JsonParser.parseReader(new FileReader(this.file)).getAsJsonObject();
    }

    public HashMap<String, List<Quote>> load() {
        HashMap<String, List<Quote>> quotes = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            List<Quote> quoteList = new ArrayList<>();
            for (JsonElement element : array) {
                quoteList.add(new Quote(element.getAsJsonObject(), server));
            }
            if (quoteList.isEmpty())
                continue;
            quotes.put(entry.getKey(), quoteList);
        }
        return quotes;
    }

    private void saveFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));
        writer.write(Main.gson.toJson(this.json));
        writer.flush();
        writer.close();
    }

    public void save(HashMap<String, List<Quote>> map) throws IOException {
        JsonObject save = this.json;
        try {
            json = new JsonObject();
            for (Map.Entry<String, List<Quote>> quotes : map.entrySet()) {
                JsonArray array = new JsonArray();
                for (Quote quote : quotes.getValue()) {
                    array.add(quote.toJson());
                }
                json.add(quotes.getKey(), array);
            }
        } catch (Exception e) {
            this.json = save;
        }
        this.saveFile();
    }


}
