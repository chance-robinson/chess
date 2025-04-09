package dataaccess.dao;

import org.junit.jupiter.api.BeforeEach;

public abstract class GameDAOTest {
    private GameDAO gameDAO;

    protected abstract GameDAO createGameDAO();

    @BeforeEach
    public void initialize() {
        this.gameDAO = createGameDAO();
        gameDAO.clear();
    }
}
