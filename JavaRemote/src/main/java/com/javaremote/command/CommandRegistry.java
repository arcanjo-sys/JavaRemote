package com.javaremote.command;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry and dispatcher for executing registered commands.
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public CommandRegistry() {
        // Register default built-in commands
        register(new PingCommand());
    }

    public void register(Command command) {
        if (command != null && command.getName() != null) {
            commands.put(command.getName().toLowerCase().trim(), command);
        }
    }

    public String processInput(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return "ERROR: Empty command received.";
        }

        String[] tokens = rawInput.trim().split("\\s+");
        String commandName = tokens[0].toLowerCase();
        String[] args = (tokens.length > 1) ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[0];

        Command command = commands.get(commandName);
        if (command != null) {
            try {
                return command.execute(args);
            } catch (Exception e) {
                return "ERROR: Command execution failed: " + e.getMessage();
            }
        }

        return "ERROR: Unknown command '" + commandName + "'. Available: " + String.join(", ", commands.keySet());
    }

    public boolean hasCommand(String commandName) {
        return commandName != null && commands.containsKey(commandName.toLowerCase().trim());
    }
}
