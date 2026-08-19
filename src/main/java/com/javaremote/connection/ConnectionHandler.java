package com.javaremote.connection;

import com.javaremote.command.CommandRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles the full communication lifecycle for an individual connected TCP client.
 */
public class ConnectionHandler implements Runnable {

    private final Socket clientSocket;
    private final CommandRegistry commandRegistry;
    private final String clientAddress;

    public ConnectionHandler(Socket clientSocket, CommandRegistry commandRegistry) {
        this.clientSocket = clientSocket;
        this.commandRegistry = commandRegistry;
        this.clientAddress = clientSocket.getRemoteSocketAddress() != null
                ? clientSocket.getRemoteSocketAddress().toString()
                : "unknown";
    }

    @Override
    public void run() {
        System.out.println("[+] Client connected: " + clientAddress);

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String input = line.trim();
                if (input.isEmpty()) {
                    continue;
                }

                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    writer.println("BYE: Connection closed.");
                    break;
                }

                String response = commandRegistry.processInput(input);
                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("[-] Communication error with client " + clientAddress + ": " + e.getMessage());
        } finally {
            closeSocket();
            System.out.println("[-] Client disconnected: " + clientAddress);
        }
    }

    private void closeSocket() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
