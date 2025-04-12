package ui.client;

/**
 * The list of States that the Repl class can be in at any time to decide
 * which client it should be using.
 */
public enum ClientState {
    SIGNEDOUT,
    SIGNEDIN,
    INGAME,
    OBSERVER
}
