package uwu.openjfx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import uwu.openjfx.core.GameEnvironment;

/**
 * Base test class for all Royal Demons tests.
 * Provides common setup and teardown functionality.
 */
public abstract class TestBase {

    @BeforeEach
    public void setUp() {
        // Set testing mode for all tests
        MainApp.setIsTesting(true);

        // Reset game environment to clean state
        GameEnvironment env = GameEnvironment.get();
        // Add any common reset logic here if needed
    }

    @AfterEach
    public void tearDown() {
        // Clean up after tests
        // Add any common cleanup logic here if needed
    }

    /**
     * Helper method to create a mock game environment for testing
     *
     * @return GameEnvironment instance for testing
     */
    protected GameEnvironment createMockGameEnvironment() {
        return GameEnvironment.get();
    }
}