package com.cegesoft.prepa.rank;

import com.cegesoft.prepa.server.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class InfoGathering {

    protected final User user;
    protected final Server server;
    protected final ArrayList<IQuestion> questions = new ArrayList<>();
    protected PrivateChannel channel;
    private int questionIndex = -1;

    public InfoGathering(Server server, User user) {
        this.user = user;
        this.server = server;
    }

    protected abstract String getPresentationMessage();

    protected abstract String getConclusionMessage();

    public PrivateChannel getChannel() {
        return channel;
    }

    public void start() {
        this.user.openPrivateChannel().queue(channel -> {
            this.channel = channel;
            Message message = new MessageBuilder().setEmbed(
                    new EmbedBuilder().setColor(Color.GREEN).setTitle("QUESTIONNAIRE").setDescription(getPresentationMessage()).build()
            ).build();
            this.channel.sendMessage(message).queue();
            this.nextQuestion(null);
        });
    }

    public void nextQuestion(Interaction interaction) {
        this.questionIndex++;
        if (this.questionIndex != 0) {
            Message message = new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.GREEN).setTitle("Réponse " + (questionIndex) + " / " + questions.size() + " :").setDescription(questions.get(questionIndex - 1).getAnswer()).build()).build();
            if (interaction == null) {
                this.channel.sendMessage(message).queue();
            } else {
                interaction.reply(message).queue();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.questionIndex >= this.questions.size()) {
            this.channel.sendMessage(new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.GREEN).setTitle("TERMINÉ !").setDescription(getConclusionMessage()).build()).build()).queue();
            this.server.getRankManager().stopInfoGathering(this);
            return;
        }
        Message message = this.questions.get(questionIndex).build(new EmbedBuilder().setColor(Color.ORANGE).setTitle("Question " + (questionIndex + 1) + " / " + questions.size() + " :")).build();
        this.channel.sendMessage(message).queue();
    }

    public InfoGathering addQuestion(IQuestion question) {
        this.questions.add(question);
        return this;
    }

    protected Consumer<String> addRole(long role) {
        return str -> {
            this.server.getGuild().addRoleToMember(user.getId(), server.getGuild().getRoleById(role)).queue();
        };
    }

    public IQuestion getCurrentQuestion() {
        return this.questions.get(this.questionIndex);
    }

}
