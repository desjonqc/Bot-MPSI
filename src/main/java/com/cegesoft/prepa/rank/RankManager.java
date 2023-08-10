package com.cegesoft.prepa.rank;

import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RankManager<T extends InfoGathering> {

    private final Server server;
    private final Class<T> tClass;
    private static final HashMap<Long, InfoGathering> infoGatherings = new HashMap<>();

    public RankManager(Server server, Class<T> tClass) {
        this.server = server;
        this.tClass = tClass;
    }

    public void startNewGathering(User user) {
        try {
            T t = tClass.getConstructor(Server.class, User.class).newInstance(this.server, user);
            infoGatherings.put(user.getIdLong(), t);
            t.start();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void stopInfoGathering(InfoGathering infoGathering) {
        infoGatherings.remove(infoGathering.user.getIdLong());
    }

    public static InfoGathering getCurrent(long user) {
        return infoGatherings.get(user);
    }
}
