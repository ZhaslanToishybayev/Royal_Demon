package uwu.openjfx.hud;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.Node;
import javafx.scene.text.*;
import javafx.util.Duration;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.progression.SimpleProgression;
import uwu.openjfx.visual.CleanDamageNumbers;
import uwu.openjfx.visual.VisualEffectsManager;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Улучшенный HUD с современным дизайном и анимациями
 */
public class EnhancedGameHUD {

    private final Entity player;
    private final PlayerComponent playerComponent;
    private final EnhancedLocalizationManager loc;
    private final VisualEffectsManager visualEffects;

    // UI элементы
    private VBox mainContainer;
    private HBox healthContainer;
    private HXPBar healthBar;
    private VBox statsContainer;
    private Text healthText;
    private Text levelText;
    private Text experienceText;
    private Text goldText;

    // Анимации
    private List<Text> floatingTexts = new ArrayList<>();

    public EnhancedGameHUD(Entity player) {
        this.player = player;
        this.playerComponent = player.getComponent(PlayerComponent.class);
        this.loc = EnhancedLocalizationManager.getInstance();
        this.visualEffects = VisualEffectsManager.getInstance();
    }

    /**
     * Инициализация улучшенного HUD
     */
    public void initUI() {
        clearExistingUI();
        createMainContainer();
        createHealthBar();
        createStatsPanel();
        setupAnimations();
        addToScene();
    }

    private void createMainContainer() {
        mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 10px; -fx-border-color: rgba(255,215,0,0.3); -fx-border-width: 1px;");
        mainContainer.setMaxWidth(300);
        mainContainer.setPrefWidth(250);
    }

    private void createHealthBar() {
        // Заголовок здоровья
        Text healthLabel = new Text(loc.getString("game.ui.health"));
        healthLabel.setFill(Color.CRIMSON);
        healthLabel.setFont(Font.font("Arial Bold", 16));

        // Контейнер для здоровья
        healthContainer = new HBox(10);
        healthContainer.setAlignment(Pos.CENTER_LEFT);

        // Полоска здоровья
        healthBar = new HXPBar();
        healthBar.setProgress(1.0);
        healthBar.setPrefWidth(200);
        healthBar.setPrefHeight(20);

        // Настройка стиля полоски здоровья
        healthBar.setStyle(
                "-fx-accent: linear-gradient(to right, #ff3838, #ff6b6b);" +
                "-fx-control-inner-background: rgba(50, 50, 50, 0.8);" +
                "-fx-background-radius: 5px;" +
                "-fx-background-insets: 0;"
        );

        // Текст здоровья
        healthText = new Text("100/100");
        healthText.setFill(Color.WHITE);
        healthText.setFont(Font.font("Arial", 14));

        HBox healthRow = new HBox(10);
        healthRow.setAlignment(Pos.CENTER_LEFT);
        healthRow.getChildren().addAll(healthBar, healthText);

        VBox healthSection = new VBox(5);
        healthSection.getChildren().addAll(healthLabel, healthRow);

        healthContainer.getChildren().add(healthSection);
    }

    private void createStatsPanel() {
        statsContainer = new VBox(8);
        statsContainer.setAlignment(Pos.CENTER_LEFT);
        statsContainer.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 5px; -fx-padding: 10px;");

        // Уровень
        levelText = new Text();
        levelText.setFill(Color.GOLD);
        levelText.setFont(Font.font("Arial Bold", 16));
        levelText.setText(loc.getString("game.ui.level") + ": 1");

        // Опыт
        experienceText = new Text();
        experienceText.setFill(Color.LIGHTBLUE);
        experienceText.setFont(Font.font("Arial", 14));
        experienceText.setText("XP: 0/100");

        // Золото
        goldText = new Text();
        goldText.setFill(Color.GOLD);
        goldText.setFont(Font.font("Arial", 14));
        goldText.setText(loc.getString("game.ui.gold") + ": 0");

        statsContainer.getChildren().addAll(levelText, experienceText, goldText);

        // Добавляем разделитель
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: rgba(255,215,0,0.3);");
        mainContainer.getChildren().addAll(healthContainer, separator, statsContainer);
    }

    private void setupAnimations() {
        // Обновление здоровья
        playerComponent.getHealthIntegerProperty().addListener((obs, oldVal, newVal) -> {
            updateHealthBar(newVal.intValue());
            animateHealthChange(oldVal.intValue(), newVal.intValue());
        });

        // Обновление уровня
        SimpleProgression progression = SimpleProgression.getInstance();
        // Добавим слушатели когда будут реализованы методы get/set

        // Динамическое обновление локализации
        loc.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateLocalizedText();
        });
    }

    private void updateHealthBar(int currentHealth) {
        double maxHealth = playerComponent.getMaxHealthPoints();
        double percentage = (double) currentHealth / maxHealth;
        healthBar.setProgress(percentage);
        healthText.setText(String.format("%d/%d", currentHealth, (int)maxHealth));

        // Анимация цвета в зависимости от здоровья
        if (percentage < 0.3) {
            healthBar.setStyle("-fx-accent: linear-gradient(to right, #ff3838, #cc0000);");
            animatePulse(healthBar);
        } else if (percentage < 0.6) {
            healthBar.setStyle("-fx-accent: linear-gradient(to right, #ffa500, #ff8c00);");
        }
    }

    private void animateHealthChange(int oldHealth, int newHealth) {
        int damage = oldHealth - newHealth;
        if (damage > 0) {
            // Показываем полученный урон
            Point2D damagePos = new Point2D(
                    mainContainer.getLayoutX() + 100,
                    mainContainer.getLayoutY() + 50
            );
            CleanDamageNumbers.showDamageNumber(damage, damagePos, false);
        } else if (damage < 0) {
            // Показываем исцеление
            int healAmount = Math.abs(damage);
            Point2D healPos = new Point2D(
                    mainContainer.getLayoutX() + 100,
                    mainContainer.getLayoutY() + 50
            );
            CleanDamageNumbers.showHealNumber(healAmount, healPos);
            visualEffects.createHealEffect(healPos);
        }
    }

    private void animatePulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), node);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    private void clearExistingUI() {
        // Удаляем старые UI элементы
        List<Node> existingUI = new ArrayList<>(FXGL.getGameScene().getUINodes());
        for (Node node : existingUI) {
            if (node.getUserData() instanceof String &&
                "HUD".equals(node.getUserData())) {
                FXGL.getGameScene().removeUINode(node);
            }
        }
    }

    private void addToScene() {
        mainContainer.setUserData("HUD");
        FXGL.getGameScene().addUINode(mainContainer);

        // Позиционирование
        mainContainer.setLayoutX(20);
        mainContainer.setLayoutY(20);
    }

    /**
     * Показывает всплывающее уведомление
     */
    public void showNotification(String message, NotificationType type) {
        Text notification = new Text(message);
        Color color;
        switch (type) {
            case SUCCESS:
                color = Color.LIGHTGREEN;
                break;
            case WARNING:
                color = Color.ORANGE;
                break;
            case ERROR:
                color = Color.RED;
                break;
            default:
                color = Color.WHITE;
        }

        notification.setFill(color);
        notification.setFont(Font.font("Arial Bold", 18));
        notification.setStroke(Color.BLACK);
        notification.setStrokeWidth(1);

        FXGL.getGameScene().addUINode(notification);
        notification.setLayoutX(getAppWidth() / 2 - 100);
        notification.setLayoutY(100);

        // Анимация появления и исчезновения
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
        sequence.setOnFinished(event -> FXGL.getGameScene().removeUINode(notification));
        sequence.play();
    }

    /**
     * Обновляет локализованный текст
     */
    private void updateLocalizedText() {
        // Обновляем текстовые элементы с новыми переводами
        if (mainContainer != null) {
            // Найти и обновить метку здоровья
            for (javafx.scene.Node node : mainContainer.getChildrenUnmodifiable()) {
                if (node instanceof VBox) {
                    VBox healthSection = (VBox) node;
                    if (healthSection.getChildren().size() > 0) {
                        javafx.scene.Node firstNode = healthSection.getChildren().get(0);
                        if (firstNode instanceof Text) {
                            ((Text) firstNode).setText(loc.getString("game.ui.health"));
                        }
                    }
                }
            }
        }
    }

    /**
     * Обновляет статистику
     */
    public void updateStats() {
        // Здесь будет логика обновления статистики
        updateLocalizedText();
    }

    /**
     * Типы уведомлений
     */
    public enum NotificationType {
        SUCCESS, WARNING, ERROR, INFO
    }
}