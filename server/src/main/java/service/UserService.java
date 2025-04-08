package service;

import dataAccess.dao.GameDAO;
import dataAccess.dao.UserDAO;

public class UserService {
    final UserDAO userDAO;
    final GameDAO gameDAO;


    public UserService(UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        userDAO.clear();
        gameDAO.clear();
    }
}
