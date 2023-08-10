package com.cegesoft.prepa.percent;

import com.google.gson.JsonObject;

public class TenPercent {

    private final int value;
    private final int id;
    private final String student;
    private final int ds;

    public TenPercent(int value, int id, String student, int ds) {
        this.value = value;
        this.id = id;
        this.student = student;
        this.ds = ds;
    }

    public TenPercent(JsonObject object) {
        this.id = object.get("id").getAsInt();
        this.value = object.get("value").getAsInt();
        this.ds = object.get("ds").getAsInt();
        this.student = object.get("student").getAsString();
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public String getStudent() {
        return student;
    }

    public int getDs() {
        return ds;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("value", this.value);
        object.addProperty("ds", this.ds);
        object.addProperty("id", this.id);
        object.addProperty("student", this.student);
        return object;
    }
}
