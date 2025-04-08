package dataAccess.dao;

import model.GameData;

public interface GameDAO {
    void clear();
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    int generateGameID();
}
