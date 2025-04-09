package dataaccess.dao.memory;

import dataaccess.dao.UserDAO;
import dataaccess.dao.UserDAOTest;

public class MemoryUserDAOTest extends UserDAOTest {
    @Override
    protected UserDAO createUserDAO() {
        return new MemoryUserDAO();
    }
}
