package com.cegesoft.prepa;

import com.cegesoft.prepa.rank.impl.BDE2024InfoGathering;
import com.cegesoft.prepa.rank.impl.BDE2025InfoGathering;
import com.cegesoft.prepa.rank.impl.BDEInfoGathering;
import com.cegesoft.prepa.rank.impl.MPStarInfoGathering;
import com.cegesoft.prepa.server.Server;
import com.cegesoft.prepa.task.TaskManager;
import com.cegesoft.prepa.time.TimeTask;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static JDA jda;
    public static CommandHandler commandHandler;
    public static TaskManager taskManager;
    public static Gson gson;

    public static final boolean DEBUG = false;

    private static final HashMap<String, Server> servers = new HashMap<>();

    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        gson = new Gson();
        taskManager = new TaskManager();
        taskManager.start();

        if (DEBUG) {
            servers.put("893187646610874388", new Server(893187646610874393L, "893187646610874388", Server.Options.GODFATHER, 243450577953095680L, BDE2024InfoGathering.class));
        } else {
            servers.put("883398835341234196", new Server(893245958345875537L, "883398835341234196", Server.Options.EASTER_EGG | Server.Options.PERCENT | Server.Options.QUOTES, 243450577953095680L));
            servers.put("1009757451123441794", new Server(1009806815984369664L, "1009757451123441794", Server.Options.QUOTES | Server.Options.EASTER_EGG, 243450577953095680L, MPStarInfoGathering.class));
            servers.put("1014815107756326993", new Server(1014856042523406397L, "1014815107756326993", Server.Options.QUOTES, 243450577953095680L));
            servers.put("1139231004867964928", new Server(1139504087994486834L, "1139231004867964928", Server.Options.GODFATHER, 243450577953095680L, BDE2024InfoGathering.class));
            servers.put("1257317696043749417", new Server(1257320763980251246L, "1257317696043749417", Server.Options.GODFATHER, 243450577953095680L, BDE2025InfoGathering.class));
        }
        for (Server server : servers.values()) {
            server.preLoad();
        }

        String TOKEN = DEBUG ? "" : "";

        jda = JDABuilder.createDefault(TOKEN)
                .addEventListeners(new BaseListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build()
                .awaitReady();


        for (Server server : servers.values()) {
            try {
                server.postLoad();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while loading server " + server.getId());
            }
        }

        //AnniversaryTask.createTask("566966039045210127", 883405861001781348L);

        TimeTask.init();
//        TimeTask.createTask("Noah", 543696250852081665L);
//        TimeTask.createTask("Vincent", 355740837025415170L);
//        TimeTask.createTask("Jean", 688811196744597534L);
//
//        Scanner scanner = new Scanner(System.in);
//        String line;
//        do {
//            line = scanner.nextLine();
//            if (line.equals(""))
//                continue;
//            Quote quote = new Quote(new Random().nextInt(9000000), "M. Roux", line.split("\"")[1], line.split("\"")[2].replace(")", "").replace("(", ""));
//            quoteHandler.addQuote(quote);
//        } while (!line.equals("stop"));


        commandHandler = new CommandHandler();
        commandHandler.registerCommand();
        System.out.println("Bot is ready !");
    }

    public static Server getServer(String serverId) {
        return servers.get(serverId);
    }
}
