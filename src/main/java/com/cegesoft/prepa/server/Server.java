package com.cegesoft.prepa.server;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.percent.PercentManager;
import com.cegesoft.prepa.quote.QuoteManager;
import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.rank.RankManager;
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
    private RankManager<?> rankManager;


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
    }

    public Server(long channelId, String serverId, int options, long adminId, Class<? extends InfoGathering> rankClass) {
        this(channelId, serverId, options | Options.RANKS, adminId);
        this.rankManager = new RankManager<>(this, rankClass);
    }

    public QuoteManager getQuoteManager() {
        return this.quoteManager;
    }

    public PercentManager getPercentManager() {
        return percentManager;
    }

    public RankManager<?> getRankManager() {
        return rankManager;
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
    }

    public void postLoad() {
        if (Options.hasOption(this.options, Options.QUOTES)) {
            this.quoteManager.postLoad();
        }
        if (Options.hasOption(this.options, Options.PERCENT)) {
            this.percentManager.postLoad();
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

    public static class Options {
        public static final int EASTER_EGG = 0b0001;
        public static final int PERCENT = 0b0010;
        public static final int QUOTES = 0b0100;
        public static final int RANKS = 0b1000;

        public static boolean hasOption(int target, int option) {
            return ~(target | ~option) == 0;
        }
    }

}
