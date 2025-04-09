package service.memory;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.memory.MemoryAuthDAO;
import dataaccess.dao.memory.MemoryGameDAO;
import service.GameServiceTest;

public class MemoryGameServiceTest extends GameServiceTest {
    @Override
    protected GameDAO createGameDAO() {
        return new MemoryGameDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new MemoryAuthDAO();
    }
}