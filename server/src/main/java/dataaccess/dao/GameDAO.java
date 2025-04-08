package dataaccess.dao;

import model.GameData;

import java.util.ArrayList;

/**
 * Interface for the MemoryGameDAO and SQLGameDAO to implement
 */
public interface GameDAO {
    /**
     * Clears all game data
     */
    void clear();

    /**
     * Creates a new game entry
     *
     * @param gameData the game data to be created
     */
    void createGame(GameData gameData);

    /**
     * Returns the game data for a given game ID
     *
     * @param gameID the game ID to retrieve
     * @return the game data for the given game ID
     */
    GameData getGame(int gameID);

    /**
     * Generates a unique game ID
     *
     * @return a new unique game ID
     */
    int generateGameID();

    /**
     * Returns a list of all games
     *
     * @return a list of all game data
     */
    ArrayList<GameData> getAllGames();

    /**
     * Returns game data for a given game
     *
     * @param gameName the game name to retrieve game data for
     * @return game data for the given game name
     */
    GameData getGameByGameName(String gameName);

    /**
     * Updates a game with new game data
     *
     * @param gameData the new game data to be updated
     */
    void update(GameData gameData);
}
