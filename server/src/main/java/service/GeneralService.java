package service;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;

/**
 * The service pertaining to all methods related to the GameDAO and UserDAO
 */
public class GeneralService {
    final UserDAO userDAO;
    final GameDAO gameDAO;
    final AuthDAO authDAO;


    public GeneralService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    /**
     * Clears all data from DAOs: userDAO, gameDAO, authDAO
     */
    public void clear() {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
