package uwu.openjfx.input;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import uwu.openjfx.components.PlayerComponent;

/**
 * Содержит все игровые input действия для рефакторинга MainApp
 */
public class GameInputActions {
    
    private static boolean actionsRegistered = false;
    
    public static void registerAllActions(Entity player) {
        if (actionsRegistered) {
            return;
        }
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        registerMovementActions(playerComponent);
        registerCombatActions(playerComponent);
        registerInventoryActions();
        registerUtilityActions();
        actionsRegistered = true;
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
}
