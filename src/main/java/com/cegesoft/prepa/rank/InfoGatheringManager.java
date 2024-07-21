package com.cegesoft.prepa.rank;

import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class InfoGatheringManager {

    private static final HashMap<Long, InfoGathering> infoGatherings = new HashMap<>();
    private final Server server;
    private final Class<? extends InfoGathering> defaultGatheringClass;

    public InfoGatheringManager(Server server, Class<? extends InfoGathering> defaultGatheringClass) {
        this.server = server;
        this.defaultGatheringClass = defaultGatheringClass;
    }

    public static InfoGathering getCurrent(long user) {
        return infoGatherings.get(user);
    }

    public void startNewGathering(User user, InfoGathering infoGathering) {
        infoGatherings.remove(user.getIdLong());
        infoGatherings.put(user.getIdLong(), infoGathering);
        infoGathering.start();
    }

    public void startNewGathering(User user) {
        try {
            InfoGathering info = defaultGatheringClass.getConstructor(Server.class, User.class).newInstance(this.server, user);
            this.startNewGathering(user, info);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void stopInfoGathering(InfoGathering infoGathering) {
        infoGatherings.remove(infoGathering.user.getIdLong());
    }

    public void preLoad() {
    }

    public void postLoad() {
        Guild guild = server.getGuild();
        for (Member member : guild.loadMembers().get()) {
            if (member.getRoles().isEmpty()) {
                this.startNewGathering(member.getUser());
            }
        }
    }
}
