package multi_users_chat.util_threads;

import multi_users_chat.MultiUsersServer;
import multi_users_chat.utils.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientAcceptThread extends Thread {
    private final MultiUsersServer server;

    public ClientAcceptThread(MultiUsersServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = server.getServer().accept();
                User user = new User(generateId(), client);
                server.getUsers().add(user);
                System.out.println("кто-то подключился - " + server.getUsers().size() + " человек в чате");

                Thread readerThread = new ReaderThread(user, server.getMessages());
                server.getUserInputThreads().add(readerThread);
                readerThread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int generateId() {
        return new Random()
                .nextInt(server.getUsers().size() * 10_000,
                        server.getUsers().size() * 100_000 + 1);
    }
}