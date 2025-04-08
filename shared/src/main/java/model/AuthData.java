package model;

/**
 * The shared data model for anything related to Authentication data
 *
 * @param authToken randomly generated unique UUID authToken
 * @param username associated username
 */
public record AuthData(String authToken, String username) {}
