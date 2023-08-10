package com.cegesoft.prepa.quote;

import com.cegesoft.prepa.Main;
import com.cegesoft.prepa.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class QuoteHandler {

    private final Server server;
    private final HashMap<String, List<Quote>> quotes = new HashMap<>();

    public QuoteHandler(Server server) {
        this.server = server;
        this.quotes.putAll(server.getQuoteManager().getQuoteFileHandler().load());
    }

    public List<Quote> getJoinedQuotes() {
        List<Quote> quotes = new ArrayList<>();
        for (List<Quote> list : this.quotes.values()) {
            quotes.addAll(list);
        }
        return quotes;
    }

    public HashMap<String, List<Quote>> getQuotes() {
        return quotes;
    }

    public List<Quote> getFrom(String prof) {
        return quotes.get(prof.toLowerCase());
    }

    public Optional<Quote> getQuote(int id) {
        return quotes.values().stream().filter(list -> list.stream().anyMatch(quote -> quote.getId() == id)).flatMap(list -> list.stream().filter(quote -> quote.getId() == id)).findFirst();
    }

    public void addQuote(Quote quote) {
        if (!quotes.containsKey(quote.getProf().toLowerCase())) {
            quotes.put(quote.getProf().toLowerCase(), new ArrayList<>());
        }
        quotes.get(quote.getProf().toLowerCase()).add(quote);
        this.save();
    }

    public void save() {
        try {
            this.server.getQuoteManager().getQuoteFileHandler().save(this.quotes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeQuote(int id) {
        this.quotes.values().forEach(list -> new ArrayList<>(list).stream().filter(quote -> quote.getId() == id).forEach(list::remove));
        this.save();
    }

    public static String formatListQuote(List<Quote> quotes) {
        StringBuilder str = new StringBuilder();
        for (Quote quote : quotes) {
            str.append(quote.getId()).append(" : ").append(quote.getFormattedQuote()).append("\n");
        }
        return str.toString();
    }
}
