package com.cegesoft.prepa.rank.question;

import com.cegesoft.prepa.rank.IQuestion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.util.function.Consumer;

public class ChatQuestion implements IQuestion {

    private final String question;
    private final Consumer<String> answer;

    private String answerStr;

    public ChatQuestion(String question, Consumer<String> answer) {
        this.question = question;
        this.answer = answer;
    }


    @Override
    public IQuestion addAnswer(String s, Emoji emoji, ButtonStyle style, Consumer<String> result) {
        return this;
    }

    @Override
    public MessageBuilder build(EmbedBuilder builder) {
        return new MessageBuilder().setEmbed(builder.addField(question, "*Répondre dans le tchat*", false).build());
    }

    @Override
    public boolean selectAnswer(String id, String o) {
        if (id.equals("message")) {
            this.answerStr = o;
            this.answer.accept((String) o);
            return true;
        }
        return false;
    }

    @Override
    public String getAnswer() {
        return answerStr;
    }
}
