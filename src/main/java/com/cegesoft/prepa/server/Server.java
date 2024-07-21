package com.cegesoft.prepa.server;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.godfather.GodFatherManager;
import com.cegesoft.prepa.percent.PercentManager;
import com.cegesoft.prepa.quote.QuoteManager;
import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.rank.InfoGatheringManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;

public class Server {

    private final long channelId;
    private final String serverId;
    private final int options;
    private final long adminId;
    private User admin;

    private QuoteManager quoteManager;
    private PercentManager percentManager;
    private InfoGatheringManager infoGatheringManager;
    private GodFatherManager godFatherManager;

    public Server(long channelId, String serverId, int options, long adminId) {
        this.channelId = channelId;
        this.serverId = serverId;
        this.options = options;
        this.adminId = adminId;
        if (Options.hasOption(this.options, Options.QUOTES)) {
            this.quoteManager = new QuoteManager(this);
        }
        if (Options.hasOption(this.options, Options.PERCENT)) {
            this.percentManager = new PercentManager(this);
        }
        if (Options.hasOption(this.options, Options.GODFATHER)) {
            this.godFatherManager = new GodFatherManager(this);
        }
    }

    public Server(long channelId, String serverId, int options, long adminId, Class<? extends InfoGathering> rankClass) {
        this(channelId, serverId, options | Options.RANKS, adminId);
        this.infoGatheringManager = new InfoGatheringManager(this, rankClass);
    }

    public QuoteManager getQuoteManager() {
        return this.quoteManager;
    }

    public PercentManager getPercentManager() {
        return percentManager;
    }

    public InfoGatheringManager getRankManager() {
        return infoGatheringManager;
    }

    public GodFatherManager getGodFatherManager() {
        return godFatherManager;
    }

    public String getId() {
        return this.serverId;
    }

    public void preLoad() throws IOException {
        if (Options.hasOption(this.options, Options.QUOTES)) {
            this.quoteManager.preLoad();
        }
        if (Options.hasOption(this.options, Options.PERCENT)) {
            this.percentManager.preLoad();
        }
        if (Options.hasOption(this.options, Options.RANKS)) {
            this.infoGatheringManager.preLoad();
        }
        if (Options.hasOption(this.options, Options.GODFATHER)) {
            this.godFatherManager.preLoad();
        }
    }

    public void postLoad() {
        if (Options.hasOption(this.options, Options.QUOTES)) {
            this.quoteManager.postLoad();
        }
        if (Options.hasOption(this.options, Options.PERCENT)) {
            this.percentManager.postLoad();
        }
        if (Options.hasOption(this.options, Options.RANKS)) {
            this.infoGatheringManager.postLoad();
        }
        if (Options.hasOption(this.options, Options.GODFATHER)) {
            this.godFatherManager.postLoad();
        }
        Main.jda.retrieveUserById(adminId).queue(user -> this.admin = user);
    }

    public long getChannelId() {
        return channelId;
    }

    public int getOptions() {
        return options;
    }

    public Guild getGuild() {
        return Main.jda.getGuildById(serverId);
    }

    public void sendWarnMessage(String message) {
        this.admin.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }

    public long getAdminId() {
        return adminId;
    }

    public static class Options {
        public static final int EASTER_EGG = 0b00001;
        public static final int PERCENT = 0b00010;
        public static final int QUOTES = 0b00100;
        public static final int RANKS = 0b01000;
        public static final int GODFATHER = 0b10000;

        public static boolean hasOption(int target, int option) {
            return ~(target | ~option) == 0;
        }
    }

}
