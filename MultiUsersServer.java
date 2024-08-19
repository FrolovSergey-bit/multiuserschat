package multi_users_chat;

import multi_users_chat.util_threads.ClientAcceptThread;
import multi_users_chat.util_threads.ServerOutputThread;
import multi_users_chat.util_threads.UserObserverThread;
import multi_users_chat.utils.Message;
import multi_users_chat.utils.User;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

// TODO в отдельном потоке слушать команду exit, после ее введения отключить всех юзеров от сервера, закрыть сервер и его ресурсы и выйти с кодом 0

// TODO подумать над тем, есть ли архитектурные проблемы, исправить, сообщить
public class MultiUsersServer {
    private final int BACKLOG = 4; // количество человек на сервере
    private final List<User> users; // список подключенных клиентов
    private final List<Thread> userInputThreads; // список потоков чтения
    private final Queue<Message> messages;
    private final ServerSocket server;

    public MultiUsersServer(String host, int port) {
        this.server = serverInit(host, port);
        this.users = new CopyOnWriteArrayList<>();
        this.userInputThreads = new CopyOnWriteArrayList<>();
        this.messages = new ConcurrentLinkedQueue<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public ServerSocket getServer() {
        return server;
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public List<Thread> getUserInputThreads() {
        return userInputThreads;
    }

    public void start() {
        try {
            ClientAcceptThread acceptThread = new ClientAcceptThread(this);
            acceptThread.start();

            Thread serverOutputThread = new ServerOutputThread(messages, users);
            serverOutputThread.start();

            Thread userObserverThread = new UserObserverThread(users, userInputThreads);
            userObserverThread.start();

            // TODO решить проблему с необходимостью заснуть
            Thread.sleep(600_000);

            acceptThread.join();
            serverOutputThread.join();
            userObserverThread.join();

            server.close();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ServerSocket serverInit(String host, int port) {
        System.out.println("server starting...");
        try {
            return new ServerSocket(port, BACKLOG, InetAddress.getByName(host));
        } catch (IOException e) {
            System.out.println("ошибка при инициализации серверного сокета");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MultiUsersServer server = new MultiUsersServer("127.0.0.1", 9000);
        server.start();
    }
}
