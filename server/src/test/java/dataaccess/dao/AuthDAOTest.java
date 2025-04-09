package dataaccess.dao;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;

public abstract class AuthDAOTest {
    private AuthDAO authDAO;
    private AuthData testAuth;
    private AuthData testAuth2;

    protected abstract AuthDAO createAuthDAO();

    @BeforeEach
    public void initialize() {
        this.authDAO = createAuthDAO();
        authDAO.clear();
        this.testAuth = new AuthData("testAuth", "testUser");
        this.testAuth2 = new AuthData("testAuth2", "testUser2");
    }
}
