package com.cegesoft.prepa.rank.impl;

import com.cegesoft.prepa.rank.InfoGathering;
import com.cegesoft.prepa.rank.question.ButtonQuestion;
import com.cegesoft.prepa.rank.question.ChatQuestion;
import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class MPStarInfoGathering extends InfoGathering {

    public MPStarInfoGathering(Server server, User user) {
        super(server, user);
        this.addQuestion(new ChatQuestion("Pour commencer, quel est ton prénom ?", str -> {
                    this.server.getGuild().retrieveMember(user).queue(member -> member.modifyNickname(str).queue());
                }))
                .addQuestion(new ButtonQuestion("Quelle option as-tu choisi ?")
                        .addAnswer("Option Info", Emoji.fromUnicode("\uD83D\uDCBB"), ButtonStyle.PRIMARY, this.addRole(1009760982937108502L))
                        .addAnswer("Option SI", Emoji.fromUnicode("\uD83D\uDEE0"), ButtonStyle.PRIMARY, this.addRole(1009760733346660393L))
                )
                .addQuestion(new ButtonQuestion("Quelle LV1 fais-tu ?")
                        .addAnswer("Anglais", Emoji.fromUnicode("\uD83C\uDDEC\uD83C\uDDE7"), ButtonStyle.PRIMARY, this.addRole(1010095733237616680L))
                        .addAnswer("Allemand", Emoji.fromUnicode("\uD83C\uDDE9\uD83C\uDDEA"), ButtonStyle.PRIMARY, this.addRole(1010095375719338045L))
                )
                .addQuestion(new ButtonQuestion("Quelle LV2 fais-tu ?")
                        .addAnswer("Anglais", Emoji.fromUnicode("\uD83C\uDDEC\uD83C\uDDE7"), ButtonStyle.PRIMARY, this.addRole(1010095908639211580L))
                        .addAnswer("Allemand", Emoji.fromUnicode("\uD83C\uDDE9\uD83C\uDDEA"), ButtonStyle.PRIMARY, this.addRole(1010095809750114304L))
                        .addAnswer("Espagnol", Emoji.fromUnicode("\uD83C\uDDEA\uD83C\uDDF8"), ButtonStyle.PRIMARY, this.addRole(1010095956945010729L))
                        .addAnswer("Aucune", null, ButtonStyle.SECONDARY, str -> {})
                )
                .addQuestion(new ButtonQuestion("En quelle année es-tu ?")
                        .addAnswer("3/2", null, ButtonStyle.PRIMARY, this.addRole(1009760616292028456L))
                        .addAnswer("5/2", null, ButtonStyle.PRIMARY, this.addRole(1009760682520104990L))
                        .addAnswer("7/2", Emoji.fromUnicode("\uD83D\uDE36"), ButtonStyle.DANGER, true, str -> {})
                );
    }

    @Override
    protected String getPresentationMessage() {
        return "Salut **" + user.getName() + "** !\n Je te propose un petit questionnaire pour configurer ton compte sur le serveur Discord de la MP* !";
    }

    @Override
    protected String getConclusionMessage() {
        return "C'est tout bon, merci !";
    }
}
