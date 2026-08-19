package com.javaremote.client;

import com.javaremote.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * TCP client responsible for connecting to a JavaRemote server, sending commands, and receiving responses.
 */
public class RemoteClient implements AutoCloseable {

    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean connected;

    public RemoteClient() {
        Config config = new Config();
        this.host = config.getHost();
        this.port = config.getPort();
    }

    public RemoteClient(String host, int port) {
        this.host = (host == null || host.isBlank()) ? Config.DEFAULT_HOST : host.trim();
        this.port = (port > 0 && port <= 65535) ? port : Config.DEFAULT_PORT;
    }

    /**
     * Connects to the remote server.
     *
     * @throws IOException if the connection fails
     */
    public void connect() throws IOException {
        if (connected) {
            return;
        }

        System.out.println("[*] Connecting to JavaRemote Server at " + host + ":" + port + "...");
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        this.connected = true;
        System.out.println("[+] Successfully connected to server at " + host + ":" + port);
    }

    /**
     * Sends a command or message to the server and returns the server's response.
     *
     * @param command string command to send (e.g. "ping")
     * @return response from the server, or null if disconnected
     * @throws IOException on transmission failure
     */
    public String sendCommand(String command) throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IOException("Client is not connected to a server.");
        }

        if (command == null || command.isBlank()) {
            return null;
        }

        writer.println(command);
        return receiveMessage();
    }

    /**
     * Sends a message without waiting for a return value.
     */
    public void sendMessage(String message) throws IOException {
        if (!connected || writer == null) {
            throw new IOException("Client is not connected to a server.");
        }
        writer.write(message);
        writer.write("<END>");
        writer.flush();
    }

    /**
     * Reads a single line response from the server.
     */
    public String receiveMessage() throws IOException {
        if (!connected || reader == null) {
            throw new IOException("Client is not connected to a server.");
        }

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.equals("<END>")) {
                break;
            }

            response.append(line).append(System.lineSeparator());
        }

        return response.toString();
    }

    /**
     * Disconnects from the server and releases all socket and stream resources.
     */
    public void disconnect() {
        connected = false;
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {
        }
        System.out.println("[-] Disconnected from server.");
    }

    @Override
    public void close() {
        disconnect();
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
