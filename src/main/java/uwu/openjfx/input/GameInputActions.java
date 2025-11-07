package uwu.openjfx.input;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.utils.GameLogger;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * Содержит все игровые input действия для рефакторинга MainApp
 */
public class GameInputActions {
    
    private static boolean actionsRegistered = false;
    
    public static void registerAllActions(Entity player) {
        synchronized (GameInputActions.class) {
            if (actionsRegistered) {
                GameLogger.gameplay("Input actions already registered, skipping");
                return;
            }

            // Принудительно очищаем FXGL input actions перед регистрацией
            // Это необходимо для корректного перезапуска игры
            clearFXGLActions();

            PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
            registerMovementActions(playerComponent);
            registerCombatActions(playerComponent);
            registerInventoryActions();
            registerUtilityActions();
            actionsRegistered = true;
            GameLogger.gameplay("All input actions registered successfully");
        }
    }

    /**
     * Принудительно очищает все input actions в FXGL через reflection
     */
    private static void clearFXGLActions() {
        try {
            Object input = FXGL.getInput();
            Class<?> inputClass = input.getClass();

            // Ищем поле с actions (может называться по-разному)
            Field actionsField = null;
            for (Field field : inputClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(input);
                if (value instanceof Map) {
                    actionsField = field;
                    break;
                }
            }

            if (actionsField != null) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> actions = (Map<Object, Object>) actionsField.get(input);
                actions.clear();
                GameLogger.gameplay("FXGL input actions cleared via reflection");
            }
        } catch (Exception e) {
            GameLogger.warn("Failed to clear FXGL input actions via reflection: " + e.getMessage());
        }
    }
    
    private static void registerMovementActions(PlayerComponent playerComponent) {
        // Движение влево
        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerComponent.left();
                playerComponent.setPressingMovementKeys(true);
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
                playerComponent.setPressingMovementKeys(false);
            }
        }, KeyCode.A);

        // Движение вправо
        FXGL.getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerComponent.right();
                playerComponent.setPressingMovementKeys(true);
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
                playerComponent.setPressingMovementKeys(false);
            }
        }, KeyCode.D);

        // Движение вверх
        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                playerComponent.up();
                playerComponent.setPressingMovementKeys(true);
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
                playerComponent.setPressingMovementKeys(false);
            }
        }, KeyCode.W);

        // Движение вниз
        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                playerComponent.down();
                playerComponent.setPressingMovementKeys(true);
            }

            @Override
            protected void onActionEnd() {
                playerComponent.stop();
                playerComponent.setPressingMovementKeys(false);
            }
        }, KeyCode.S);
    }
    
    private static void registerCombatActions(PlayerComponent playerComponent) {
        // Обычная атака (ЛКМ)
        FXGL.getInput().addAction(new UserAction("LMB") {
            @Override
            protected void onActionBegin() {
                if (!playerComponent.isAttacking() && !PlayerComponent.isChanneling()) {
                    playerComponent.setMousePosition(
                            FXGL.getInput().getMousePositionWorld().getX(),
                            FXGL.getInput().getMousePositionWorld().getY());
                    playerComponent.autoAttack(false);
                }
            }
        }, MouseButton.PRIMARY);
        
        // Ультимативная атака (Пробел)
        FXGL.getInput().addAction(new UserAction("SPACE") {
            @Override
            protected void onActionBegin() {
                if (!playerComponent.isAttacking() && !playerComponent.getUltimateCD()) {
                    playerComponent.setMousePosition(
                            FXGL.getInput().getMousePositionWorld().getX(),
                            FXGL.getInput().getMousePositionWorld().getY());
                    playerComponent.autoAttack(true);
                }
            }
        }, KeyCode.SPACE);
    }
    
    private static void registerInventoryActions() {
        // Подобрать предмет
        FXGL.getInput().addAction(new PickItem("PickItem"), KeyCode.E);
        
        // Использовать предмет
        FXGL.getInput().addAction(new UseItem("UseItem"), KeyCode.F);
        
        // Использовать зелье здоровья
        FXGL.getInput().addAction(new UseHealthPot("UseHealthPot"), KeyCode.DIGIT1);
        
        // Использовать зелье ярости
        FXGL.getInput().addAction(new UseRagePot("UseRagePot"), KeyCode.DIGIT2);
    }
    
    private static void registerUtilityActions() {
        // Показать карту
        FXGL.getInput().addAction(new ShowMapAction("showMap"), KeyCode.M);

        // Показать инвентарь
        FXGL.getInput().addAction(new ShowInventoryAction("showInventory"), KeyCode.I);

        // Телепорт в showcase комнату
        FXGL.getInput().addAction(new TeleportToShowcaseRoom("teleportToShowcaseRoom"), KeyCode.P);
    }

    /**
     * Сбросить флаг регистрации действий для корректного перезапуска игры
     */
    public static void resetActionsRegistration() {
        synchronized (GameInputActions.class) {
            actionsRegistered = false;
            GameLogger.gameplay("Input actions registration flag reset");
        }
    }
}
