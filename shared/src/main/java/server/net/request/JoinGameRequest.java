package server.net.request;

/**
 * Request for when a user requests to join a game
 *
 * @param playerColor desired playerColor on game to join
 * @param gameID id of desired game to join
 */
public record JoinGameRequest(String playerColor, int gameID) {}
