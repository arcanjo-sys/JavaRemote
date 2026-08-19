# JavaRemote

A modular and extensible Java 17+ client-server communication framework built on TCP sockets.

JavaRemote provides a clean foundation for building authorized remote communication and administration tools. The project focuses on network communication, connection lifecycle management, concurrent client handling, and an extensible command-processing architecture.

> **Project status:** Early development / `1.0.0-SNAPSHOT`

---

## Overview

JavaRemote implements a lightweight TCP client-server architecture where multiple clients can connect to a server and exchange text-based commands and responses.

The project was designed with separation of concerns in mind, keeping networking, connection handling, command processing, and configuration isolated into dedicated components.

The current version provides a minimal but functional communication pipeline:

```text
RemoteClient
     │
     │ TCP / UTF-8
     ▼
RemoteServer
     │
     ▼
ExecutorService
     │
     ▼
ConnectionHandler
     │
     ▼
CommandRegistry
     │
     ▼
PingCommand
     │
     ▼
"pong"
```

---

## Features

* TCP client-server communication
* Java 17+
* Maven-based project
* Multiple concurrent client connections
* Configurable thread pool
* UTF-8 text communication
* Extensible command architecture
* Thread-safe command registry
* Graceful connection shutdown
* Graceful server shutdown
* Environment variable configuration
* Java system property configuration
* Interactive command-line client
* JUnit 5 unit tests
* Clean separation between networking and command processing

---

## Technology Stack

* **Java 17+**
* **Maven**
* **Java Sockets**

  * `ServerSocket`
  * `Socket`
* **ExecutorService**
* **ConcurrentHashMap**
* **JUnit 5**

No external framework is required for the core networking layer.

---

## Project Structure

```text
JavaRemote/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── javaremote/
    │               ├── Main.java
    │               ├── MainServer.java
    │               ├── MainClient.java
    │               │
    │               ├── client/
    │               │   └── RemoteClient.java
    │               │
    │               ├── server/
    │               │   └── RemoteServer.java
    │               │
    │               ├── connection/
    │               │   └── ConnectionHandler.java
    │               │
    │               ├── command/
    │               │   ├── Command.java
    │               │   ├── PingCommand.java
    │               │   └── CommandRegistry.java
    │               │
    │               └── config/
    │                   └── Config.java
    │
    └── test/
        └── java/
            └── com/
                └── javaremote/
                    └── CommandTest.java
```

---

## Architecture

### RemoteServer

Responsible for:

* Opening the TCP `ServerSocket`
* Accepting incoming connections
* Managing the server lifecycle
* Dispatching clients to the executor
* Gracefully shutting down active resources

The server uses an `ExecutorService` to handle multiple clients concurrently.

---

### RemoteClient

Responsible for:

* Connecting to the configured server
* Maintaining the TCP connection
* Sending commands
* Receiving server responses
* Closing the connection safely

`RemoteClient` implements `AutoCloseable`, allowing it to be used with Java's try-with-resources pattern.

---

### ConnectionHandler

Each connected client is handled by an independent `ConnectionHandler`.

Its responsibilities include:

* Reading incoming messages
* Decoding UTF-8 text
* Passing commands to `CommandRegistry`
* Returning responses to the client
* Handling connection termination
* Releasing network resources

---

### Command

`Command` defines the contract used by the command-processing layer.

Example:

```java
public interface Command {

    String getName();

    String execute(String[] args);
}
```

This allows new commands to be added without modifying the networking layer.

---

### CommandRegistry

`CommandRegistry` acts as the command dispatcher.

It:

1. Receives raw input.
2. Parses the command and arguments.
3. Finds the corresponding registered command.
4. Executes it.
5. Returns the result to the client.

The registry uses a thread-safe command map based on `ConcurrentHashMap`.

---

### PingCommand

The current reference command is `ping`.

Example:

```text
client> ping
server> pong
```

This provides a minimal end-to-end test of the communication pipeline.

---

## Configuration

Default configuration:

| Property    | Default     |
| ----------- | ----------- |
| Host        | `127.0.0.1` |
| Port        | `5000`      |
| Thread Pool | `10`        |

Configuration can be provided through environment variables:

```text
JAVAREMOTE_HOST
JAVAREMOTE_PORT
JAVAREMOTE_THREADS
```

Java system properties are also supported:

```bash
java -Djavaremote.port=8080 \
     -jar target/javaremote-1.0.0-SNAPSHOT.jar server
```

---

## Building the Project

Clone the repository and enter the project directory:

```bash
cd JavaRemote
```

Build the project:

```bash
mvn clean package
```

Run the test suite:

```bash
mvn test
```

The generated JAR will be available under:

```text
target/javaremote-1.0.0-SNAPSHOT.jar
```

---

## Running the Server

### Using Maven

```bash
mvn exec:java \
  -Dexec.mainClass="com.javaremote.Main" \
  -Dexec.args="server"
```

Using a custom port:

```bash
mvn exec:java \
  -Dexec.mainClass="com.javaremote.Main" \
  -Dexec.args="server 8080"
```

### Using the packaged JAR

```bash
java -jar target/javaremote-1.0.0-SNAPSHOT.jar server 5000
```

### Using an IDE

Run:

```text
com.javaremote.MainServer
```

---

## Running the Client

Start the client from a second terminal:

### Using Maven

```bash
mvn exec:java \
  -Dexec.mainClass="com.javaremote.Main" \
  -Dexec.args="client"
```

### Using the packaged JAR

```bash
java -jar target/javaremote-1.0.0-SNAPSHOT.jar client 127.0.0.1 5000
```

### Using an IDE

Run:

```text
com.javaremote.MainClient
```

---

## Example Session

### Client

```text
==================================================
           JavaRemote Interactive Client
==================================================

Target: 127.0.0.1:5000

Type 'ping' to test the connection.
Type 'exit' to disconnect.

client> ping
server> pong

client> status
server> ERROR: Unknown command 'status'. Available: ping

client> exit
[-] Disconnected from server.
```

### Server

```text
[+] Server started on 127.0.0.1:5000

[+] Client connected: /127.0.0.1:54321
[-] Client disconnected: /127.0.0.1:54321
```

---

## Adding a New Command

New functionality can be added by implementing the `Command` interface.

Example:

```java
package com.javaremote.command;

public class InfoCommand implements Command {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String execute(String[] args) {
        return "OS: " + System.getProperty("os.name")
                + " | Java: " + System.getProperty("java.version");
    }
}
```

Register the command with the `CommandRegistry`:

```java
register(new InfoCommand());
```

The command can then be invoked through the client:

```text
client> info
server> OS: Linux | Java: 17.0.x
```

---

## Testing

The project currently includes JUnit 5 tests covering the command-processing layer.

Current test coverage includes:

* `PingCommand` response
* Command registry processing
* Case-insensitive commands
* Unknown command handling
* Empty input handling

Run the tests with:

```bash
mvn test
```

---

## Roadmap

The current implementation intentionally keeps the core architecture small.

Planned development areas include:

* Authentication and authorization
* Secure communication using TLS
* Structured message protocol
* JSON-based communication
* Connection heartbeat / keep-alive
* Additional system information commands
* Improved logging and monitoring
* Remote screen capture
* Screen image transfer
* Improved client/server protocol
* More integration tests

### Remote Screen Capture

A future version of JavaRemote is planned to be integrated into **ArcImage**, a separate project focused on significantly compressing an image file..

The intended architecture is:

```text
┌─────────────────┐
│   JavaRemote    │
│                 │
│ TCP Connection  │
└────────┬────────┘
         │
         │ Screen Capture Request
         ▼
┌─────────────────┐
│    ArcImage     │
│                 │
│ Screen Capture  │
└────────┬────────┘
         │
         │ Image Data
         ▼
┌─────────────────┐
│   JavaRemote    │
│                 │
│ Image Transfer  │
└─────────────────┘
```

The integration is planned as a modular component rather than embedding the screen-capture implementation directly into the networking layer.

---

## Security

JavaRemote is intended for authorized environments, development, testing, and controlled administration scenarios.

The current version is **not designed to provide secure remote administration over untrusted networks**.

Before using the project outside a controlled environment, additional security mechanisms should be implemented, including:

* Authentication
* Authorization
* TLS encryption
* Secure credential handling
* Input validation
* Connection limits
* Audit logging
* Secure protocol design

Arbitrary operating-system command execution is intentionally outside the scope of the current base implementation.

---

## Project Goals

The main goal of JavaRemote is to provide a clean foundation for experimenting with:

* Java networking
* TCP communication
* Concurrent programming
* Client-server architectures
* Command dispatching
* Resource management
* Extensible software architecture
* Secure remote administration concepts

The project is intentionally modular so that additional functionality can be introduced without coupling the networking layer to application-specific features.

---

## License

Add your preferred license here.

---

## Author

Developed as an independent Java networking project and continued as an evolving software engineering portfolio project.
