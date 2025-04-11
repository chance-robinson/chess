package ui.client;

import exception.ResponseException;

public interface Client {
    ClientResult eval(String input) throws ResponseException;

    default ClientResult handleError(ResponseException e) {
        System.out.printf("%s\n", e.getMessage());
        return new ClientResult("error", null, null);
    }
}
