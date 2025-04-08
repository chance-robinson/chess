package server.net.result;

/**
 * Result for when a user is logged in
 *
 * @param username username that was logged in
 * @param authToken corresponding authToken to logged-in user
 */
public record LoginResult(String username, String authToken) {}
