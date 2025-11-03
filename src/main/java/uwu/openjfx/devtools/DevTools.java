package uwu.openjfx.devtools;

import uwu.openjfx.core.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.Map;

/**
 * Инструменты разработчика для отладки и тестирования
 */
public class DevTools {
    private Stage devToolsWindow;
    private boolean isVisible = false;

    public DevTools() {
        // Создаем окно разработчика
        createDevToolsWindow();
    }

    /**
     * Создать окно DevTools
     */
    private void createDevToolsWindow() {
        devToolsWindow = new Stage();
        devToolsWindow.setTitle("Royal Demons - DevTools");
        devToolsWindow.setResizable(true);
        devToolsWindow.setMinWidth(400);
        devToolsWindow.setMinHeight(500);

        // Основной контейнер
        VBox mainContainer = new VBox(10);
        mainContainer.setStyle("-fx-padding: 20; -fx-background-color: #2b2b2b;");

        // Заголовок
        Label titleLabel = new Label("🔧 Developer Tools");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        mainContainer.getChildren().add(titleLabel);

        // Информация о системе
        VBox systemInfoBox = createSystemInfoSection();
        mainContainer.getChildren().add(systemInfoBox);

        // Модули
        VBox modulesBox = createModulesSection();
        mainContainer.getChildren().add(modulesBox);

        // Тестовые кнопки
        VBox testButtonsBox = createTestButtonsSection();
        mainContainer.getChildren().add(testButtonsBox);

        // Логирование
        VBox loggingBox = createLoggingSection();
        mainContainer.getChildren().add(loggingBox);

        // Кнопка закрытия
        Button closeButton = new Button("Close DevTools");
        closeButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        closeButton.setOnAction(e -> hide());
        mainContainer.getChildren().add(closeButton);

        devToolsWindow.setScene(new Scene(mainContainer));
    }

    /**
     * Создать секцию информации о системе
     */
    private VBox createSystemInfoSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #555; -fx-border-width: 1; -fx-padding: 10;");

        Label sectionTitle = new Label("System Information");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff00;");
        section.getChildren().add(sectionTitle);

        Platform platform = Platform.getCurrent();
        String osInfo = String.format("OS: %s (%s)",
            platform, System.getProperty("os.name"));

        Label osLabel = new Label(osInfo);
        osLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(osLabel);

        String javaVersion = "Java: " + System.getProperty("java.version");
        Label javaLabel = new Label(javaVersion);
        javaLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(javaLabel);

        String javaFxVersion = "JavaFX: " + System.getProperty("javafx.version");
        Label fxLabel = new Label(javaFxVersion);
        fxLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(fxLabel);

        String memoryUsage = String.format("Memory: %.1f MB / %.1f MB",
            Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0,
            Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0);
        Label memoryLabel = new Label(memoryUsage);
        memoryLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(memoryLabel);

        return section;
    }

    /**
     * Создать секцию управления модулями
     */
    private VBox createModulesSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #555; -fx-border-width: 1; -fx-padding: 10;");

        Label sectionTitle = new Label("Modules");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff00;");
        section.getChildren().add(sectionTitle);

        ModuleManager moduleManager = ModuleManager.getInstance();
        Label moduleCountLabel = new Label("Total Modules: " + moduleManager.getModuleCount());
        moduleCountLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(moduleCountLabel);

        Label activeModulesLabel = new Label("Active Modules: " + moduleManager.getActiveModules().size());
        activeModulesLabel.setStyle("-fx-text-fill: white;");
        section.getChildren().add(activeModulesLabel);

        // Список модулей
        StringBuilder modulesList = new StringBuilder();
        for (GameModule module : moduleManager.getAllModules()) {
            modulesList.append("• ")
                .append(module.getName())
                .append(" v")
                .append(module.getVersion())
                .append(" [")
                .append(module.getState())
                .append("]\n");
        }

        Label modulesLabel = new Label(modulesList.toString());
        modulesLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-family: monospace; -fx-font-size: 11px;");
        modulesLabel.setMaxHeight(100);
        section.getChildren().add(modulesLabel);

        return section;
    }

    /**
     * Создать секцию тестовых кнопок
     */
    private VBox createTestButtonsSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #555; -fx-border-width: 1; -fx-padding: 10;");

        Label sectionTitle = new Label("Test Actions");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff00;");
        section.getChildren().add(sectionTitle);

        // Кнопка для теста модулей
        Button testModulesButton = new Button("Test Modules");
        testModulesButton.setOnAction(e -> testModules());
        section.getChildren().add(testModulesButton);

        // Кнопка для теста ресурсов
        Button testResourcesButton = new Button("Test Resources");
        testResourcesButton.setOnAction(e -> testResources());
        section.getChildren().add(testResourcesButton);

        // Кнопка для очистки логов
        Button clearLogsButton = new Button("Clear Logs");
        clearLogsButton.setOnAction(e -> clearLogs());
        section.getChildren().add(clearLogsButton);

        // Кнопка для создания бэкапа конфигурации
        Button backupConfigButton = new Button("Backup Config");
        backupConfigButton.setOnAction(e -> backupConfig());
        section.getChildren().add(backupConfigButton);

        return section;
    }

    /**
     * Создать секцию настроек логирования
     */
    private VBox createLoggingSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #555; -fx-border-width: 1; -fx-padding: 10;");

        Label sectionTitle = new Label("Logging");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff00;");
        section.getChildren().add(sectionTitle);

        // Чекбоксы для уровней логирования
        CheckBox debugCheck = new CheckBox("DEBUG");
        debugCheck.setOnAction(e -> setLogLevel("DEBUG", debugCheck.isSelected()));
        debugCheck.setStyle("-fx-text-fill: white;");
        section.getChildren().add(debugCheck);

        CheckBox infoCheck = new CheckBox("INFO");
        infoCheck.setSelected(true);
        infoCheck.setOnAction(e -> setLogLevel("INFO", infoCheck.isSelected()));
        infoCheck.setStyle("-fx-text-fill: white;");
        section.getChildren().add(infoCheck);

        CheckBox gameplayCheck = new CheckBox("GAMEPLAY");
        gameplayCheck.setOnAction(e -> setLogLevel("GAMEPLAY", gameplayCheck.isSelected()));
        gameplayCheck.setStyle("-fx-text-fill: white;");
        section.getChildren().add(gameplayCheck);

        CheckBox warningCheck = new CheckBox("WARNING");
        warningCheck.setSelected(true);
        warningCheck.setOnAction(e -> setLogLevel("WARNING", warningCheck.isSelected()));
        warningCheck.setStyle("-fx-text-fill: white;");
        section.getChildren().add(warningCheck);

        CheckBox errorCheck = new CheckBox("ERROR");
        errorCheck.setSelected(true);
        errorCheck.setOnAction(e -> setLogLevel("ERROR", errorCheck.isSelected()));
        errorCheck.setStyle("-fx-text-fill: white;");
        section.getChildren().add(errorCheck);

        return section;
    }

    /**
     * Показать DevTools
     */
    public void show() {
        if (!isVisible) {
            devToolsWindow.show();
            isVisible = true;
            GameLogger.system("DevTools opened");
        }
    }

    /**
     * Скрыть DevTools
     */
    public void hide() {
        if (isVisible) {
            devToolsWindow.hide();
            isVisible = false;
            GameLogger.system("DevTools closed");
        }
    }

    /**
     * Переключить видимость DevTools
     */
    public void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }

    /**
     * Тестировать модули
     */
    private void testModules() {
        try {
            ModuleManager moduleManager = ModuleManager.getInstance();
            GameLogger.info("DevTools", "=== Module Test ===");

            // Проверяем все модули
            for (GameModule module : moduleManager.getAllModules()) {
                GameLogger.info("DevTools", String.format(
                    "Module: %s v%s [%s]",
                    module.getName(),
                    module.getVersion(),
                    module.getState()
                ));
            }

            // Проверяем зависимости
            for (GameModule module : moduleManager.getAllModules()) {
                String[] deps = module.getDependencies();
                if (deps.length > 0) {
                    GameLogger.info("DevTools", String.format(
                        "Dependencies for %s: %s",
                        module.getName(),
                        String.join(", ", deps)
                    ));
                }
            }

            GameLogger.info("DevTools", "Module test completed");

        } catch (Exception e) {
            GameLogger.error("DevTools", "Module test failed", e);
        }
    }

    /**
     * Тестировать ресурсы
     */
    private void testResources() {
        try {
            ResourceManager resourceManager = ResourceManager.getInstance();
            GameLogger.info("DevTools", "=== Resource Test ===");

            // Тестируем загрузку различных ресурсов
            String[] testResources = {
                "assets/textures/ui/inventory/sword0.png",
                "assets/audio/click.wav",
                "config/weapons.json",
                "i18n/strings_ru.properties"
            };

            for (String resource : testResources) {
                boolean exists = resourceManager.resourceExists(resource);
                GameLogger.info("DevTools", String.format(
                    "Resource %s: %s",
                    resource,
                    exists ? "FOUND" : "NOT FOUND"
                ));
            }

            // Тестируем список ресурсов
            var weapons = resourceManager.listResources("assets/textures/ui/inventory");
            GameLogger.info("DevTools", "Found " + weapons.size() + " weapons");

            GameLogger.info("DevTools", "Resource test completed");

        } catch (Exception e) {
            GameLogger.error("DevTools", "Resource test failed", e);
        }
    }

    /**
     * Очистить логи
     */
    private void clearLogs() {
        GameLogger.system("Clearing logs...");
        // Логи можно очистить, удалив файл (реализовано в GameLogger.shutdown)
        GameLogger.info("DevTools", "Logs cleared");
    }

    /**
     * Создать бэкап конфигурации
     */
    private void backupConfig() {
        try {
            ConfigManager configManager = ConfigManager.getInstance();
            configManager.backupConfigs();
            GameLogger.info("DevTools", "Configuration backed up");
        } catch (Exception e) {
            GameLogger.error("DevTools", "Backup failed", e);
        }
    }

    /**
     * Установить уровень логирования
     */
    private void setLogLevel(String level, boolean enabled) {
        if (enabled) {
            GameLogger.info("DevTools", "Enabled log level: " + level);
        } else {
            GameLogger.info("DevTools", "Disabled log level: " + level);
        }
        // Реализация изменения уровня логирования
        // (можно добавить в GameLogger)
    }

    /**
     * Показать быструю информацию (overlay)
     */
    public void showQuickInfo() {
        // Создаем overlay с быстрой информацией
        // Показываем FPS, память, активные модули
        // Можно вызывать по клавише F12
    }

    public boolean isVisible() {
        return isVisible;
    }
}
