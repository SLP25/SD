package client.exceptions;

import common.messages.NotAuthenticatedResponse;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException(String msg) {
        super(msg);
    }
}
