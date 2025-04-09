package service.sql;

import dataaccess.dao.AuthDAO;
import dataaccess.dao.UserDAO;
import dataaccess.dao.sql.SQLAuthDAO;
import dataaccess.dao.sql.SQLUserDAO;
import service.UserServiceTest;

public class SQLUserServiceTest extends UserServiceTest {
    @Override
    protected AuthDAO createAuthDAO() {
        return new SQLAuthDAO();
    }

    @Override
    protected UserDAO createUserDAO() {
        return new SQLUserDAO();
    }
}
