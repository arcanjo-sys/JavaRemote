package com.javaremote;

import com.javaremote.client.RemoteClient;
import com.javaremote.config.Config;
import com.javaremote.server.RemoteServer;

import java.util.Scanner;

/**
 * Main application entry point.
 * Allows launching either the RemoteServer or the RemoteClient via command line arguments.
 *
 * Usage:
 *   java -jar javaremote.jar server [port]
 *   java -jar javaremote.jar client [host] [port]
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndPrompt();
            return;
        }

        String mode = args[0].toLowerCase();
        switch (mode) {
            case "server" -> {
                int port = parsePort(args, 1, Config.DEFAULT_PORT);
                Config config = new Config(Config.DEFAULT_HOST, port);
                RemoteServer server = new RemoteServer(config);

                // Add shutdown hook for clean termination (Ctrl+C)
                Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

                server.start();
            }
            case "client" -> {
                String host = args.length > 1 ? args[1] : Config.DEFAULT_HOST;
                int port = parsePort(args, 2, Config.DEFAULT_PORT);
                runInteractiveClient(host, port);
            }
            case "help", "-h", "--help" -> printUsage();
            default -> {
                System.err.println("[!] Unknown mode: '" + mode + "'");
                printUsage();
            }
        }
    }

    private static void printUsageAndPrompt() {
        System.out.println("==================================================");
        System.out.println("             JavaRemote Bootstrap                 ");
        System.out.println("==================================================");
        System.out.println("Choose startup mode:");
        System.out.println("  1. Start Server");
        System.out.println("  2. Start Client");
        System.out.println("  3. Exit");
        System.out.print("\nEnter choice (1/2/3): ");

        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    Config config = new Config();
                    RemoteServer server = new RemoteServer(config);
                    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
                    server.start();
                }
                case "2" -> {
                    Config config = new Config();
                    runInteractiveClient(config.getHost(), config.getPort());
                }
                case "3" -> System.out.println("Exiting JavaRemote.");
                default -> {
                    System.out.println("Invalid selection. Showing CLI usage:");
                    printUsage();
                }
            }
        } else {
            printUsage();
        }
    }

    private static void runInteractiveClient(String host, int port) {
        System.out.println("==================================================");
        System.out.println("           JavaRemote Interactive Client          ");
        System.out.println("==================================================");
        System.out.println("Target: " + host + ":" + port);
        System.out.println("Type 'ping' to test connection, or 'exit' to quit.\n");

        try (RemoteClient client = new RemoteClient(host, port);
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

    private static int parsePort(String[] args, int index, int fallback) {
        if (args.length > index) {
            try {
                return Integer.parseInt(args[index]);
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }

    private static void printUsage() {
        System.out.println("\nUsage:");
        System.out.println("  java -jar javaremote.jar server [port]");
        System.out.println("  java -jar javaremote.jar client [host] [port]");
        System.out.println("\nExamples:");
        System.out.println("  java -jar javaremote.jar server 5000");
        System.out.println("  java -jar javaremote.jar client 127.0.0.1 5000");
    }
}
