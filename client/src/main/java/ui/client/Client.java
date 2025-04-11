package ui.client;

import exception.ResponseException;

import static ui.EscapeSequences.*;

public interface Client {
    ClientResult eval(String input) throws ResponseException;

    default ClientResult handleError(ResponseException e) {
        System.out.printf(SET_TEXT_COLOR_RED + "    %s\n" + RESET_TEXT_COLOR, e.getMessage());
        return new ClientResult("error", null, null);
    }
}
