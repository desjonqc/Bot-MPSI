package com.cegesoft.prepa;

import com.cegesoft.prepa.godfather.GFGroup;
import com.cegesoft.prepa.percent.TenPercent;
import com.cegesoft.prepa.quote.Quote;
import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.rank.InfoGatheringManager;
import com.cegesoft.prepa.server.Server;
import com.cegesoft.prepa.time.TimeTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BaseListener extends ListenerAdapter {

    private boolean tryEject(SlashCommandEvent event, Server server) {
        if (event.getChannel().getIdLong() != server.getChannelId()) {
            event.reply(":heart:").queue();
            return true;
        }
        return false;
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Server server = Main.getServer(event.getGuild().getId());
        if (server == null || !Server.Options.hasOption(server.getOptions(), Server.Options.QUOTES) || event.getChannel().getIdLong() != server.getChannelId() || event.getUser() == null || event.getUser().isBot()) {
            return;
        }

        event.retrieveMessage().queue(message -> {
            if (message.getAuthor().getIdLong() == Main.jda.getSelfUser().getIdLong() && message.getType().equals(MessageType.APPLICATION_COMMAND) && !message.getEmbeds().isEmpty()) {
                if (event.getReactionEmote().isEmote()) {
                    message.removeReaction(event.getReactionEmote().getEmote()).queue();
                    return;
                }
                try {
                    int quoteId = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" : ")[1]);
                    Optional<Quote> quoteOpt = server.getQuoteManager().getQuoteHandler().getQuote(quoteId);
                    if (!quoteOpt.isPresent()) {
                        return;
                    }
                    Quote quote = quoteOpt.get();
                    switch (event.getReactionEmote().getAsCodepoints()) {
                        case "U+2764U+fe0f": // ❤
                            quote.addLike();
                            break;
                        case "U+1f6ab":
                            quote.addDislike();
                            break;
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Server server = Main.getServer(event.getGuild().getId());
        if (server == null || !Server.Options.hasOption(server.getOptions(), Server.Options.QUOTES) || event.getChannel().getIdLong() != server.getChannelId() || event.getUser() == null || event.getUser().isBot()) {
            return;
        }

        event.retrieveMessage().queue(message -> {
            if (message.getAuthor().getIdLong() == Main.jda.getSelfUser().getIdLong() && message.getType().equals(MessageType.APPLICATION_COMMAND) && !message.getEmbeds().isEmpty()) {
                if (event.getReactionEmote().isEmote()) {
                    message.removeReaction(event.getReactionEmote().getEmote()).queue();
                    return;
                }
                try {
                    int quoteId = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split(" : ")[1]);
                    Optional<Quote> quoteOpt = server.getQuoteManager().getQuoteHandler().getQuote(quoteId);
                    if (!quoteOpt.isPresent()) {
                        return;
                    }
                    Quote quote = quoteOpt.get();
                    switch (event.getReactionEmote().getAsCodepoints()) {
                        case "U+2764U+fe0f": // ❤
                            quote.removeLike();
                            break;
                        case "U+1f6ab":
                            quote.removeDislike();
                            break;
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null)
            return;
        Server server = Main.getServer(event.getGuild().getId());
        if (server == null)
            return;
        if (event.getCommandIdLong() == Main.commandHandler.getQuoteCommand().getIdLong() && event.getGuild() != null) {
            if (tryEject(event, server) || !Server.Options.hasOption(server.getOptions(), Server.Options.QUOTES))
                return;
            List<Quote> quotes = server.getQuoteManager().getQuoteHandler().getJoinedQuotes();
            if (quotes.isEmpty()) {
                event.reply("Aucune citation enregistrée.").queue();
                return;
            }
            if (Math.random() > 0.75) {
                quotes = quotes.stream().filter(quote -> quote.getDate().getYear() == 2021).collect(Collectors.toList());
            }
            double maxRatio = quotes.stream().mapToDouble(Quote::getRatio).max().getAsDouble();
            double max = Math.random() * maxRatio / (2* Math.log(maxRatio + 1.1));
            quotes = quotes.stream().filter(quote -> quote.getRatio() >= max).collect(Collectors.toList());
            Collections.shuffle(quotes);
            event.replyEmbeds(quotes.get(0).toEmbed()).queue(interactionHook -> interactionHook.retrieveOriginal().queue(message -> {
                message.addReaction("U+2764U+fe0f").queue();
                message.addReaction("U+1f6ab").queue();
            }));
        }
        if (event.getCommandIdLong() == Main.commandHandler.getQuoteAdminCommand().getIdLong() && event.getSubcommandName() != null) {
            if (tryEject(event, server) || !Server.Options.hasOption(server.getOptions(), Server.Options.QUOTES))
                return;
            switch (event.getSubcommandName()) {
                case "list":
                    String optProf = null;
                    int optPage = 0;
                    if (!event.getOptions().isEmpty()) {
                        for (OptionMapping opt : event.getOptions()) {
                            if (opt.getName().equals("page")) {
                                optPage = (int) opt.getAsLong();
                            } else if (opt.getName().equals("prof")) {
                                optProf = opt.getAsString();
                            }
                        }
                    }
                    EmbedBuilder builder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Liste des citations (Total : " + server.getQuoteManager().getQuoteHandler().getJoinedQuotes().size() + ") :");
                    if (optProf == null) {
                        boolean first = true;
                        for (Map.Entry<String, List<Quote>> entry : server.getQuoteManager().getQuoteHandler().getQuotes().entrySet()) {
                            if (entry.getValue().isEmpty())
                                continue;
                            if (first) {
                                first = false;
                            } else
                                builder.addBlankField(false);
                            builder.addField("Citations de " + entry.getValue().get(0).getProf() + " : (Total : " + entry.getValue().size() + "; Page : " + (optPage + 1) + ")", "", false);
                            for (int i = optPage * 10; i < Math.min((optPage + 1) * 10, entry.getValue().size()); i++) {
                                Quote quote = entry.getValue().get(i);
                                builder.addField("ID " + quote.getId(), quote.getFormattedQuote(), false);
                            }
                        }
                    } else {
                        List<Quote> quotes = server.getQuoteManager().getQuoteHandler().getFrom(optProf);
                        if (quotes == null || quotes.isEmpty()) {
                            event.reply("Aucune citation de " + optProf).queue();
                            return;
                        }
                        builder.addField("Citations de " + quotes.get(0).getProf() + " : (Total : " + quotes.size() + "; Page : " + (optPage + 1) + ")", "", false);
                        for (int i = optPage * 10; i < Math.min((optPage + 1) * 10, quotes.size()); i++) {
                            Quote quote = quotes.get(i);
                            builder.addField("ID " + quote.getId(), quote.getFormattedQuote(), false);
                        }
                    }
                    event.replyEmbeds(builder.build()).queue();
                    break;
                case "add":
                    String quote = null;
                    String prof = null;
                    String note = null;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("quote")) {
                            quote = option.getAsString();
                        } else if (option.getName().equals("prof")) {
                            prof = option.getAsString();
                        } else if (option.getName().equals("note")) {
                            note = option.getAsString();
                        }
                    }
                    if (quote == null || prof == null) {
                        event.reply("Veuillez entrer un prof et une citation au minimum.").queue();
                        return;
                    }
                    Quote q = new Quote(new Random().nextInt(9000000), prof, quote.replaceAll("(»«\")", ""), note, server);
                    server.getQuoteManager().getQuoteHandler().addQuote(q);
                    event.reply("Citation ajoutée !").queue();
                    break;
                case "remove":
                    if (event.getOptions().isEmpty()) {
                        event.reply("Veuillez indiquer l'identifiant.").queue();
                        return;
                    }
                    int id = (int) event.getOptions().get(0).getAsLong();
                    server.getQuoteManager().getQuoteHandler().removeQuote(id);
                    event.reply("Citation retirée !").queue();
                    break;
                case "random":
                    List<Quote> quotes;
                    if (event.getOptions().isEmpty()) {
                        quotes = server.getQuoteManager().getQuoteHandler().getJoinedQuotes();
                        if (quotes.isEmpty()) {
                            event.reply("Aucune citation enregistrée.").queue();
                            return;
                        }
                    } else {
                        quotes = server.getQuoteManager().getQuoteHandler().getFrom(event.getOptions().get(0).getAsString());
                        if (quotes == null || quotes.isEmpty()) {
                            event.reply("Aucune citation enregistrée pour cet auteur.").queue();
                            return;
                        }
                    }
                    Collections.shuffle(quotes);
                    event.replyEmbeds(quotes.get(0).toEmbed()).queue();
                    break;
                default:
                    event.reply("Commande non reconnue").queue();
                    break;
            }
        }

        if (event.getCommandIdLong() == Main.commandHandler.getPercentCommand().getIdLong()) {
            if (!Server.Options.hasOption(server.getOptions(), Server.Options.PERCENT))
                return;
            if (event.getOptions().isEmpty()) {
                List<String> classement = server.getPercentManager().getPercentHandler().getClassement();
                if (classement.isEmpty()) {
                    event.reply("Aucune donnée :c").queue();
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder().setColor(Color.BLUE).setTitle("Classement des Pourcentages de M. Jung :");
                for (int i = 0; i < classement.size(); i++) {
                    builder.addField((i + 1) + "e Position :", classement.get(i), false);
                }
                builder.setFooter("Source : INSEE");
                event.replyEmbeds(builder.build()).queue();
            } else {
                String eleve = event.getOptions().get(0).getAsString();
                List<TenPercent> percents = server.getPercentManager().getPercentHandler().getFrom(eleve);
                if (percents == null || percents.isEmpty()) {
                    event.reply("Aucun pourcentage attribué à " + eleve).queue();
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN).setTitle("Pourcentages attribués à " + percents.get(0).getStudent() + " :");
                for (TenPercent percent : percents) {
                    builder.addField("DS " + percent.getDs() + " (" + percent.getId() + ") : ", "-" + percent.getValue() + "%", false);
                }
                event.replyEmbeds(builder.build()).queue();
            }
        }

        if (event.getCommandIdLong() == Main.commandHandler.getPercentAdminCommand().getIdLong() && event.getSubcommandName() != null) {
            if (tryEject(event, server) || !Server.Options.hasOption(server.getOptions(), Server.Options.PERCENT))
                return;
            switch (event.getSubcommandName()) {
                case "add":
                    int value = -1;
                    String student = null;
                    int ds = -1;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("valeur")) {
                            value = (int) option.getAsLong();
                        } else if (option.getName().equals("eleve")) {
                            student = option.getAsString();
                        } else if (option.getName().equals("ds")) {
                            ds = (int) option.getAsLong();
                        }
                    }
                    if (student == null || value == -1 || ds == -1) {
                        event.reply("Veuillez remplir tous les paramètres").queue();
                        return;
                    }
                    TenPercent p = new TenPercent(value, new Random().nextInt(9000000), student, ds);
                    server.getPercentManager().getPercentHandler().addPercents(p);
                    event.reply("Pourcentage ajouté !").queue();
                    break;
                case "remove":
                    if (event.getOptions().isEmpty()) {
                        event.reply("Veuillez indiquer l'identifiant.").queue();
                        return;
                    }
                    int id = (int) event.getOptions().get(0).getAsLong();
                    server.getPercentManager().getPercentHandler().removePercents(id);
                    event.reply("Pourcentage retiré !").queue();
                    break;
                default:
                    event.reply("Commande non reconnue").queue();
                    break;
            }
        }

        if (event.getCommandIdLong() == Main.commandHandler.getTimeCommand().getIdLong()) {
            if (!Server.Options.hasOption(server.getOptions(), Server.Options.EASTER_EGG))
                return;
            boolean owner = event.getUser().getIdLong() == 243450577953095680L;
            User a = owner ? null : event.getUser();
            if (owner) {
                if (!event.getOptions().isEmpty()) {
                    for (OptionMapping opt : event.getOptions()) {
                        a = opt.getAsUser();
                    }
                }
            }

            if (a == null) {
                event.reply("Veuillez remplir tous les paramètres").queue();
                return;
            }
            if (owner || !TimeTask.hasTask(a)) {
                if (TimeTask.toggleTask(a)) {
                    event.reply("Je donne l'heure à " + a.getAsMention() + " !").queue();
                } else {
                    event.reply("J'arrête de donner l'heure à " + a.getAsMention() + " !").queue();
                }
            } else {
                event.reply(":heart:").queue();
            }
        }

        if (event.getCommandIdLong() == Main.commandHandler.getGodfatherCommand().getIdLong() && event.getSubcommandName() != null) {
            if (!Server.Options.hasOption(server.getOptions(), Server.Options.GODFATHER))
                return;
            switch (event.getSubcommandName()) {
                case "ajouter_groupe":
                    String godfathers = null;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("parrains")) {
                            godfathers = option.getAsString();
                        }
                    }
                    if (godfathers == null) {
                        event.reply("Veuillez remplir tous les paramètres").setEphemeral(true).queue();
                        return;
                    }
                    if (!godfathers.contains(";")) {
                        event.reply("Les groupes doivent compter au moins deux parrains !").setEphemeral(true).queue();
                        return;
                    }
                    event.deferReply(true).queue();
                    String[] gfSplit = godfathers.split(";");
                    for (String gf : gfSplit) {
                        if (server.getGodFatherManager().getGodFatherHandler().getByGF(gf).isPresent()) {
                            event.getHook().editOriginal("Le parrain " + gf + " est déjà dans un groupe !").queue();
                            return;
                        }
                    }
                    GFGroup group = new GFGroup();
                    for (String parrain : gfSplit) {
                        group.addGodFather(parrain);
                    }
                    server.getGodFatherManager().getGodFatherHandler().addGroup(group);
                    event.getHook().editOriginal("Groupe ajouté ! Utilisez /info pour consulter l'état du groupe !").queue();
                    break;
                case "info":
                    String godfather = null;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("parrain")) {
                            godfather = option.getAsString();
                        }
                    }
                    if (godfather == null) {
                        event.reply("Veuillez remplir tous les paramètres").setEphemeral(true).queue();
                        return;
                    }
                    Optional<GFGroup> groupOptional = server.getGodFatherManager().getGodFatherHandler().getByGF(godfather);
                    if (!groupOptional.isPresent()) {
                        event.reply("Le parrain " + godfather + " n'est pas dans un groupe !").setEphemeral(true).queue();
                        return;
                    }
                    event.reply(groupOptional.get().format()).setEphemeral(true).queue();
                    break;
                case "supp_groupe":
                    String godfather2 = null;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("parrain")) {
                            godfather2 = option.getAsString();
                        }
                    }
                    if (godfather2 == null) {
                        event.reply("Veuillez remplir tous les paramètres").setEphemeral(true).queue();
                        return;
                    }
                    Optional<GFGroup> groupOptional2 = server.getGodFatherManager().getGodFatherHandler().getByGF(godfather2);
                    if (!groupOptional2.isPresent()) {
                        event.reply("Le parrain " + godfather2 + " n'est pas dans un groupe !").setEphemeral(true).queue();
                        return;
                    }
                    server.getGodFatherManager().getGodFatherHandler().removeGroup(groupOptional2.get());
                    event.reply("Groupe supprimé !").setEphemeral(true).queue();
                    break;
                case "ajouter_filleul":
                    String godSons = null;
                    String godFather = null;
                    for (OptionMapping option : event.getOptions()) {
                        if (option.getName().equals("filleuls")) {
                            godSons = option.getAsString();
                        } else if (option.getName().equals("parrain")) {
                            godFather = option.getAsString();
                        }
                    }
                    if (godSons == null || godFather == null) {
                        event.reply("Veuillez remplir tous les paramètres").setEphemeral(true).queue();
                        return;
                    }

                    event.deferReply(true).queue();
                    String[] gsSplit = godSons.split(";");
                    for (String gs : gsSplit) {
                        if (server.getGodFatherManager().getGodFatherHandler().getByGS(gs).isPresent()) {
                            event.getHook().editOriginal("Le filleul " + gs + " est déjà dans un groupe !").queue();
                            return;
                        }
                    }
                    Optional<GFGroup> groupOptional3 = server.getGodFatherManager().getGodFatherHandler().getByGF(godFather);
                    if (!groupOptional3.isPresent()) {
                        event.getHook().editOriginal("Le parrain " + godFather + " n'est pas dans un groupe !").queue();
                        return;
                    }
                    GFGroup group2 = groupOptional3.get();
                    for (String filleul : gsSplit) {
                        group2.addGodSon(filleul);
                    }
                    server.getGodFatherManager().getGodFatherHandler().saveGroups();
                    event.getHook().editOriginal("Filleuls ajoutés ! Utilisez /info pour consulter l'état du groupe !").queue();
                    break;
            }
        }
        if (event.getCommandIdLong() == Main.commandHandler.getGodfatherAdminCommand().getIdLong()) {
            if (!Server.Options.hasOption(server.getOptions(), Server.Options.GODFATHER))
                return;
            if (event.getUser().getIdLong() != server.getAdminId()) {
                event.reply("Vous n'avez pas la permission d'utiliser cette commande !").setEphemeral(true).queue();
                return;
            }
            event.deferReply(true).queue();
            StringBuilder builder = new StringBuilder();
            for (GFGroup group : server.getGodFatherManager().getGodFatherHandler().getGroups()) {
                builder.append(group.formatCSV()).append("\n");
            }
            File file = new File("_" + server.getId(), "godfather" + UUID.randomUUID() + ".csv");
            try {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(builder.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                event.getHook().editOriginal("Erreur lors de la création du fichier !").queue();
                return;
            }
            event.getHook().editOriginal("Fichier créé ! " + file.getName()).queue();
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Server server = Main.getServer(event.getGuild().getId());
        if (server == null || !Server.Options.hasOption(server.getOptions(), Server.Options.RANKS))
            return;
        server.getRankManager().startNewGathering(event.getUser());
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        InfoGathering info = InfoGatheringManager.getCurrent(event.getUser().getIdLong());
        if (info == null || event.getButton() == null)
            return;
        if (event.getChannel().equals(info.getChannel()) && info.getCurrentQuestion().selectAnswer(event.getButton().getId(), event.getButton().getLabel())) {
            info.nextQuestion(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        InfoGathering info = InfoGatheringManager.getCurrent(event.getAuthor().getIdLong());
        if (info == null)
            return;
        if (event.getChannel().equals(info.getChannel()) && info.getCurrentQuestion().selectAnswer("message", event.getMessage().getContentRaw())) {
            info.nextQuestion(null);
        }
    }
}
