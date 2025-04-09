package dataaccess.dao.sql;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.AuthDAOTest;

public class SQLAuthDAOTest extends AuthDAOTest {
    @Override
    protected AuthDAO createAuthDAO() {
        return new SQLAuthDAO();
    }
}
