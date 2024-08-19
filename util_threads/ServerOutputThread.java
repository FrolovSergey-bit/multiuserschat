package multi_users_chat.util_threads;

import multi_users_chat.utils.Message;
import multi_users_chat.utils.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

public class ServerOutputThread extends Thread {
    private final Queue<Message> messages;
    private final List<User> users;

    public ServerOutputThread(Queue<Message> messages, List<User> users) {
        this.messages = messages;
        this.users = users;
    }

    @Override
    public void run() {
        while (true) {
            if (!messages.isEmpty()) {
                Message messageToSend = messages.poll();
                users.forEach(user -> sendMessageExceptSender(user, messageToSend));
                System.out.println(messageToSend);
            }
        }
    }

    private void sendMessageExceptSender(User user, Message message) {
        if (message.clientId() != user.clientId()) {
            clientOutputStream(user.client()).println(message.message());
        }
    }

    private PrintWriter clientOutputStream(Socket client) {
        try {
            return new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream()
                            )
                    ), true
            );
        } catch (IOException e) {
            System.out.println("ошибка при инициализации потока вывода сервера");
            throw new RuntimeException(e);
        }
    }
}
