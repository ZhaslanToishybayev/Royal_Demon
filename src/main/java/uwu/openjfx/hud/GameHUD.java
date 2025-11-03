package uwu.openjfx.hud;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.i18n.LocalizationManager;
import uwu.openjfx.progression.SimpleProgression;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class GameHUD {

    private Entity player;
    private PlayerComponent playerComponent;
    private LocalizationManager loc;

    private HBox healthBar;
    private Text goldText;
    private Text levelText;

    public GameHUD(Entity player) {
        this.player = player;
        this.playerComponent = player.getComponent(PlayerComponent.class);
        this.loc = LocalizationManager.getInstance();
    }

    public void initUI() {
        clearExistingUI();
        createHealthUI();
        createGoldUI();
        createLevelUI();
    }

    private void createHealthUI() {
        healthBar = new HBox(5);
        healthBar.setTranslateX(25);
        healthBar.setTranslateY(25);

        List<Texture> hearts = new ArrayList<>();
        // HP игрока: 20 (1 сердце = 5 HP) = 4 сердца
        for (int i = 0; i < playerComponent.getMaxHealthPoints() / 5; i++) {
            Texture heart = FXGL.texture("items/ui_heart_full_32x32.png");
            hearts.add(heart);
            healthBar.getChildren().add(heart);
        }

        playerComponent.getHealthIntegerProperty().addListener((obs, old, newHealth) -> {
            // Пересчитываем количество полных сердец
            int fullHearts = (int) Math.ceil(newHealth.intValue() / 5.0);

            // Обновляем отображение сердец
            for (int i = 0; i < hearts.size(); i++) {
                if (i < fullHearts) {
                    // Показываем яркое сердце
                    hearts.get(i).setOpacity(1.0);
                    hearts.get(i).setVisible(true);
                } else {
                    // Убираем сердце полностью
                    hearts.get(i).setVisible(false);
                }
            }
        });

        getGameScene().addUINode(healthBar);
    }

    private void createGoldUI() {
        goldText = getUIFactoryService().newText(loc.getString("game.ui.gold") + ": " + playerComponent.getGold(), 30);
        goldText.setTranslateX(getAppWidth() - 150);
        goldText.setTranslateY(25);
        goldText.setStroke(Color.GOLD);
        goldText.textProperty().bind(FXGL.getip("coin").asString(loc.getString("game.ui.gold") + ": %d"));
        getGameScene().addUINode(goldText);
    }

    private void createLevelUI() {
        levelText = getUIFactoryService().newText(loc.getString("game.ui.level") + ": " + SimpleProgression.getInstance().getLevel(), 30);
        levelText.setTranslateX(25);
        levelText.setTranslateY(60);
        levelText.setStroke(Color.CYAN);
        levelText.textProperty().bind(SimpleProgression.getInstance().levelProperty().asString(loc.getString("game.ui.level") + ": %d"));
        getGameScene().addUINode(levelText);
    }

    private void clearExistingUI() {
        // Remove existing UI elements by ID if they were added with IDs
        getGameScene().getUINodes().removeIf(node -> {
            String id = node.getId();
            return id != null && (id.equals("healthPrefix") || id.equals("healthValue") ||
                                 id.equals("goldPrefix") || id.equals("goldValue") ||
                                 id.equals("levelPrefix") || id.equals("levelValue"));
        });
    }
}