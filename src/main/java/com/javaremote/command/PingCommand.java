package com.javaremote.command;

/**
 * Basic health check command. Responds with "pong" when "ping" is received.
 */
public class PingCommand implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String execute(String[] args) {
        return "pong";
    }
}
