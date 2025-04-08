package dataAccess.dao.memory;

import dataAccess.dao.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int newGameID = 0;

    @Override
    public void clear() {
        games.clear();
        newGameID = 0;
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
}
