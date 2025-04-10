package server.net.request;

/**
 * Request for when someone wants to register a user
 *
 * @param username username to register
 * @param password password to hash and register with
 * @param email email to register with
 */
public record RegisterRequest(String username, String password, String email) {}
