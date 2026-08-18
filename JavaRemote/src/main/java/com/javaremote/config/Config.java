package com.javaremote.config;

/**
 * Centralized application configuration.
 * Reads properties from environment variables or system properties with safe default fallbacks.
 */
public final class Config {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 5000;
    public static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final int DEFAULT_SO_TIMEOUT_MS = 0; // 0 = infinite timeout

    private final String host;
    private final int port;
    private final int threadPoolSize;

    public Config() {
        this(resolveHost(), resolvePort(), resolveThreadPoolSize());
    }

    public Config(String host, int port) {
        this(host, port, DEFAULT_THREAD_POOL_SIZE);
    }

    public Config(String host, int port, int threadPoolSize) {
        this.host = (host == null || host.isBlank()) ? DEFAULT_HOST : host.trim();
        this.port = (port > 0 && port <= 65535) ? port : DEFAULT_PORT;
        this.threadPoolSize = (threadPoolSize > 0) ? threadPoolSize : DEFAULT_THREAD_POOL_SIZE;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    private static String resolveHost() {
        String envHost = System.getenv("JAVAREMOTE_HOST");
        if (envHost != null && !envHost.isBlank()) {
            return envHost;
        }
        return System.getProperty("javaremote.host", DEFAULT_HOST);
    }

    private static int resolvePort() {
        String envPort = System.getenv("JAVAREMOTE_PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                return Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String sysPort = System.getProperty("javaremote.port");
        if (sysPort != null && !sysPort.isBlank()) {
            try {
                return Integer.parseInt(sysPort.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_PORT;
    }

    private static int resolveThreadPoolSize() {
        String envThreads = System.getenv("JAVAREMOTE_THREADS");
        if (envThreads != null && !envThreads.isBlank()) {
            try {
                return Integer.parseInt(envThreads.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_THREAD_POOL_SIZE;
    }

    @Override
    public String toString() {
        return "Config{host='" + host + "', port=" + port + ", threadPoolSize=" + threadPoolSize + "}";
    }
}
