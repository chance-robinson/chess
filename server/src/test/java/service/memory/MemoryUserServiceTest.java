package service.memory;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryUserDAO;
import service.UserServiceTest;

public class MemoryUserServiceTest extends UserServiceTest {
    @Override
    protected AuthDAO createAuthDAO() {
        return new MemoryAuthDAO();
    }

    @Override
    protected UserDAO createUserDAO() {
        return new MemoryUserDAO();
    }
}
