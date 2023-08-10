package com.cegesoft.prepa.time;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.task.Task;
import com.cegesoft.prepa.task.TaskId;
import com.cegesoft.prepa.task.TaskManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class TimeTask implements Runnable {

    public static final HashMap<User, TimeTask> tasks = new HashMap<>();

    private static TimeFileHandler handler;

    public static void init() throws IOException {
        handler = new TimeFileHandler();
        handler.load();
    }

    private final User user;
    private final int minutes;
    private int hour = 0;
    private TaskId id;

    public TimeTask(User user, int minutes) {
        this.user = user;
        this.minutes = minutes;
    }

    JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("minutes", minutes);
        object.addProperty("user", user.getIdLong());
        return object;
    }

    private static void createTask(User user, int minutes, boolean instant) {
        TimeTask task = new TimeTask(user, minutes);
        if (!instant)
            task.hour = getHour();
        TaskId id = TaskManager.scheduleTaskTimer(task, "HOUR-" + user.getName(), 5000, 60000);
        task.setId(id);
        tasks.put(user, task);
    }

    public static void createTask(User user) {
        createTask(user, getMinutes(), true);
        try {
            handler.save(tasks.values());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadTask(JsonObject object) {
        if (object.has("user") && object.has("minutes")) {
            long user = object.get("user").getAsLong();
            int minutes = object.get("minutes").getAsInt();
            Main.jda.retrieveUserById(user).queue(u -> createTask(u, minutes, false));
        }
    }

    public static boolean hasTask(User user) {
        return tasks.containsKey(user);
    }

    public static boolean toggleTask(User user) {
        if (tasks.containsKey(user)) {
            TimeTask id = tasks.remove(user);
            id.send("Bon, j'arrête de t'emmerder...");
            Task task = TaskManager.getTask(id.getId());
            task.interrupt();
            try {
                handler.save(tasks.values());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            createTask(user);
            return true;
        }
    }

    @Override
    public void run() {
        if (this.canSend()) {
            this.send();
        }
    }

    private void send(String message) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }

    private void send() {
        this.send(getKeyword() + user.getName() + " ! Il est " + roundCalendar() + " !");
    }

    private boolean canSend() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        int current = calendar.get(Calendar.HOUR_OF_DAY);
        if (current >= 6 && current < 23 && current != hour && calendar.get(Calendar.MINUTE) >= minutes) {
            this.hour = current;
            return true;
        }
        return false;
    }

    private static int getMinutes() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        return calendar.get(Calendar.MINUTE);
    }

    private static int getHour() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private String roundCalendar() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        return format(calendar.get(Calendar.HOUR_OF_DAY) + 1) + "h" + format(calendar.get(Calendar.MINUTE));
    }

    private String getKeyword() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        return calendar.get(Calendar.HOUR_OF_DAY) > 18 ? "Bonsoir " : "Bonjour ";
    }

    private String format(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    public TaskId getId() {
        return id;
    }

    public TimeTask setId(TaskId id) {
        this.id = id;
        return this;
    }
}
