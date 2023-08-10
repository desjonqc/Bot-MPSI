package com.cegesoft.prepa.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.util.function.Consumer;

public interface IQuestion {

    IQuestion addAnswer(String s, Emoji emoji, ButtonStyle style, Consumer<String> result);

    MessageBuilder build(EmbedBuilder builder);

    boolean selectAnswer(String id, String o);

    String getAnswer();

}
