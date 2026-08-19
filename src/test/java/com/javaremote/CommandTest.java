package com.javaremote;

import com.javaremote.command.CommandRegistry;
import com.javaremote.command.PingCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    private CommandRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new CommandRegistry();
    }

    @Test
    public void testPingCommandReturnsPong() {
        PingCommand ping = new PingCommand();
        assertEquals("ping", ping.getName());
        assertEquals("pong", ping.execute(new String[0]));
    }

    @Test
    public void testCommandRegistryProcessesPing() {
        String response = registry.processInput("ping");
        assertEquals("pong", response);
    }

    @Test
    public void testCommandRegistryCaseInsensitive() {
        String response = registry.processInput("PING");
        assertEquals("pong", response);
    }

    @Test
    public void testUnknownCommandReturnsError() {
        String response = registry.processInput("unknown_cmd");
        assertTrue(response.startsWith("ERROR: Unknown command"));
    }

    @Test
    public void testEmptyInputReturnsError() {
        String response = registry.processInput("   ");
        assertTrue(response.startsWith("ERROR: Empty command"));
    }
}
