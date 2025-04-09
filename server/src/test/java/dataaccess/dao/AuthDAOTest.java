package dataaccess.dao;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void clear() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        authDAO.clear();
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void createAuth() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        AuthData auth = authDAO.getAuth(testAuth.authToken());

        assertNotNull(auth);
        assertEquals(testAuth, auth);
    }

    @Test
    public void createAuthExists() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        assertThrows(RuntimeException.class, () -> authDAO.createAuth(testAuth.authToken(), testAuth));
    }

    @Test
    public void getAuth() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        AuthData retrievedAuth = authDAO.getAuth(testAuth.authToken());
        assertNotNull(retrievedAuth);
        assertEquals(retrievedAuth.authToken(), testAuth.authToken());
        assertEquals(retrievedAuth, testAuth);
    }

    @Test
    public void getAuthNone() {
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void deleteAuth() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        authDAO.deleteAuth(testAuth.authToken());

        AuthData retrievedAuth = authDAO.getAuth(testAuth.authToken());
        assertNull(retrievedAuth);

    }

    @Test
    public void deleteAuthNone() {
        authDAO.createAuth(testAuth.authToken(), testAuth);
        assertNotNull(authDAO.getAuth(testAuth.authToken()));

        authDAO.deleteAuth(testAuth.authToken()+":test");

        assertNotNull(authDAO.getAuth(testAuth.authToken()));
    }
}
