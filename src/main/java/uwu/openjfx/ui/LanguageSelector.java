package uwu.openjfx.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.integration.GameIntegration;

/**
 * Компонент для выбора языка в настройках игры
 */
public class LanguageSelector extends VBox {

    private final EnhancedLocalizationManager localizationManager;
    private final Label currentLanguageLabel;

    public LanguageSelector() {
        this.localizationManager = EnhancedLocalizationManager.getInstance();

        // Создаем UI элементы
        Label titleLabel = new Label("Language / Язык");
        titleLabel.setFont(Font.font("Arial Bold", 18));
        titleLabel.setTextFill(Color.GOLD);

        currentLanguageLabel = new Label();
        currentLanguageLabel.setFont(Font.font("Arial", 14));
        currentLanguageLabel.setTextFill(Color.WHITE);

        Button russianButton = new Button("Русский");
        russianButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-size: 14px;");
        russianButton.setOnAction(e -> changeLanguage("ru"));

        Button englishButton = new Button("English");
        englishButton.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-font-size: 14px;");
        englishButton.setOnAction(e -> changeLanguage("en"));

        // Настраиваем компоновку
        setSpacing(15);
        setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().addAll(titleLabel, currentLanguageLabel, russianButton, englishButton);

        // Обновляем отображение текущего языка
        updateCurrentLanguageDisplay();

        // Добавляем слушатель для обновления при смене языка
        localizationManager.localeProperty().addListener((obs, oldVal, newVal) -> {
            updateCurrentLanguageDisplay();
        });
    }

    /**
     * Изменяет язык игры
     */
    private void changeLanguage(String languageTag) {
        GameIntegration.setLanguage(languageTag);
    }

    /**
     * Обновляет отображение текущего языка
     */
    private void updateCurrentLanguageDisplay() {
        java.util.Locale currentLocale = localizationManager.getCurrentLocale();
        String displayName = localizationManager.getLanguageDisplayName(currentLocale);
        currentLanguageLabel.setText("Текущий язык / Current: " + displayName);
    }

    /**
     * Создает VBox с компонентом выбора языка для использования в меню
     */
    public static VBox createLanguageSelector() {
        return new LanguageSelector();
    }
}