package com.javaremote;

import com.javaremote.config.Config;
import com.javaremote.server.RemoteServer;

/**
 * Dedicated entry point to run the RemoteServer directly (convenient for IDE execution).
 */
public class MainServer {

    public static void main(String[] args) {
        Config config = new Config();
        RemoteServer server = new RemoteServer(config);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
    }
}
