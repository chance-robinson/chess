package server.net.result;

/**
 * Result for when an authenticated user creates a game
 *
 * @param gameID the newly created gameID
 */
public record CreateGameResult(Integer gameID) {}
