package com.cegesoft.prepa.quote;

import com.cegesoft.prepa.server.Server;

import java.io.IOException;

public class QuoteManager {

    private final Server server;

    private QuoteHandler quoteHandler;
    private QuoteFileHandler quoteFileHandler;

    public QuoteManager(Server server) {
        this.server = server;
    }

    public void preLoad() throws IOException {
        quoteFileHandler = new QuoteFileHandler("_" + this.server.getId(), this.server);
    }

    public void postLoad() {
        quoteHandler = new QuoteHandler(this.server);
    }

    public QuoteHandler getQuoteHandler() {
        return quoteHandler;
    }

    public QuoteFileHandler getQuoteFileHandler() {
        return quoteFileHandler;
    }
}
