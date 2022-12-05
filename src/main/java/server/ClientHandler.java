package server;

import common.User;

public class ClientHandler implements Runnable {

    private final User user;

    public ClientHandler(User user) {
        this.user = user;
    }

    @Override
    public void run() {

    }
}
