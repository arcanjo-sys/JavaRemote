package com.javaremote.server;

import com.javaremote.command.CommandRegistry;
import com.javaremote.config.Config;
import com.javaremote.connection.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded TCP server that accepts and handles remote client connections.
 */
public class RemoteServer {

    private final Config config;
    private final CommandRegistry commandRegistry;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public RemoteServer(Config config) {
        this.config = config != null ? config : new Config();
        this.commandRegistry = new CommandRegistry();
    }

    public RemoteServer(int port) {
        this(new Config(Config.DEFAULT_HOST, port));
    }

    public void registerCommand(com.javaremote.command.Command command) {
        this.commandRegistry.register(command);
    }

    /**
     * Starts the server loop in the current thread or blocks until stopped.
     */
    public void start() {
        if (!running.compareAndSet(false, true)) {
            System.out.println("[!] Server is already running.");
            return;
        }

        this.threadPool = Executors.newFixedThreadPool(config.getThreadPoolSize());

        try {
            this.serverSocket = new ServerSocket(config.getPort());
            System.out.println("==================================================");
            System.out.println(" JavaRemote Server started on port: " + config.getPort());
            System.out.println(" Thread pool size: " + config.getThreadPoolSize());
            System.out.println(" Ready to accept client connections...");
            System.out.println("==================================================");

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(new ConnectionHandler(clientSocket, commandRegistry));
                } catch (IOException e) {
                    if (!running.get()) {
                        break; // Server socket closed intentionally
                    }
                    System.err.println("[!] Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[X] Could not start server on port " + config.getPort() + ": " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Gracefully stops the server, closes sockets, and terminates the thread pool.
     */
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }

        System.out.println("[*] Shutting down JavaRemote Server...");

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[*] JavaRemote Server stopped successfully.");
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return config.getPort();
    }
}
