package uwu.openjfx.core;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uwu.openjfx.config.DifficultyService;
import uwu.openjfx.config.WeaponBalanceService;

import java.util.Random;

/**
 * Centralized runtime settings and shared resources for the game session.
 * Provides access to flags like testing mode and shared random generator.
 */
public final class GameEnvironment {

    private static final GameEnvironment INSTANCE = new GameEnvironment();

    private final Random sharedRandom = new Random();
    private final WeaponBalanceService weaponBalanceService = new WeaponBalanceService();
    private final DifficultyService difficultyService = new DifficultyService();
    private boolean testing;

    private GameEnvironment() {
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "GameEnvironment is a process-wide singleton")
    public static GameEnvironment get() {
        return INSTANCE;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Shared Random provides deterministic behavior across systems.")
    public Random getRandom() {
        return sharedRandom;
    }

    public WeaponBalanceService getWeaponBalanceService() {
        return weaponBalanceService;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Difficulty service is designed as a shared application singleton.")
    public DifficultyService getDifficultyService() {
        return difficultyService;
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    public void reset() {
        testing = false;
        sharedRandom.setSeed(System.nanoTime());
    }
}
