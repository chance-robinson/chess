package server.net.result;

/**
 * Result for when a user is registered
 *
 * @param username name that was registered
 * @param authToken corresponding authToken to newly registered user
 */
public record RegisterResult(String username, String authToken) {}
