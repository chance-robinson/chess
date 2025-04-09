package dataaccess.dao.memory;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.AuthDAOTest;

public class MemoryAuthDAOTest extends AuthDAOTest {
    @Override
    protected AuthDAO createAuthDAO() {
        return new MemoryAuthDAO();
    }
}
