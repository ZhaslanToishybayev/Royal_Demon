package uwu.openjfx.hud;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uwu.openjfx.MainApp;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.combo.SimpleComboSystem;
import uwu.openjfx.progression.SimpleProgression;

import java.lang.ref.WeakReference;

/**
 * Современный HUD с ТОПОВЫМ дизайном
 */
public class ModernGameHUD {
    private static WeakReference<Entity> playerRef = new WeakReference<>(null);

    // Properties для привязки данных
    private static IntegerProperty goldProperty = new SimpleIntegerProperty();
    private static IntegerProperty healthPotProperty = new SimpleIntegerProperty();
    private static IntegerProperty ragePotProperty = new SimpleIntegerProperty();
    private static IntegerProperty levelProperty = new SimpleIntegerProperty();
    private static IntegerProperty maxExpProperty = new SimpleIntegerProperty();
    private static ObjectProperty<Image> weaponProperty = new SimpleObjectProperty<>();

    // UI элементы
    private static Pane hudContainer;
    private static VBox topCenterPanel;
    private static VBox leftTopPanel;
    private static HBox healthContainer;
    private static HBox expContainer;
    private static VBox goldContainer;
    private static VBox weaponContainer;
    private static Text comboText;
    private static ProgressBar healthBar;
    private static ProgressBar expBar;

    public static void init(Entity player) {
        if (player == null) return;

        playerRef = new WeakReference<>(player);
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);

        if (!MainApp.isIsTesting()) {
            createHUD(playerComponent);
        }
    }

    private static void createHUD(PlayerComponent playerComponent) {
        // Инициализируем значения
        goldProperty.set(PlayerComponent.getGold());
        healthPotProperty.set(0);
        ragePotProperty.set(0);

        // Основной контейнер HUD
        hudContainer = new Pane();
        hudContainer.setPickOnBounds(false);

        // ===== СОЗДАЕМ ЦЕНТРАЛЬНУЮ ВЕРХНЮЮ ПАНЕЛЬ (HP + EXP) =====
        createTopCenterPanel(playerComponent);

        // ===== СОЗДАЕМ ЛЕВУЮ ВЕРХНЮЮ ПАНЕЛЬ (золото, оружие, зелья) =====
        createLeftTopPanel(playerComponent);

        // Добавляем всё в контейнер
        hudContainer.getChildren().addAll(topCenterPanel, leftTopPanel, comboText);

        // Добавляем на сцену
        FXGL.getGameScene().addUINode(hudContainer);

        // Настройка анимаций
        setupAnimations(playerComponent);
    }

    private static void createTopCenterPanel(PlayerComponent playerComponent) {
        topCenterPanel = new VBox(8);
        topCenterPanel.setAlignment(Pos.CENTER);
        topCenterPanel.setLayoutX(FXGL.getAppWidth() / 2.0 - 250);
        topCenterPanel.setLayoutY(10);
        topCenterPanel.setPrefSize(500, 80);

        // ===== ПАНЕЛЬ ЗДОРОВЬЯ =====
        healthContainer = new HBox(10);
        healthContainer.setPadding(new Insets(8, 12, 8, 12));
        healthContainer.setStyle("-fx-background-color: rgba(220, 53, 69, 0.9); -fx-background-radius: 10px; " +
                                 "-fx-border-color: rgba(255,255,255,0.4); -fx-border-width: 2px;");
        healthContainer.setPrefSize(500, 35);

        // Иконка сердца
        Text heartIcon = new Text("❤️");
        heartIcon.setFont(Font.font(18));

        // Текст здоровья
        Text healthText = new Text("100");
        healthText.setFill(Color.WHITE);
        healthText.setFont(Font.font("Arial Bold", 16));
        healthText.setStyle("-fx-text-shadow: 2px 2px 4px rgba(0,0,0,0.8);");

        // Привязка только к текущему здоровью
        healthText.textProperty().bind(playerComponent.getHealthIntegerProperty().asString());

        // Полоса здоровья
        healthBar = new ProgressBar(false);
        healthBar.setWidth(300);
        healthBar.setHeight(12);
        healthBar.setMaxValue(playerComponent.getMaxHealthPoints());
        healthBar.currentValueProperty().bind(playerComponent.getHealthIntegerProperty());

        // Цвет здоровья при изменении
        playerComponent.getHealthIntegerProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue() / playerComponent.getMaxHealthPoints();
            if (percentage > 0.6) {
                healthBar.getInnerBar().setFill(Color.rgb(255, 255, 255));
            } else if (percentage > 0.3) {
                healthBar.getInnerBar().setFill(Color.rgb(255, 255, 0));
                animateWarning(healthBar);
            } else {
                healthBar.getInnerBar().setFill(Color.rgb(255, 0, 0));
                animateWarning(healthBar);
            }
        });

        healthContainer.getChildren().addAll(heartIcon, healthBar, healthText);

        // ===== ПАНЕЛЬ ОПЫТА =====
        expContainer = new HBox(10);
        expContainer.setPadding(new Insets(8, 12, 8, 12));
        expContainer.setStyle("-fx-background-color: rgba(30, 30, 60, 0.9); -fx-background-radius: 10px; " +
                              "-fx-border-color: rgba(173, 216, 230, 0.6); -fx-border-width: 2px;");
        expContainer.setPrefSize(500, 35);

        // Иконка опыта
        Text expIcon = new Text("✨");
        expIcon.setFont(Font.font(18));

        // Текст опыта с правильным форматом (слева)
        Text expText = new Text("0 из 100");
        expText.setFill(Color.rgb(173, 216, 230));
        expText.setFont(Font.font("Arial Bold", 14));
        expText.setStyle("-fx-text-shadow: 1px 1px 2px rgba(0,0,0,0.8);");

        // Уровень (справа)
        Text levelText = new Text("Уровень 1");
        levelText.setFill(Color.rgb(173, 216, 230));
        levelText.setFont(Font.font("Arial Bold", 16));
        levelText.setStyle("-fx-text-shadow: 1px 1px 2px rgba(0,0,0,0.8);");

        // Полоса опыта
        expBar = new ProgressBar(false);
        expBar.setWidth(200);
        expBar.setHeight(10);
        expBar.setMaxValue(100);

        // Привязка опыта с правильным отображением
        try {
            SimpleProgression progression = SimpleProgression.getInstance();
            levelProperty.bind(progression.levelProperty());

            // Привязываем уровень
            levelText.textProperty().bind(levelProperty.asString("Уровень "));

            // Привязываем полосу опыта
            expBar.currentValueProperty().bind(progression.experienceProperty());

            // Логируем изменения опыта
            progression.experienceProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("🎮 HUD: Опыт изменился! Старое: " + oldVal + ", Новое: " + newVal);
            });

            // Обновляем максимальный опыт при изменении уровня
            levelProperty.addListener((obs, oldVal, newVal) -> {
                int newLevel = newVal.intValue();
                int maxExp = newLevel * 100; // Level 1 = 100, Level 2 = 200, etc.
                System.out.println("🎮 HUD: Уровень изменился! " + oldVal + " → " + newLevel + ", Макс. опыт: " + maxExp);
                maxExpProperty.set(maxExp);
                expBar.setMaxValue(maxExp);
            });

            // Инициализируем максимальный опыт для ТЕКУЩЕГО уровня (нужно для следующего уровня)
            int currentLevel = progression.getLevel();
            int initialMaxExp = currentLevel * 100; // Level 1 = 100, Level 2 = 200, etc.
            System.out.println("🎮 HUD: Инициализация. Уровень: " + currentLevel + ", Макс. опыт: " + initialMaxExp);
            maxExpProperty.set(initialMaxExp);
            expBar.setMaxValue(initialMaxExp);

            // Привязываем текст опыта с динамическим максимумом
            expText.textProperty().bind(
                progression.experienceProperty().asString().concat("/").concat(
                    maxExpProperty.asString()
                )
            );

        } catch (Exception e) {
            // Если система прогрессии не готова
        }

        expContainer.getChildren().addAll(expIcon, expBar, expText, levelText);

        topCenterPanel.getChildren().addAll(healthContainer, expContainer);
    }

    private static void createLeftTopPanel(PlayerComponent playerComponent) {
        leftTopPanel = new VBox(8);
        leftTopPanel.setAlignment(Pos.TOP_LEFT);
        leftTopPanel.setLayoutX(15);
        leftTopPanel.setLayoutY(15);
        leftTopPanel.setPrefSize(140, 250);

        // ===== ЗОЛОТО =====
        goldContainer = new VBox(4);
        goldContainer.setPadding(new Insets(8, 10, 8, 10));
        goldContainer.setStyle("-fx-background-color: rgba(255,215,0,0.2); -fx-background-radius: 8px; " +
                               "-fx-border-color: rgba(255,215,0,0.6); -fx-border-width: 2px;");

        Text goldIcon = new Text("💰");
        goldIcon.setFont(Font.font(20));

        Text goldTitle = new Text("ЗОЛОТО");
        goldTitle.setFill(Color.rgb(255, 215, 0));
        goldTitle.setFont(Font.font("Arial Bold", 12));

        Text goldAmount = new Text("0");
        goldAmount.setFill(Color.rgb(255, 215, 0));
        goldAmount.setFont(Font.font("Arial Bold", 16));
        goldAmount.setStyle("-fx-text-shadow: 1px 1px 2px rgba(0,0,0,0.8);");
        goldAmount.textProperty().bind(goldProperty.asString());

        goldContainer.getChildren().addAll(goldIcon, goldTitle, goldAmount);

        // ===== ОРУЖИЕ =====
        weaponContainer = new VBox(5);
        weaponContainer.setPadding(new Insets(8, 10, 8, 10));
        // Убираем все цвета - делаем полностью прозрачным
        weaponContainer.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Text weaponTitle = new Text("ОРУЖИЕ");
        weaponTitle.setFill(Color.rgb(255, 255, 255)); // Белый цвет для лучшей видимости
        weaponTitle.setFont(Font.font("Arial Bold", 12));

        Texture weapon = new Texture(PlayerComponent.getCurrentWeapon().getWeaponSprite());
        weapon.setFitWidth(50);
        weapon.setFitHeight(50);
        weapon.setRotate(15);
        weapon.imageProperty().bind(weaponProperty);

        weaponContainer.getChildren().addAll(weaponTitle, weapon);

        leftTopPanel.getChildren().addAll(goldContainer, weaponContainer);

        // ===== КОМБО (Верхний центр) =====
        comboText = new Text();
        comboText.setFill(Color.rgb(255, 255, 255)); // Белый цвет вместо оранжевого
        comboText.setFont(Font.font("Arial Bold", 18));
        comboText.setStyle("-fx-text-shadow: 2px 2px 4px rgba(0,0,0,0.8);");
        comboText.setLayoutX(FXGL.getAppWidth() / 2.0 - 30);
        comboText.setLayoutY(10);
        comboText.setVisible(false); // Скрываем по умолчанию

        // Показываем только когда есть комбо
        comboText.textProperty().bind(
            SimpleComboSystem.getInstance().getComboCountProperty().asString("x")
        );

        // Слушатель для показа/скрытия в зависимости от значения комбо
        SimpleComboSystem.getInstance().getComboCountProperty().addListener((obs, oldVal, newVal) -> {
            comboText.setVisible(newVal.intValue() > 0);
        });
    }

    private static void setupAnimations(PlayerComponent playerComponent) {
        // Анимация пульсации при низком здоровье
        playerComponent.getHealthIntegerProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue() / playerComponent.getMaxHealthPoints();
            if (percentage < 0.3) {
                animatePulse(healthContainer);
            }
        });

        // Анимация появления комбо
        SimpleComboSystem.getInstance().getComboCountProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > oldVal.intValue()) {
                animateCombo(comboText);
            }
        });
    }

    private static void animateWarning(ProgressBar bar) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> bar.setOpacity(1.0)),
            new KeyFrame(Duration.millis(250), e -> bar.setOpacity(0.5)),
            new KeyFrame(Duration.millis(500), e -> bar.setOpacity(1.0)),
            new KeyFrame(Duration.millis(750), e -> bar.setOpacity(0.5)),
            new KeyFrame(Duration.millis(1000), e -> bar.setOpacity(1.0))
        );
        timeline.play();
    }

    private static void animatePulse(Pane pane) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), pane);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private static void animateCombo(Text text) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), text);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    // ===== МЕТОДЫ ДЛЯ РАБОТЫ СО СВОЙСТВАМИ =====

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty getGoldProperty() {
        return goldProperty;
    }

    public static void setGoldProperty(int goldProperty) {
        ModernGameHUD.goldProperty.set(goldProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty getHealthPotProperty() {
        return healthPotProperty;
    }

    public static void setHealthPotProperty(int healthPotProperty) {
        ModernGameHUD.healthPotProperty.set(healthPotProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static IntegerProperty ragePotProperty() {
        return ragePotProperty;
    }

    public static void setRagePotProperty(int ragePotProperty) {
        ModernGameHUD.ragePotProperty.set(ragePotProperty);
    }

    public static void setWeaponProperty(Image weaponProperty) {
        ModernGameHUD.weaponProperty.set(weaponProperty);
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to shared properties.")
    public static ObjectProperty<Image> getWeaponProperty() {
        return weaponProperty;
    }

    // ===== УНАСЛЕДОВАННЫЕ МЕТОДЫ ДЛЯ СОВМЕСТИМОСТИ =====

    public static void addHealthPot() {
        if (healthPotProperty.get() < 3) {
            int oldCount = healthPotProperty.get();
            healthPotProperty.set(oldCount + 1);
            System.out.println("💚 Добавлено зелье здоровья! Инвентарь: " + oldCount + " → " + healthPotProperty.get());
        } else {
            System.out.println("⚠️ Инвентарь зелий здоровья полон (максимум 3)");
        }
    }

    public static void useHealthPot() {
        Entity player = playerRef.get();
        if (player == null) return;

        PlayerComponent component = player.getComponent(PlayerComponent.class);

        if (healthPotProperty.get() <= 0) {
            System.out.println("❌ Нет зелий здоровья!");
            return;
        }

        if (component.getHealthPoints() >= component.getMaxHealthPoints()) {
            System.out.println("⚠️ Здоровье уже полное!");
            return;
        }

        int oldHealth = component.getHealthPoints();
        healthPotProperty.set(healthPotProperty.get() - 1);
        component.increaseHealth(5);
        int newHealth = component.getHealthPoints();

        System.out.println("💚 Использовано зелье здоровья! HP: " + oldHealth + " → " + newHealth +
                          " | В инвентаре: " + healthPotProperty.get());

        if (!MainApp.isIsTesting()) {
            FXGL.play("ui/pot.wav");
        }
    }

    public static void addRagePot() {
        if (ragePotProperty.get() < 3) {
            int oldCount = ragePotProperty.get();
            ragePotProperty.set(oldCount + 1);
            System.out.println("💜 Добавлено зелье ярости! Инвентарь: " + oldCount + " → " + ragePotProperty.get());
        } else {
            System.out.println("⚠️ Инвентарь зелий ярости полон (максимум 3)");
        }
    }

    public static void useRagePot() {
        if (ragePotProperty.get() <= 0) {
            System.out.println("❌ Нет зелий ярости!");
            return;
        }

        ragePotProperty.set(ragePotProperty.get() - 1);
        PlayerComponent.setIsAttackPowerBuffed(true);

        System.out.println("💜 Использовано зелье ярости! Бафф атаки активирован | В инвентаре: " + ragePotProperty.get());

        if (!MainApp.isIsTesting()) {
            FXGL.play("ui/pot2.wav");
        }
    }

    public static void initBossCutsceneAndUI() {
        // Дополнительный UI босса
    }
}
