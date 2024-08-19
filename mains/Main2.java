package multi_users_chat.mains;

import multi_users_chat.Client;

public class Main2 {
    public static void main(String[] args) {
        Client c = new Client("127.0.0.1", 9000);
        c.start();
    }
}
