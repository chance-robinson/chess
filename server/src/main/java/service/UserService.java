package service;

import dataAccess.dao.UserDAO;

public class UserService {
    final UserDAO userDAO;


    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void clear() {
        userDAO.clear();
    }
}
