package server.net.request;

/**
 * Request for when a user logs in
 *
 * @param username username to login
 * @param password password to login with
 */
public record LoginRequest(String username, String password) {}
