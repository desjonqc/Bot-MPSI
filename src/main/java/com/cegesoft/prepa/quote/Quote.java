package com.cegesoft.prepa.quote;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.server.Server;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Quote {

    private final int id;
    private final String prof;
    private final String quote;
    private final String note;
    private final Server server;
    private final PostDate date;
    private int likes;
    private int dislikes;

    public Quote(int id, String prof, String quote, String note, Server server) {
        this.id = id;
        this.prof = prof;
        this.quote = this.filterQuote(quote);
        this.note = note;
        this.server = server;
        this.date = this.checkYear();
    }

    public Quote(JsonObject object, Server server) {
        this.id = object.get("id").getAsInt();
        this.prof = object.get("prof").getAsString();
        this.server = server;
        String note = object.get("note").getAsString();
        this.note = note.equals("null") ? null : note;
        this.quote = this.filterQuote(object.get("quote").getAsString());
        this.date = object.get("year") == null ? this.checkYear() : new PostDate(object);
        this.likes = object.get("likes") == null ? 0 : object.get("likes").getAsInt();
        this.dislikes = object.get("dislikes") == null ? 0 : object.get("dislikes").getAsInt();
    }

    private String filterQuote(String raw) {
        if ((raw.startsWith("\"") && raw.endsWith("\"")) || (raw.startsWith("“") && raw.endsWith("”"))) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }

    public PostDate checkYear() {
        if (note != null && note.contains(" ")) {
            String date = note.split(" ")[note.startsWith(" ") ? 2 : 1];
            if (date.contains(".") && date.split("\\.").length == 3) {
                try {
                    String[] split = date.split("\\.");
                    int month = Integer.parseInt(split[1]);
                    Calendar d = Calendar.getInstance();
                    d.set(Calendar.DATE, 1);
                    d.set(Calendar.MONTH, month - 1);
                    d.set(Calendar.YEAR, Integer.parseInt(split[2]));
                    return new PostDate(parseSchoolYear(d.getTime()), month);
                } catch (Exception ignored){}
            }
        }
        return new PostDate(parseSchoolYear(new Date()), new Date().getMonth() + 1);
    }

    private int parseSchoolYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) < 8 ? calendar.get(Calendar.YEAR) - 1 : calendar.get(Calendar.YEAR);
    }

    public int getId() {
        return id;
    }

    public String getProf() {
        return prof;
    }

    public String getQuote() {
        return quote;
    }

    public String getNote() {
        return note;
    }

    public String getFormattedQuote() {
        return "**« " + this.quote + " »**";
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("quote", this.quote);
        object.addProperty("prof", this.prof);
        object.addProperty("id", this.id);
        object.addProperty("note", this.note == null ? "null" : this.note);
        object.addProperty("year", this.date.year);
        object.addProperty("month", this.date.month);
        object.addProperty("likes", this.likes);
        object.addProperty("dislikes", this.dislikes);
        return object;
    }

    public MessageEmbed toEmbed() {
        EmbedBuilder builder = new EmbedBuilder().setColor(this.date.toColor()).setTitle(this.prof + " a dit...").appendDescription(this.getFormattedQuote()).setFooter("ID : " + this.id);
        if (this.note != null)
            builder.addField("Un peu de contexte : ", note, false);
        return builder.build();
    }

    public PostDate getDate() {
        return date;
    }

    public void addLike() {
        this.likes ++;
        this.server.getQuoteManager().getQuoteHandler().save();
    }

    public void addDislike() {
        this.dislikes ++;
        this.server.getQuoteManager().getQuoteHandler().save();
    }

    public void removeLike() {
        this.likes --;
        this.server.getQuoteManager().getQuoteHandler().save();
    }

    public void removeDislike() {
        this.dislikes --;
        this.server.getQuoteManager().getQuoteHandler().save();
    }

    public float getRatio() {
        return (this.likes + 1) / ( 1 + this.dislikes*1.0f);
    }

    public static class PostDate {
        private int year;
        private int month;


        public PostDate(int year, int month) {
            this.year = year;
            this.month = month;
        }

        private PostDate(JsonObject object) {
            this.year = object.get("year").getAsInt();
            this.month = object.get("month").getAsInt();
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        private int randomColor() {
            return new Random().nextInt(255);
        }

        public Color toColor() {
            return new Color(randomColor(), randomColor(), randomColor());
        }
    }
}
