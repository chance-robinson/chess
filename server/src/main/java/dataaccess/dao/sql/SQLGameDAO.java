package dataaccess.dao.sql;

import dataaccess.dao.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {
    /**
     * Clears all game data
     */
    @Override
    public void clear() {

    }

    /**
     * Creates a new game entry
     *
     * @param gameData the game data to be created
     */
    @Override
    public void createGame(GameData gameData) {

    }

    /**
     * Returns the game data for a given game ID
     *
     * @param gameID the game ID to retrieve
     * @return the game data for the given game ID
     */
    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    /**
     * Generates a unique game ID
     *
     * @return a new unique game ID
     */
    @Override
    public int generateGameID() {
        return 0;
    }

    /**
     * Returns a list of all games
     *
     * @return a list of all game data
     */
    @Override
    public ArrayList<GameData> getAllGames() {
        return null;
    }

    /**
     * Returns game data for a given game
     *
     * @param gameName the game name to retrieve game data for
     * @return game data for the given game name
     */
    @Override
    public GameData getGameByGameName(String gameName) {
        return null;
    }

    /**
     * Updates a game with new game data
     *
     * @param gameData the new game data to be updated
     */
    @Override
    public void update(GameData gameData) {

    }
}
