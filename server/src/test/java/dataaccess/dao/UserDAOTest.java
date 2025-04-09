package dataaccess.dao;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class UserDAOTest {
    private UserDAO userDAO;
    private UserData testUser;

    protected abstract UserDAO createUserDAO();

    @BeforeEach
    public void initialize() {
        this.userDAO = createUserDAO();
        userDAO.clear();
        this.testUser = new UserData("testUser", "testPass", "test@example.com");
    }

    @Test
    public void clear() {
        userDAO.createUser(testUser);
        assertNotNull(userDAO.getUser(testUser.username()));

        userDAO.clear();
        assertNull(userDAO.getUser(testUser.username()));
    }

    @Test
    public void createUser() {
        userDAO.createUser(testUser);
        assertNotNull(userDAO.getUser(testUser.username()));
    }

    @Test
    public void createUserDuplicate() {
        userDAO.createUser(testUser);
        assertNotNull(userDAO.getUser(testUser.username()));
        assertThrows(RuntimeException.class, () -> userDAO.createUser(testUser));
    }

    @Test
    public void getUser() {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser(testUser.username());
        assertEquals(retrievedUser.email(), testUser.email());
        assertEquals(retrievedUser.username(), testUser.username());
        assertTrue(userDAO.isPasswordEqual(testUser.password(), retrievedUser.password()));
    }

    @Test
    public void getUserNone() {
        userDAO.createUser(testUser);
        assertNull(userDAO.getUser("DNE"));
    }

    @Test
    public void getUserByEmail() {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser(testUser.username());
        assertEquals(retrievedUser.email(), testUser.email());
        assertEquals(retrievedUser.username(), testUser.username());
    }

    @Test
    public void getUserByEmailNone() {
        userDAO.createUser(testUser);
        assertNull(userDAO.getUserByEmail("DNE_Email"));
    }

    @Test
    public void isPasswordEqual() {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser(testUser.username());
        assertEquals(retrievedUser.email(), testUser.email());
        assertEquals(retrievedUser.username(), testUser.username());
        assertTrue(userDAO.isPasswordEqual(testUser.password(), retrievedUser.password()));
    }

    @Test
    public void isPasswordEqualDifferentPasswords() {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser(testUser.username());
        assertEquals(retrievedUser.email(), testUser.email());
        assertEquals(retrievedUser.username(), testUser.username());
        assertFalse(userDAO.isPasswordEqual(testUser.password()+":test", retrievedUser.password()));
    }
}
