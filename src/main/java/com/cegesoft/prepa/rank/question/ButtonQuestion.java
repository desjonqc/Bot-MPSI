package com.cegesoft.prepa.rank.question;

import com.cegesoft.prepa.rank.IQuestion;
import com.cegesoft.prepa.rank.InfoGathering;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ButtonQuestion implements IQuestion {

    private final List<Component> actionRows = new ArrayList<>();
    private final String question;
    private final HashMap<String, Consumer<String>> answerList = new HashMap<>();

    private String answerStr;

    public ButtonQuestion(String question) {
        this.question = question;
    }

    public ButtonQuestion addAnswer(String s, Emoji emoji, ButtonStyle style, boolean fake, Consumer<String> result) {
        UUID uuid = UUID.randomUUID();
        actionRows.add(Button.of(style, uuid.toString(), s, emoji));
        if (!fake)
            answerList.put(uuid.toString(), result);
        return this;
    }
    @Override
    public ButtonQuestion addAnswer(String s, Emoji emoji, ButtonStyle style, Consumer<String> result) {
        return this.addAnswer(s, emoji, style, false, result);
    }

    @Override
    public MessageBuilder build(EmbedBuilder builder) {
        return new MessageBuilder().setActionRows(ActionRow.of(actionRows)).setEmbed(builder.addField(question, "", true).build());
    }

    @Override
    public boolean selectAnswer(String id, String o) {
        Consumer<String> c = answerList.get(id);
        if (c == null)
            return false;
        this.answerStr = o;
        c.accept(o);
        return true;
    }

    @Override
    public String getAnswer() {
        return answerStr;
    }
}
