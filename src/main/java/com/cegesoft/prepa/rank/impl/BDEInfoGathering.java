package com.cegesoft.prepa.rank.impl;

import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.rank.question.ButtonQuestion;
import com.cegesoft.prepa.rank.question.ChatQuestion;
import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.awt.*;

public class BDEInfoGathering extends InfoGathering {

    private static final long MPSI_ROLE = 1139232591019507782L;
    private static final long MP_ROLE = 1139233007476162610L;
    private static final long MP5_2 = 1139233413837099122L;

    public BDEInfoGathering(Server server, User user) {
        super(server, user);
        this.addQuestion(new ChatQuestion("Pour commencer, quel est ton prénom ?", str -> {
            this.server.getGuild().retrieveMember(user).queue(member -> member.modifyNickname(str).queue());
        }))
                .addQuestion(new ButtonQuestion("En quelle classe es-tu ?")
                        .addAnswer("MPSI", Emoji.fromUnicode("U+1F42C"), ButtonStyle.PRIMARY, this.addRole(MPSI_ROLE).andThen(this.skipQuestions(3)))
                        .addAnswer("MP", Emoji.fromUnicode("U+1F988"), ButtonStyle.PRIMARY, str -> {}))
                .addQuestion(new ChatQuestion("Simple mesure de précaution, quel est le nom de ce théorème : Toute suite réelle bornée admet une extraction convergente ? (en un seul mot avec tiret)", str -> {
                    String preparedStr = str.replaceAll("[ \\-]", "").toLowerCase();
                    if (!preparedStr.contains("bolzanoweierstrass")) {
                        this.addRole(MPSI_ROLE).andThen(s -> {
                            this.server.sendWarnMessage("SUS : " + user.getName() + " a répondu '" + str + "' au lieu de Bolzano-Weierstrass...");
                            this.channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Mauvaise réponse").setDescription("Désolé ce n'est pas la bonne réponse. Si tu es réellement MP, demande en privé aux admins du serveur !").build()).queue();
                            this.skipQuestions(2).accept("");
                        }).accept("");
                        return;
                    }
                    this.addRole(MP_ROLE).accept("");
                }))
                .addQuestion(new ButtonQuestion("En quelle année es-tu ?")
                        .addAnswer("3/2", Emoji.fromUnicode("\uD83D\uDCA9"), ButtonStyle.SECONDARY, this.skipQuestions(1))
                        .addAnswer("5/2", Emoji.fromUnicode("\uD83D\uDE0E"), ButtonStyle.PRIMARY, str -> {}))
                .addQuestion(new ChatQuestion("De même, comment s'appelle cette équation : ΔV = 0 (V est un champ scalaire)", str -> {
                    if (str.replaceAll(" ", "").toLowerCase().contains("laplace")) {
                        this.addRole(MP5_2).accept("");
                        return;
                    }
                    this.server.sendWarnMessage("SUS : " + user.getName() + " a répondu '" + str + "' au lieu de Laplace...");
                    this.channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Mauvaise réponse").setDescription("Désolé ce n'est pas la bonne réponse. Si tu es réellement 5/2, demande en privé aux admins du serveur !").build()).queue();
                }));
    }

    @Override
    protected String getPresentationMessage() {
        return "Bienvenue sur le serveur de la prépa Malherbe MPSI/MP ! Réponds à ces quelques questions pour profiter du serveur !";
    }

    @Override
    protected String getConclusionMessage() {
        return "C'est tout bon, merci !";
    }
}
