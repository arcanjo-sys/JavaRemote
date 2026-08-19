package com.javaremote;

import com.javaremote.client.RemoteClient;
import com.javaremote.config.Config;

import java.util.Scanner;

/**
 * Dedicated entry point to run the RemoteClient directly (convenient for IDE execution).
 */
public class MainClient {

    public static void main(String[] args) {
        Config config = new Config();

        System.out.println("==================================================");
        System.out.println("           JavaRemote Interactive Client          ");
        System.out.println("==================================================");
        System.out.println("Target: " + config.getHost() + ":" + config.getPort());
        System.out.println("Type 'ping' to test connection, or 'exit' to quit.\n");

        try (RemoteClient client = new RemoteClient(config.getHost(), config.getPort());
             Scanner scanner = new Scanner(System.in)) {

            client.connect();

            while (client.isConnected()) {
                System.out.print("client> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }

                if ("exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) {
                    client.sendMessage("exit");
                    break;
                }

                String response = client.sendCommand(input);
                if (response == null) {
                    System.out.println("[!] Server closed the connection.");
                    break;
                }
                System.out.println("server> " + response);
            }
        } catch (Exception e) {
            System.err.println("[X] Client error: " + e.getMessage());
        }
    }
}
