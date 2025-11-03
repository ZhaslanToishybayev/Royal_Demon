package uwu.openjfx;

import com.almasb.fxgl.cutscene.Cutscene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uwu.openjfx.components.BossComponent;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.combo.SimpleComboSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.ref.WeakReference;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.loopBGM;

public class UI {
    private static WeakReference<Entity> playerRef = new WeakReference<>(null);

    private static IntegerProperty goldProperty = new SimpleIntegerProperty();
    private static IntegerProperty healthPotProperty = new SimpleIntegerProperty();
    private static IntegerProperty ragePotProperty = new SimpleIntegerProperty();
    private static ObjectProperty<Image> weaponProperty = new SimpleObjectProperty<>();

    public static void init(Entity player) {
        UI.playerRef = new WeakReference<>(player);

        if (!MainApp.isIsTesting()) {
            // HP:
            ProgressBar healthBar = new ProgressBar(false);
            healthBar.setWidth(200);
            healthBar.setHeight(20);
            healthBar.setTranslateX(25);
            healthBar.setTranslateY(25);
            healthBar.setMaxValue(player.getComponent(PlayerComponent.class).getMaxHealthPoints());
            healthBar.currentValueProperty().bind(player.getComponent(PlayerComponent.class).getHealthIntegerProperty());
            healthBar.getBackgroundBar().setFill(Color.GRAY);
            healthBar.getInnerBar().setFill(Color.RED);

            // GOLD:
            Text textGoldPrefix = FXGL.getUIFactoryService().newText("GOLD:", 50);
            textGoldPrefix.setTranslateX(FXGL.getAppWidth() - 225);
            textGoldPrefix.setTranslateY(50);
            textGoldPrefix.setStroke(Color.GOLD);

            // Gold amount
            Text textGold = FXGL.getUIFactoryService().newText("", 50);
            textGold.setTranslateX(FXGL.getAppWidth() - 100);
            textGold.setTranslateY(50);
            textGold.setStroke(Color.WHITE);

            textGold.textProperty().bind(goldProperty.asString());

            // Combo Count:
            Text textComboPrefix = FXGL.getUIFactoryService().newText("COMBO:", 50);
            textComboPrefix.setTranslateX(FXGL.getAppWidth() / 2 - 100);
            textComboPrefix.setTranslateY(50);
            textComboPrefix.setStroke(Color.ORANGE);

            Text textCombo = FXGL.getUIFactoryService().newText("", 50);
            textCombo.setTranslateX(FXGL.getAppWidth() / 2 + 50);
            textCombo.setTranslateY(50);
            textCombo.setStroke(Color.WHITE);

            textCombo.textProperty().bind(
                    SimpleComboSystem.getInstance().getComboCountProperty().asString());

            // weapon
            Texture weaponBox = FXGL.getAssetLoader().loadTexture("ui/weapon_box_ui.png");
            weaponBox.setTranslateX(25);
            weaponBox.setTranslateY(FXGL.getAppHeight() - weaponBox.getHeight() - 25);

            Texture weapon = new Texture(PlayerComponent.getCurrentWeapon().getWeaponSprite());
            weapon.setRotate(45.0);
            weapon.setTranslateX(weaponBox.getTranslateX()
                    + (weaponBox.getWidth() / 2)
                    - weapon.getWidth() / 2);
            weapon.setTranslateY(weaponBox.getTranslateY()
                    + (weaponBox.getWidth() / 2)
                    - weapon.getHeight() / 2);

            weapon.imageProperty().bind(weaponProperty);


            // Health pots
            Texture healthPotBox = FXGL.getAssetLoader().loadTexture("ui/pot_box_ui.png");
            healthPotBox.setTranslateX(weaponBox.getTranslateX() + weaponBox.getWidth() + 10);
            healthPotBox.setTranslateY(FXGL.getAppHeight() - healthPotBox.getHeight() - 25);

            Texture healthPot = FXGL.getAssetLoader().loadTexture("ui/health_potion_ui.png");
            healthPot.setTranslateX(healthPotBox.getTranslateX()
                    + healthPotBox.getWidth() / 4.0);
            healthPot.setTranslateY(healthPotBox.getTranslateY()
                    + healthPotBox.getHeight() / 4.0 - 2);

            Text textHealthPot = FXGL.getUIFactoryService().newText("", 25);
            textHealthPot.setTranslateX(
                    healthPotBox.getTranslateX() + healthPotBox.getWidth() * 3 / 5.0);
            textHealthPot.setTranslateY(
                    healthPotBox.getTranslateY() + healthPotBox.getHeight() * 3 / 4.0);
            textHealthPot.setStroke(Color.RED);

            textHealthPot.textProperty().bind(healthPotProperty.asString());

            // Rage pots
            Texture ragePotBox = FXGL.getAssetLoader().loadTexture("ui/pot_box_ui.png");
            ragePotBox.setTranslateX(healthPotBox.getTranslateX() + healthPotBox.getWidth() + 10);
            ragePotBox.setTranslateY(healthPotBox.getTranslateY());

            Texture ragePot = FXGL.getAssetLoader().loadTexture("ui/rage_potion_ui.png");
            ragePot.setTranslateX(ragePotBox.getTranslateX() + ragePotBox.getWidth() / 4.0);
            ragePot.setTranslateY(ragePotBox.getTranslateY() + ragePotBox.getHeight() / 4.0 - 2);

            Text textRagePot = FXGL.getUIFactoryService().newText("", 25);
            textRagePot.setTranslateX(ragePotBox.getTranslateX()
                    + ragePotBox.getWidth() * 3 / 5.0);
            textRagePot.setTranslateY(ragePotBox.getTranslateY()
                    + ragePotBox.getHeight() * 3 / 4.0);
            textRagePot.setStroke(Color.ORANGE);  // Убрал PURPLE - фиолетовые артефакты при стрельбе из посоха

            textRagePot.textProperty().bind(ragePotProperty.asString());

            FXGL.getGameScene().addUINodes(
                    healthBar,
                    textGoldPrefix,
                    textGold,
                    textComboPrefix,
                    textCombo,
                    weaponBox,
                    weapon,
                    healthPotBox,
                    healthPot,
                    textHealthPot,
                    ragePotBox,
                    ragePot,
                    textRagePot
            );
        }
    }

    public static void initBossCutsceneAndUI() {
        ArrayList<String> bossFight = new ArrayList<>(Arrays.asList(
                "1.name = " + PlayerComponent.getPlayerName(),
                "2.name = Ultra Extremely Bad Guy Who Kidnapped the Princess Bc He is The Worst",
                "1.image = ui_yoshi.png",
                "2.image = ui_peach.png",
                "1: Where is the princess?! >:-(",
                "2: You'll have to defeat me to find out, you fool!",
                "1: I'll save her!"
        ));
        FXGL.getSceneService().getTimer().runOnceAfter(() -> FXGL.getCutsceneService()
                .startCutscene(
                        new Cutscene(bossFight)), Duration.millis(750));
        getAudioPlayer().stopMusic(FXGL.getAssetLoader().loadMusic("evil4.mp3"));
        loopBGM("boss/boss_battle_ 2.mp3");

        ProgressBar bossHealth = new ProgressBar();
        bossHealth.setBackgroundFill(Color.RED);
        bossHealth.setHeight(25);
        bossHealth.setMinValue(0);
        bossHealth.setMaxValue(100);
        bossHealth.setTranslateX(FXGL.getAppWidth() / 2.0
                - bossHealth.getBackgroundBar().getWidth() / 2);
        bossHealth.setTranslateY(12.5);
        bossHealth.currentValueProperty().bind(BossComponent.getBossHealthProperty());
        FXGL.getGameScene().addUINodes(bossHealth);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty getGoldProperty() {
        return goldProperty;
    }

    public static void setGoldProperty(int goldProperty) {
        UI.goldProperty.set(goldProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty getHealthPotProperty() {
        return healthPotProperty;
    }

    public static void setHealthPotProperty(int healthPotProperty) {
        UI.healthPotProperty.set(healthPotProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty ragePotProperty() {
        return ragePotProperty;
    }

    public static void setRagePotProperty(int ragePotProperty) {
        UI.ragePotProperty.set(ragePotProperty);
    }

    public static void setWeaponProperty(Image weaponProperty) {
        UI.weaponProperty.set(weaponProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static ObjectProperty<Image> getWeaponProperty() {
        return weaponProperty;
    }

    public static void addHealthPot() {
        if (healthPotProperty.get() < 3) {
            healthPotProperty.set(healthPotProperty.get() + 1);
        }
    }

    public static void useHealthPot() {
        Entity player = playerRef.get();
        if (player == null) {
            return;
        }
        PlayerComponent component = player.getComponent(PlayerComponent.class);
        if (component.getHealthPoints() < component.getMaxHealthPoints()) {
            healthPotProperty.set(healthPotProperty.get() - 1);
            component.increaseHealth(5); // 5 HP за зелье (1 сердце)
            if (!MainApp.isIsTesting()) {
                FXGL.play("ui/pot.wav");
            }
        }
    }

    public static void addRagePot() {
        if (ragePotProperty.get() < 3) {
            ragePotProperty.set(ragePotProperty.get() + 1);
        }
    }

    public static void useRagePot() {
        if (ragePotProperty.get() > 0) {
            ragePotProperty.set(ragePotProperty.get() - 1);
            PlayerComponent.setIsAttackPowerBuffed(true);
            if (!MainApp.isIsTesting()) {
                FXGL.play("ui/pot2.wav");
            }
        }
    }
}