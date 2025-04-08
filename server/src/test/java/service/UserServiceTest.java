package service;

import dataAccess.dao.memory.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Test
    public void clearPositive() {
        UserService userService = new UserService(new MemoryUserDAO());

        UserData user = new UserData("testUser", "testPass", "test@example.com");
        userService.userDAO.createUser(user);

        assertNotNull(userService.userDAO.getUser("testUser"));

        userService.clear();

        assertNull(userService.userDAO.getUser("testUser"));
    }
}
