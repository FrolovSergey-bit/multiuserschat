package multi_users_chat.utils;

import java.net.Socket;

public record User (int clientId, Socket client) {
}
