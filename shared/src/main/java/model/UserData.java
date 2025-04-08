package model;

/**
 * The shared data model for anything related to User data
 *
 * @param username a users username
 * @param password a users password
 * @param email a users email
 */
public record UserData(String username, String password, String email) {}
