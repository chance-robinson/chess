package dataaccess.dao.memory;

import dataaccess.dao.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int newGameID = 1;

    @Override
    public void clear() {
        games.clear();
        newGameID = 1;
    }

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public int generateGameID() {
        return newGameID++;
    }

    @Override
    public ArrayList<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGameByGameName(String gameName) {
        for (GameData game : games.values()) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void update(GameData gameData) {
        games.remove(gameData.gameID());
        games.put(gameData.gameID(), gameData);
    }
}
