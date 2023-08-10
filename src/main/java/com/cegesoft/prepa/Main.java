package com.cegesoft.prepa;

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

    private static final HashMap<String, Server> servers = new HashMap<>();

    public static void main(String[] args) throws LoginException, IOException {
        gson = new Gson();
        taskManager = new TaskManager();
        taskManager.start();

        servers.put("883398835341234196", new Server(893245958345875537L, "883398835341234196", Server.Options.EASTER_EGG | Server.Options.PERCENT | Server.Options.QUOTES));
        servers.put("1009757451123441794", new Server(1009806815984369664L, "1009757451123441794", Server.Options.QUOTES | Server.Options.EASTER_EGG, MPStarInfoGathering.class));
        servers.put("1014815107756326993", new Server(1014856042523406397L, "1014815107756326993", Server.Options.QUOTES));

        for (Server server : servers.values()) {
            server.preLoad();
        }

        jda = JDABuilder.createDefault("ODkzMTg1Njc3NDEwMzI0NTYy.YVXx_A.lVhkEHm5C_UxjCm1CNwu_hq8kkc")
                .addEventListeners(new BaseListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        for (Server server : servers.values()) {
            server.postLoad();
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
    }

    public static Server getServer(String serverId) {
        return servers.get(serverId);
    }
}
