package ui.client;

import exception.ResponseException;

import java.io.IOException;

import static ui.EscapeSequences.*;

/**
 * The interface which all of the 3 clients: InGame, LoggedIn, and PreLogin are implementing.
 */
public interface Client {
    ClientResult eval(String input) throws ResponseException, IOException;

    /**
     * This handles the printing on the terminal for when an error occurs.
     *
     * @param e the ResponseException to display
     * @return a generic ClientResult response indicating there was an error
     */
    default ClientResult handleError(ResponseException e) {
        System.out.printf(SET_TEXT_COLOR_RED + "    %s\n" + RESET_TEXT_COLOR, e.getMessage());
        return new ClientResult("error", null, null);
    }
}
