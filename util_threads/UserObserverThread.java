package multi_users_chat.util_threads;

import multi_users_chat.utils.User;

import java.io.IOException;
import java.util.List;

public class UserObserverThread extends Thread {
    private final List<User> users;
    private final List<Thread> userInputThreads;

    public UserObserverThread(List<User> users, List<Thread> userInputThreads) {
        this.users = users;
        this.userInputThreads = userInputThreads;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Thread deadThread = userInputThreads.stream()
                    .filter(thread -> !thread.isAlive())
                    .findFirst()
                    .orElse(null);
            if (deadThread != null) {
                userInputThreads.remove(deadThread);
                removeUserIfThreadDead((ReaderThread) deadThread);
                System.out.println("кто-то вышел");
            }
        }
    }

    private void removeUserIfThreadDead(ReaderThread deadThread) {
        int id = deadThread.getClientId();
        try {
            users.stream()
                    .filter(user -> user.clientId() == id)
                    .findFirst()
                    .get()
                    .client()
                    .close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        users.removeIf(user -> user.clientId() == id);
    }
}
