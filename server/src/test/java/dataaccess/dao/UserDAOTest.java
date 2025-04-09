package dataaccess.dao;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;

public abstract class UserDAOTest {
    private UserDAO userDAO;
    private UserData testUser;
    private UserData testUser2;

    protected abstract UserDAO createUserDAO();

    @BeforeEach
    public void initialize() {
        this.userDAO = createUserDAO();
        userDAO.clear();
        this.testUser = new UserData("testUser", "testPass", "test@example.com");
        this.testUser2 = new UserData("testUser2", "testPass2", "test2@example.com");
    }
}
