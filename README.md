# JavaRemote

Base modular e limpa para comunicação remota cliente-servidor utilizando sockets TCP em Java 17+.

---

## 📋 Descrição

O **JavaRemote** é uma estrutura inicial, extensível e organizada projetada para servir como ponto de partida na construção de ferramentas de administração e comunicação remota autorizada.

Ele estabelece um servidor TCP multithread capaz de receber múltiplas conexões simultâneas de clientes, processar mensagens/comandos de texto através de um sistema desacoplado de comandos (`Command`) e responder de forma síncrona aos clientes.

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+** (com suporte a recursos modernos da linguagem)
- **Maven** (gerenciamento de build e dependências)
- **Sockets TCP (`java.net.ServerSocket`, `java.net.Socket`)**
- **ExecutorService / ThreadPool** (gerenciamento concorrente de conexões)
- **JUnit 5** (testes unitários)

---

## 📁 Estrutura do Projeto

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
    │               ├── Main.java               # Ponto de entrada CLI (inicia Servidor ou Cliente)
    │               ├── MainServer.java         # Execução direta do Servidor (IDE 1-click)
    │               ├── MainClient.java         # Execução direta do Cliente (IDE 1-click)
    │               ├── client/
    │               │   └── RemoteClient.java   # Gerenciamento da conexão TCP do lado cliente
    │               ├── server/
    │               │   └── RemoteServer.java   # Servidor TCP com pool de threads e lifecycle
    │               ├── connection/
    │               │   └── ConnectionHandler.java # Ciclo de vida e I/O de cada cliente conectado
    │               ├── command/
    │               │   ├── Command.java        # Interface base para comandos
    │               │   ├── PingCommand.java    # Comando de teste (retorna "pong")
    │               │   └── CommandRegistry.java# Registro e despacho dinâmico de comandos
    │               └── config/
    │                   └── Config.java         # Configurações centralizadas (Host, Porta, Threads)
    └── test/
        └── java/
            └── com/
                └── javaremote/
                    └── CommandTest.java        # Testes unitários do pipeline de comandos
```

---

## ⚙️ Como Compilar com Maven

Abra o terminal na pasta do projeto (`JavaRemote/`) e execute:

```bash
# Limpar e compilar o projeto com testes
mvn clean package
```

Isso irá gerar o arquivo `.jar` executável na pasta `target/javaremote-1.0.0-SNAPSHOT.jar`.

Para rodar apenas os testes:
```bash
mvn test
```

---

## 🚀 Como Iniciar

### 1. Iniciar o Servidor

Você pode iniciar o servidor de 3 formas:

**Opção A — Executando a classe Main via Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.javaremote.Main" -Dexec.args="server"
```
*(Para especificar uma porta personalizada, ex: `server 8080`)*

**Opção B — Executando o JAR compilado:**
```bash
java -jar target/javaremote-1.0.0-SNAPSHOT.jar server 5000
```

**Opção C — Diretamente na sua IDE (IntelliJ / Eclipse / VS Code):**
- Abra e execute a classe `com.javaremote.MainServer`.

---

### 2. Iniciar o Cliente

Em outro terminal ou janela da IDE:

**Opção A — Executando a classe Main via Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.javaremote.Main" -Dexec.args="client"
```
*(Para conectar em host/porta específicos: `client 127.0.0.1 5000`)*

**Opção B — Executando o JAR compilado:**
```bash
java -jar target/javaremote-1.0.0-SNAPSHOT.jar client 127.0.0.1 5000
```

**Opção C — Diretamente na sua IDE:**
- Abra e execute a classe `com.javaremote.MainClient`.

---

## 💬 Exemplo Básico de Comunicação

Quando o cliente conectar ao servidor, o console interativo estará pronto para entrada:

```text
==================================================
           JavaRemote Interactive Client          
==================================================
Target: 127.0.0.1:5000
Type 'ping' to test connection, or 'exit' to quit.

client> ping
server> pong

client> status
server> ERROR: Unknown command 'status'. Available: ping

client> exit
[-] Disconnected from server.
```

No console do servidor você verá os logs de conexão:
```text
[+] Client connected: /127.0.0.1:54321
[-] Client disconnected: /127.0.0.1:54321
```

---

## 🔧 Configurações Personalizadas

As configurações padrão são:
- **Host:** `127.0.0.1`
- **Porta:** `5000`
- **Tamanho do Thread Pool:** `10`

Você pode sobrescrever essas configurações sem alterar o código através de variáveis de ambiente:
- `JAVAREMOTE_HOST`
- `JAVAREMOTE_PORT`
- `JAVAREMOTE_THREADS`

Ou propriedades do sistema Java:
```bash
java -Djavaremote.port=8080 -jar target/javaremote-1.0.0-SNAPSHOT.jar server
```

---

## 🧭 Próximos Passos Sugeridos

1. **Novos Comandos:** Crie novas classes que implementem `com.javaremote.command.Command` (ex: `InfoCommand`, `TimeCommand`, `EchoCommand`) e registre-as no `CommandRegistry`.
2. **Camada de Autenticação Segura:** Adicionar handshake com token ou criptografia de sessão (TLS/SSL via `SSLSocket`).
3. **Serialização de Protocolo:** Implementar um protocolo em JSON ou binário para troca de mensagens estruturadas.
4. **Heartbeat / Keep-Alive:** Adicionar ping periódico automático para monitoramento da integridade da conexão.
