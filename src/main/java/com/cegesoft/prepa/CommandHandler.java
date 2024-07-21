package com.cegesoft.prepa;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandHandler {

    private Command quoteCommand;
    private Command quoteAdminCommand;

    private Command percentCommand;
    private Command percentAdminCommand;

    private Command godfatherCommand;
    private Command godfatherAdminCommand;

    private Command timeCommand;

    public void registerCommand() {
        quoteCommand = Main.jda.upsertCommand("quote", "Les meilleures citations des profs !").complete();
        quoteAdminCommand = Main.jda.upsertCommand("aquote", "Gère les citations.").addSubcommands(new SubcommandData("add", "Ajoute une citation").addOption(OptionType.STRING, "quote", "La citation").addOption(OptionType.STRING, "prof", "L'auteur").addOption(OptionType.STRING, "note", "Contexte de la citation"))
                .addSubcommands(new SubcommandData("list", "Liste les citations existantes").addOption(OptionType.INTEGER, "page", "Page à consulter (10 par page)").addOption(OptionType.STRING, "prof", "L'auteur"))
                .addSubcommands(new SubcommandData("remove", "Supprime une citation").addOption(OptionType.INTEGER, "id", "Identifiant de la citation"))
                .addSubcommands(new SubcommandData("random", "Lance une citation au hasard").addOption(OptionType.STRING, "prof", "Auteur")).complete();

        percentCommand = Main.jda.upsertCommand("pourcent", "Le classement des pourcentages de M. Jung").addOption(OptionType.STRING, "eleve", "Voir les pourcentages d'un élève").complete();
        percentAdminCommand = Main.jda.upsertCommand("apourcent", "Gère les pourcentages de M. Jung.").addSubcommands(new SubcommandData("add", "Ajoute un pourcentage").addOption(OptionType.INTEGER, "valeur", "Le pourcentage").addOption(OptionType.STRING, "eleve", "L'élève").addOption(OptionType.INTEGER, "ds", "Le DS concerné"))
                .addSubcommands(new SubcommandData("remove", "Supprime un pourcentage de M. Jung.").addOption(OptionType.INTEGER, "id", "Identifiant de l'entrée")).complete();
        timeCommand = Main.jda.upsertCommand("donnelheure", "Donne l'heure à quelqu'un").addOption(OptionType.USER, "à", "A qui ?").complete();
        godfatherCommand = Main.jda.upsertCommand("parrain", "Gère les parrains")
                .addSubcommands(new SubcommandData("ajouter_groupe", "Ajoute un groupe de parrains").addOption(OptionType.STRING, "parrains", "Le prénom puis le nom des parrains séparés par ; (exemple : 'Jean Dupont;Clara Smith;...') (MAX 4)"))
                .addSubcommands(new SubcommandData("supp_groupe", "Supprime un groupe de parrains").addOption(OptionType.STRING, "parrain", "Le prénom puis le nom d'un parrain présent dans le groupe (exemple : 'Jean Dupont')"))
                .addSubcommands(new SubcommandData("info", "Affiche les informations d'un groupe").addOption(OptionType.STRING, "parrain", "Le prénom puis le nom d'un parrain présent dans le groupe (exemple : 'Jean Dupont')"))
                .addSubcommands(new SubcommandData("ajouter_filleul", "Ajoute des filleuls à un groupe de parrains").addOption(OptionType.STRING, "parrain", "Le prénom puis le nom d'un parrain présent dans le groupe (exemple : 'Jean Dupont')").addOption(OptionType.STRING, "filleuls", "Le prénom puis le nom des filleuls séparés par ; (exemple : 'Jean Dupont;Clara Smith;...') (MAX 4)")).complete();
        godfatherAdminCommand = Main.jda.upsertCommand("parrain_csv", "Convertit les parrains en CSV").complete();
    }

    public Command getQuoteCommand() {
        return quoteCommand;
    }

    public Command getQuoteAdminCommand() {
        return quoteAdminCommand;
    }

    public Command getPercentAdminCommand() {
        return percentAdminCommand;
    }

    public Command getPercentCommand() {
        return percentCommand;
    }

    public Command getTimeCommand() {
        return timeCommand;
    }

    public Command getGodfatherCommand() {
        return godfatherCommand;
    }

    public Command getGodfatherAdminCommand() {
        return godfatherAdminCommand;
    }
}
