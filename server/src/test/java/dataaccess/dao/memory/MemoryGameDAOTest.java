package dataaccess.dao.memory;

import dataaccess.dao.GameDAO;
import dataaccess.dao.GameDAOTest;

public class MemoryGameDAOTest extends GameDAOTest {
    @Override
    protected GameDAO createGameDAO() {
        return new MemoryGameDAO();
    }
}
