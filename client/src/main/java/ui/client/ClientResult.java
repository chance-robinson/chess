package ui.client;

/**
 * The Result for when an eval command is ran off a given client type: InGame, LoggedIn, PreLogin.
 *
 * @param result indicating the eval command that was ran or if an error happened
 * @param updatedState the ClientState value to be updated to for determining which client to use,
 *                     can be null if nothing changed
 * @param authToken the authToken from the response result, can be null on void or if authToken doesn't
 *                  need to be updated
 */
public record ClientResult(String result, ClientState updatedState, String authToken) { }
