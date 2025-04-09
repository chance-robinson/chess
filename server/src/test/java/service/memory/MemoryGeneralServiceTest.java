package service.memory;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryGameDAO;
import dataaccess.dao.memory.MemoryUserDAO;
import service.GeneralServiceTest;

public class MemoryGeneralServiceTest extends GeneralServiceTest {
    @Override
    protected GameDAO createGameDAO() {
        return new MemoryGameDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new MemoryAuthDAO();
    }

    @Override
    protected UserDAO createUserDAO() {
        return new MemoryUserDAO();
    }
}
