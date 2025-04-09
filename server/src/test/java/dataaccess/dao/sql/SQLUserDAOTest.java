package dataaccess.dao.sql;

import dataaccess.dao.UserDAO;
import dataaccess.dao.UserDAOTest;

public class SQLUserDAOTest extends UserDAOTest {
    @Override
    protected UserDAO createUserDAO() {
        return new SQLUserDAO();
    }
}
