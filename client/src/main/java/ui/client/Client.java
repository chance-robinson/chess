package ui.client;

import exception.ResponseException;

public interface Client {
    ClientResult eval(String input) throws ResponseException;
}
