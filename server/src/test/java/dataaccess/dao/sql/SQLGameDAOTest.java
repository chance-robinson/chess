package dataaccess.dao.sql;

import dataaccess.dao.GameDAO;
import dataaccess.dao.GameDAOTest;

public class SQLGameDAOTest extends GameDAOTest {
    @Override
    protected GameDAO createGameDAO() {
        return new SQLGameDAO();
    }
}
