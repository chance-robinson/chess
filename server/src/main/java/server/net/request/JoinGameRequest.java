package server.net.request;

public record JoinGameRequest(String playerColor, int gameID, String authToken) {}
