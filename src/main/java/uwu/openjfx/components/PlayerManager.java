package uwu.openjfx.components;

import uwu.openjfx.weapons.Weapon;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe менеджер для управления игроком.
 * Заменяет статические методы PlayerComponent для обеспечения безопасности.
 */
public class PlayerManager {

    private static final PlayerManager INSTANCE = new PlayerManager();
    private final AtomicReference<PlayerComponent> currentPlayer = new AtomicReference<>();

    private PlayerManager() {}

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Регистрация текущего игрока
     */
    public void registerPlayer(PlayerComponent player) {
        currentPlayer.set(player);
    }

    /**
     * Удаление регистрации игрока
     */
    public void unregisterPlayer(PlayerComponent player) {
        currentPlayer.compareAndSet(player, null);
    }

    /**
     * Получение текущего игрока
     */
    public PlayerComponent getCurrentPlayer() {
        return currentPlayer.get();
    }

    /**
     * Безопасное получение оружия текущего игрока
     */
    public Weapon getCurrentWeapon() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getCurrentWeapon() : null;
    }

    /**
     * Безопасная установка оружия текущему игроку
     */
    public void setCurrentWeapon(Weapon weapon) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.setCurrentWeapon(weapon);
        }
    }

    /**
     * Безопасное добавление золота текущему игроку
     */
    public void addGold(int gold) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.addGold(gold);
        }
    }

    /**
     * Безопасное получение золота текущего игрока
     */
    public int getGold() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getGold() : 0;
    }

    /**
     * Безопасное получение силы атаки текущего игрока
     */
    public double getAttackPower() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getAttackPower() : 1.0;
    }

    /**
     * Безопасная установка силы атаки текущему игроку
     */
    public void setIsAttackPowerBuffed(boolean buffed) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.setIsAttackPowerBuffed(buffed);
        }
    }

    /**
     * Безопасное получение пробивающей силы текущего игрока
     */
    public int getPiercePow() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getPiercePow() : 1;
    }

    /**
     * Безопасная установка пробивающей силы текущему игроку
     */
    public void setPiercePow(int pierce) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.setPiercePow(pierce);
        }
    }

    /**
     * Безопасная проверка, находится ли игрок в режиме концентрации
     */
    public boolean isChanneling() {
        PlayerComponent player = currentPlayer.get();
        return player != null && player.isChanneling();
    }

    /**
     * Безопасное выполнение атаки с концентрацией
     */
    public void channelAttack() {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.channelAttack();
        }
    }

    /**
     * Безопасное получение количества убитых монстров
     */
    public int getMonstersKilled() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getMonstersKilled() : 0;
    }

    /**
     * Безопасное добавление убитого монстра
     */
    public void addToMonstersKilled() {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.addToMonstersKilled();
        }
    }

    /**
     * Безопасное получение нанесенного урона
     */
    public double getDamageDealt() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getDamageDealt() : 0.0;
    }

    /**
     * Безопасное добавление нанесенного урона
     */
    public void addToDamageDealt(double damageDone) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.addToDamageDealt(damageDone);
        }
    }

    /**
     * Безопасное добавление опыта
     */
    public void addExperience(int exp) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.addExperience(exp);
        }
    }

    /**
     * Безопасное получение имени игрока
     */
    public String getPlayerName() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getPlayerName() : null;
    }

    /**
     * Безопасная установка имени игрока
     */
    public void setPlayerName(String playerName) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.setPlayerName(playerName);
        }
    }

    /**
     * Безопасное получение сложности игры
     */
    public String getGameDifficulty() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getGameDifficulty() : null;
    }

    /**
     * Безопасная установка сложности игры
     */
    public void setGameDifficulty(String gameDifficulty) {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.setGameDifficulty(gameDifficulty);
        }
    }

    /**
     * Безопасный сброс всех игровых данных
     */
    public void resetAllGameData() {
        PlayerComponent player = currentPlayer.get();
        if (player != null) {
            player.resetAllGameData();
        }
    }

    /**
     * Проверка, зарегистрирован ли игрок
     */
    public boolean isPlayerRegistered() {
        return currentPlayer.get() != null;
    }

    /**
     * Получение здоровья игрока
     */
    public int getHealth() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getHealthPoints() : 0;
    }

    /**
     * Получение максимального здоровья игрока
     */
    public int getMaxHealth() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getMaxHealthPoints() : 0;
    }

    /**
     * Получение скорости игрока
     */
    public double getSpeed() {
        PlayerComponent player = currentPlayer.get();
        return player != null ? player.getSpeed() : 0.0;
    }
}