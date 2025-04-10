package server.net.request;

/**
 * Request for when an authenticated user wants to create a game
 *
 * @param gameName name of game that user wants to create
 */
public record CreateGameRequest(String gameName) {}
