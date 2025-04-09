package service.sql;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.GameDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.sql.SQLAuthDAO;
import dataaccess.dao.sql.SQLGameDAO;
import dataaccess.dao.sql.SQLUserDAO;
import service.GeneralServiceTest;

public class SQLGeneralServiceTest extends GeneralServiceTest {
    @Override
    protected GameDAO createGameDAO() {
        return new SQLGameDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new SQLAuthDAO();
    }

    @Override
    protected UserDAO createUserDAO() {
        return new SQLUserDAO();
    }
}
