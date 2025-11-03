package uwu.openjfx.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import uwu.openjfx.MainApp;
import uwu.openjfx.UI;
import uwu.openjfx.behaviors.GameOverWhenDie;
import uwu.openjfx.weapons.Weapon;
import uwu.openjfx.integration.IntegrationHelpers;
import uwu.openjfx.integration.GameIntegration;
import uwu.openjfx.utils.GameLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/*
    This class is responsible for the following:
    - Animation of the player: movement, idleness, attacking
    - Being the caller for attacking, which in turn spawns the necessary weapon of type Weapon
 */
public class PlayerComponent extends CreatureComponent {

    private PhysicsComponent physics;

    private AnimatedTexture texture; // current player animation

    private AnimationChannel animIdle; // idle player anim
    private AnimationChannel animWalk; // walk player anim

    private static Weapon currentWeapon; // Player's current weapon
    private static final List<Weapon> weaponInventoryList = new ArrayList<>();
    private static double attackPower = 1; // Player attack power based on power buff or normal
    private static int piercePow = 1; // Player pierces through blocks if 0
    private static boolean isAttackPowerBuffed = false; // player has drank a rage potion recently
    private static int attackPowerBuffDuration = 300; // how long attack power buff lasts / 60 sec
    private static boolean isChanneling = false; // if player is channeling (fire breath)

    private double currMouseX; // mouse input for x
    private double currMouseY; // mouse input for y

    private double speed = 180; // Оптимальная скорость для комфортной игры
    private static final double VELOCITY_DECREMENTER = 5; // rate at which player decelerates
    private boolean isPressingMovementKeys = false; // Player is moving with WASD / Arrow keys
    private boolean prepAttack = false; // Player has initiated attack charge/channel
    private boolean startAttack = false; // Player does the actual attack
    private boolean ultimateActivated = false; // Player is using ultimate
    private boolean ultimateCD = false; // how long until Player can activate Ultimate again

    // Todo: char state class?
    private static String playerName;
    private static String gameDifficulty;
    private static int monstersKilled;
    private static double damageDealt;

    // Todo: inventory-esque things, move?
    private static int gold;
    private static int healthPotAmount;
    private static int ragePotAmount;

    public PlayerComponent(int maxHealthPoints) {
        super(maxHealthPoints, maxHealthPoints, new GameOverWhenDie());

        if (!MainApp.isIsTesting()) {
            animIdle = new AnimationChannel(FXGL.image("creatures/lizard_m_40x55.png"), 9,
                    40, 55, Duration.seconds(0.5), 0, 3);
            animWalk = new AnimationChannel(FXGL.image("creatures/lizard_m_40x55.png"), 9,
                    40, 55, Duration.seconds(0.5), 4, 7);

            texture = new AnimatedTexture(animIdle);
            texture.loop();
        }

    }


    @Override
    public void onAdded() {
        if (!MainApp.isIsTesting() && getEntity() != null) {
            getEntity().getTransformComponent().setScaleOrigin(new Point2D(20, 25));
            getEntity().getViewComponent().addChild(texture);
            getEntity().addComponent(new StatusEffectComponent());
            getEntity().addComponent(new ComboManager());
        }
    }

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "PlayerComponent represents a single-player state shared via static fields.")
    public void onUpdate(double tpf) {
        // region Movement
        if (!isPressingMovementKeys) {
            normalizeVelocityX();
            normalizeVelocityY();
        }
        if (!prepAttack) {
            // if Player has initiated an attack, then do not perform walk/idle animations
            if (physics.isMoving()) {
                if (texture.getAnimationChannel() != animWalk) {
                    texture.loopAnimationChannel(animWalk);
                    // Убрал фиолетовые анимации
                }
            } else {
                if (texture.getAnimationChannel() != animIdle) {
                    texture.loopAnimationChannel(animIdle);
                    // Убрал фиолетовые анимации
                }
            }
        }
        //endregion

        //region Player performs attack
        if (startAttack && getEntity() != null) { // Player performs the actual attack
            currentWeapon.attack(getEntity(), currMouseX, currMouseY);
            prepAttack = false;
            startAttack = false;
            if (!isChanneling) {
                ultimateActivated = false;
            }
        }
        if (currentWeapon.getClass().getName().contains("MagicStaff")) {
            if (isChanneling()) {
                speed = 120; // Скорость при использовании магии (снижена, но играбельно)
            } else {
                speed = 180; // Нормальная скорость для MagicStaff
                ultimateActivated = false;
            }
        }

        // Убрал бонус скорости от комбо для лучшего баланса

        if (isAttackPowerBuffed) {
            attackPowerBuffDuration--;
            if (attackPowerBuffDuration <= 0) {
                attackPowerBuffDuration = 300;
                isAttackPowerBuffed = false;
            }
        }
        if (isInvulnerable()) {
            texture.setOpacity(0.5);
        } else {
            texture.setOpacity(1);
        }
        // endregion
    }

    // region Player Movement
    // As long as player has not initiated an attack, can move
    private void normalizeVelocityX() {
        if (physics.getVelocityX() != 0) {
            if (physics.getVelocityX() > 0) {
                physics.setVelocityX(physics.getVelocityX() - VELOCITY_DECREMENTER);
                if (physics.getVelocityX() < 0) {
                    physics.setVelocityX(0);
                }
            } else if (physics.getVelocityX() < 0) {
                physics.setVelocityX(physics.getVelocityX() + VELOCITY_DECREMENTER);
                if (physics.getVelocityX() > 0) {
                    physics.setVelocityX(0);
                }
            }
        }
    }

    private void normalizeVelocityY() {
        if (physics.getVelocityY() != 0) {
            if (physics.getVelocityY() > 0) {
                physics.setVelocityY(physics.getVelocityY() - VELOCITY_DECREMENTER);
                if (physics.getVelocityY() < 0) {
                    physics.setVelocityY(0);
                }
            } else if (physics.getVelocityY() < 0) {
                physics.setVelocityY(physics.getVelocityY() + VELOCITY_DECREMENTER);
                if (physics.getVelocityY() > 0) {
                    physics.setVelocityY(0);
                }
            }
        }
    }

    public void left() {
        if (!prepAttack) {
            if (!isChanneling()) {
                Entity entity = getEntity();
                if (entity != null) {
                    entity.setScaleX(-1);
                }
            }
            physics.setVelocityX(-speed);
        }
    }

    public void right() {
        if (!prepAttack) {
            if (!isChanneling()) {
                Entity entity = getEntity();
                if (entity != null) {
                    entity.setScaleX(1);
                }
            }
            physics.setVelocityX(speed);
        }
    }

    public void up() {
        if (!prepAttack) {
            physics.setVelocityY(-speed);
        }
    }

    public void down() {
        if (!prepAttack) {
            physics.setVelocityY(speed);
        }
    }

    public void stop() {
        physics.setVelocityX(0);
        physics.setVelocityY(0);
    }

    public void faceRight() {
        if (getEntity() != null) {
            getEntity().setScaleX(1);
        }
    }

    public boolean isPressingMovementKeys() {
        return isPressingMovementKeys;
    }

    public void setPressingMovementKeys(boolean moving) {
        isPressingMovementKeys = moving;
    }
    // endregion

    // region Player Attack functions
    public void autoAttack(boolean ultimateActivated) {
        if (currentWeapon == null) {
            GameLogger.warn("Cannot attack - no weapon equipped");
            return;
        }

        this.ultimateActivated = ultimateActivated;

        // Интеграция с системой комбо
        String attackType = ultimateActivated ? "heavy" : "light";
        IntegrationHelpers.onPlayerAttack(attackType);

        if (ultimateActivated) {
            ultimateCD = true;
            Timeline ultimateCooldownTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(currentWeapon.getUltimateCD() * 1000), e -> ultimateCD = false)
            );
            ultimateCooldownTimeline.play();
        }
        if (getEntity() != null) {
            if (currMouseX > getEntity().getX() + 20) { // turn player in direction of mouse
                getEntity().setScaleX(1);
            } else {
                getEntity().setScaleX(-1);
            }
        }
        if (!MainApp.isIsTesting() && texture != null && animWalk != null) {
            texture.playAnimationChannel(animWalk); // play attack animation
        }
        prepAttack = true; // Player has initiated attack
        stop(); // stop moving
        // Player performs the actual attack after duration amount of milliseconds
        try {
            Timeline attackTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(currentWeapon.getDuration(ultimateActivated)), e -> startAttack = true)
            );
            attackTimeline.play();
            currentWeapon.getDuration(ultimateActivated);
            if (getEntity() != null) {
                currentWeapon.prepAttack(getEntity());
            }
        } catch (Exception e) {
            GameLogger.error("Error during attack execution: " + e.getMessage(), e);
            prepAttack = false; // Reset attack state on error
        }
    }

    public static void channelAttack() {
        isChanneling = true;
        Timeline channelTimeline = new Timeline(
            new KeyFrame(javafx.util.Duration.millis(2000), e -> isChanneling = false)
        );
        channelTimeline.play();
    }

    public static boolean isChanneling() {
        return isChanneling;
    }

    public boolean getUltimateCD() {
        return ultimateCD;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isAttacking() {
        // used in MainApp for LMB/RMB input, confirmation of whether or not Player is attacking
        return prepAttack;
    }

    public static void setIsAttackPowerBuffed(boolean buffed) {
        isAttackPowerBuffed = buffed;
        piercePow = 0;
        attackPower = 1.5;
    }

    public static double getAttackPower() {
        return attackPower;
    }

    public static void setPiercePow(int pierce) {
        piercePow = pierce;
    }

    public static int getPiercePow() {
        return piercePow;
    }
    // endregion

    // region Player Weapon
    public static void setCurrentWeapon(Weapon weapon) {
        currentWeapon = weapon;
        if (weapon != null) {
            UI.setWeaponProperty(currentWeapon.getWeaponSprite());
        } else {
            UI.setWeaponProperty(FXGL.texture("ui/transparent.png").getImage());
        }
    }

    public static Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public static List<Weapon> getWeaponInventoryList() {
        return Collections.unmodifiableList(weaponInventoryList);
    }

    public static void addWeaponToInventory(Weapon weapon) {
        if (weapon == null) {
            return;
        }
        for (Weapon playerWeapon : weaponInventoryList) {
            if (playerWeapon.getClass().equals(weapon.getClass())) {
                return;
            }
        }
        weaponInventoryList.add(weapon);
    }

    // endregion

    // region Gold
    public static void addGold(int gold) {
        // Используем FXGL.getip("coin") для синхронизации с UI
        int currentGold = 0;
        try {
            currentGold = FXGL.geti("coin");
        } catch (Exception e) {
            // Если не удалось получить, используем статическое поле
            currentGold = PlayerComponent.gold;
        }

        int newGold = currentGold + gold;
        PlayerComponent.gold = newGold;

        // Обновляем FXGL переменную (синхронизация с UI)
        FXGL.set("coin", newGold);

        // Проверяем достижения при получении золота
        try {
            uwu.openjfx.achievements.SimpleAchievements.getInstance().checkAchievements();
        } catch (Exception e) {
            // Игнорируем ошибки, если система достижений не инициализирована
        }
    }

    public static void setGold(int gold) {
        PlayerComponent.gold = gold;
        FXGL.set("coin", gold); // Синхронизация с FXGL и UI
    }

    public static int getGold() {
        return gold;
    }
    // endregion

    // region Player Name & Game Misc.
    public static String getPlayerName() {
        return playerName;
    }

    public static void setPlayerName(String playerName) {
        PlayerComponent.playerName = playerName;
    }

    public static String getGameDifficulty() {
        return gameDifficulty;
    }

    public static void setGameDifficulty(String gameDifficulty) {
        PlayerComponent.gameDifficulty = gameDifficulty;
    }

    public void setMousePosition(double mouseXPos, double mouseYPos) {
        currMouseX = mouseXPos;
        currMouseY = mouseYPos;
    }

    public static int getMonstersKilled() {
        return monstersKilled;
    }

    public static void addToMonstersKilled() {
        monstersKilled++;
        
        // Проверяем достижения при убийстве монстров
        try {
            uwu.openjfx.achievements.SimpleAchievements.getInstance().checkAchievements();
        } catch (Exception e) {
            // Игнорируем ошибки, если система достижений не инициализирована
        }
    }

    public static double getDamageDealt() {
        return damageDealt;
    }

    public static void addToDamageDealt(double damageDone) {
        damageDealt += damageDone;
    }
    
    /**
     * Интеграция с системой прогрессии - добавить опыт игроку
     */
    public static void addExperience(int exp) {
        try {
            uwu.openjfx.progression.SimpleProgression.getInstance().addExperience(exp);
        } catch (Exception e) {
            // Игнорируем ошибки, если система прогрессии не инициализирована
        }
    }
    
    /**
     * Интеграция с системой комнат - отметить исследованную комнату
     */
    public static void onRoomExplored() {
        try {
            IntegrationHelpers.onRoomExplored();
        } catch (Exception e) {
            // Игнорируем ошибки, если система интеграции не инициализирована
        }
    }
    
    /**
     * Интеграция с сундуками - отметить открытый сундук
     */
    public static void onChestOpened() {
        try {
            IntegrationHelpers.onChestOpened();
        } catch (Exception e) {
            // Игнорируем ошибки, если система интеграции не инициализирована
        }
    }
    
    /**
     * Безопасный сброс всех игровых данных
     */
    public static void resetAllGameData() {
        playerName = null;
        gameDifficulty = null;
        gold = 0;
        healthPotAmount = 0;
        ragePotAmount = 0;
        monstersKilled = 0;
        damageDealt = 0.0;
        attackPower = 1;
        piercePow = 1;
        isAttackPowerBuffed = false;
        isChanneling = false;
        currentWeapon = null;
        weaponInventoryList.clear();
    }
    
    // endregion
}
