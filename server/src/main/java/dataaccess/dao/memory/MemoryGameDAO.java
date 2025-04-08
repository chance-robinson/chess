package dataaccess.dao.memory;

import dataaccess.dao.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * In-memory implementation of the GameDAO interface.
 */
public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int newGameID = 1;

    /**
     * Clears all game data from memory HashMap games and resets newGameID to 1
     */
    @Override
    public void clear() {
        games.clear();
        newGameID = 1;
    }

    /**
     * Creates a new game entry
     *
     * @param gameData the game data to be created from memory
     */
    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    /**
     * Returns the game data for a given game ID
     *
     * @param gameID the game ID to retrieve from memory
     * @return the game data for the given game ID
     */
    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    /**
     * Generates a unique game ID
     *
     * @return a new unique game ID based on newGameID value
     */
    @Override
    public int generateGameID() {
        return newGameID++;
    }

    /**
     * Returns a list of all games
     *
     * @return a list of all game data from memory
     */
    @Override
    public ArrayList<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    /**
     * Returns game data for a given game
     *
     * @param gameName the game name to retrieve game data for from memory
     * @return game data for the given game name
     */
    @Override
    public GameData getGameByGameName(String gameName) {
        for (GameData game : games.values()) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    /**
     * Updates a game with new game data
     *
     * @param gameData the new game data to be updated from memory
     */
    @Override
    public void update(GameData gameData) {
        games.remove(gameData.gameID());
        games.put(gameData.gameID(), gameData);
    }
}
