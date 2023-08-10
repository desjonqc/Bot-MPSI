package com.cegesoft.prepa;

import com.cegesoft.prepa.task.TaskId;
import com.cegesoft.prepa.task.TaskManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Calendar;
import java.util.Optional;
import java.util.TimeZone;

public class AnniversaryTask implements Runnable {

    private final String name;
    private final long channelId;
    private String calendar = null;

    public AnniversaryTask(String name, long channelId) {
        this.name = name;
        this.channelId = channelId;
    }

    @Override
    public void run() {
        if (this.calendar == null){
            this.calendar = this.roundCalendar();
        } else if (!this.calendar.equals(this.roundCalendar())) {
            this.wish();
        }
    }

    private void wish() {
        this.calendar = this.roundCalendar();
        EmbedBuilder embedBuilder = new EmbedBuilder().setDescription(":partying_face: YOUPIII ! \n Aujourd'hui c'est l'anniversaire de <@" + name + "> !!")
                .setColor(Color.GREEN)
                .setTitle("**ANNIVERSAIRE**");

        Optional<TextChannel> channel = Main.jda.getGuilds().stream().filter(guild -> guild.getTextChannelById(channelId) != null).findFirst().map(guild -> guild.getTextChannelById(channelId));
        if (!channel.isPresent()){
            System.out.println("Channel " + channelId + " does not exist.");
            return;
        }
        channel.get().sendMessage(embedBuilder.build()).queue();
        channel.get().sendMessage("<@" + name + ">").queue();

    }

    private String roundCalendar() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1"));
        return calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DATE);
    }

    public static TaskId createTask(String user, long channelId) {
        return TaskManager.scheduleTaskTimer(new AnniversaryTask(user, channelId), "ANNIVERSAIRE-" + user, 5000, 60000);
    }
}
