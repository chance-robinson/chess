package dataaccess.dao;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clear();
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    int generateGameID();
    ArrayList<GameData> getAllGames();
    GameData getGameByGameName(String gameName);
    void update(GameData gameData);
}
