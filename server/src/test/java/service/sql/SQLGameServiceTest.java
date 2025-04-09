package service.sql;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.sql.SQLAuthDAO;
import dataaccess.dao.sql.SQLGameDAO;
import service.GameServiceTest;

public class SQLGameServiceTest extends GameServiceTest {
    @Override
    protected GameDAO createGameDAO() {
        return new SQLGameDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new SQLAuthDAO();
    }
}