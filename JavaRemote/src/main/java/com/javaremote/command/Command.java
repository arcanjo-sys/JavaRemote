package com.javaremote.command;

/**
 * Functional abstraction representing a command that can be executed on the server.
 */
public interface Command {

    /**
     * Returns the unique identifier/name for this command (e.g. "ping").
     *
     * @return lowercase command name
     */
    String getName();

    /**
     * Executes the command with the provided arguments and returns a response string.
     *
     * @param args optional arguments passed after the command name
     * @return response message to be sent back to the client
     */
    String execute(String[] args);
}
