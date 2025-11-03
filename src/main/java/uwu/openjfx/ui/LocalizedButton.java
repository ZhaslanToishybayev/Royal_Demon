package uwu.openjfx.ui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import uwu.openjfx.i18n.EnhancedLocalizationManager;

/**
 * Локализованная кнопка, которая автоматически обновляет текст при смене языка
 */
public class LocalizedButton extends Button {

    private final EnhancedLocalizationManager localizationManager;
    private String localizationKey;
    private Object[] formatArgs;

    public LocalizedButton(String key) {
        this.localizationManager = EnhancedLocalizationManager.getInstance();
        this.localizationKey = key;
        this.formatArgs = null;

        // Настраиваем стиль кнопки
        setupStyle();

        // Устанавливаем начальный текст
        updateText();

        // Добавляем слушатель для обновления при смене языка
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateText();
        });
    }

    public LocalizedButton(String key, Object... args) {
        this.localizationManager = EnhancedLocalizationManager.getInstance();
        this.localizationKey = key;
        this.formatArgs = args;

        // Настраиваем стиль кнопки
        setupStyle();

        // Устанавливаем начальный текст
        updateText();

        // Добавляем слушатель для обновления при смене языка
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateText();
        });
    }

    /**
     * Настраивает стиль кнопки
     */
    private void setupStyle() {
        setFont(Font.font("Arial", 14));
        setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4a90e2, #357abd);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-radius: 5px;" +
            "-fx-border-color: #2e6da4;" +
            "-fx-border-width: 1px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0.0, 0, 1);"
        );

        // Эффекты при наведении
        setOnMouseEntered(e -> {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom, #5ba0f2, #4590cd);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-radius: 5px;" +
                "-fx-border-color: #2e6da4;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 8, 0.0, 0, 2);"
            );
        });

        setOnMouseExited(e -> {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4a90e2, #357abd);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-radius: 5px;" +
                "-fx-border-color: #2e6da4;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0.0, 0, 1);"
            );
        });

        // Эффект при нажатии
        setOnMousePressed(e -> {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom, #357abd, #2980b9);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-radius: 5px;" +
                "-fx-border-color: #2e6da4;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0.0, 0, 1);"
            );
        });

        setOnMouseReleased(e -> {
            setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4a90e2, #357abd);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-radius: 5px;" +
                "-fx-border-color: #2e6da4;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0.0, 0, 1);"
            );
        });
    }

    /**
     * Обновляет текст в соответствии с текущим языком
     */
    private void updateText() {
        if (formatArgs != null && formatArgs.length > 0) {
            setText(localizationManager.getString(localizationKey, formatArgs));
        } else {
            setText(localizationManager.getString(localizationKey));
        }
    }

    /**
     * Изменяет ключ локализации и обновляет текст
     */
    public void setKey(String key) {
        this.localizationKey = key;
        this.formatArgs = null;
        updateText();
    }

    /**
     * Изменяет ключ локализации с параметрами форматирования
     */
    public void setKey(String key, Object... args) {
        this.localizationKey = key;
        this.formatArgs = args;
        updateText();
    }

    /**
     * Получает текущий ключ локализации
     */
    public String getKey() {
        return localizationKey;
    }

    /**
     * Создает кнопку с основным стилем
     */
    public static LocalizedButton createPrimary(String key) {
        LocalizedButton button = new LocalizedButton(key);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5cb85c, #4cae4c);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-radius: 5px;" +
            "-fx-border-color: #4cae4c;" +
            "-fx-border-width: 1px;" +
            "-fx-font-size: 16px;" +
            "-fx-padding: 10px 20px;"
        );
        return button;
    }

    /**
     * Создает кнопку со стилем опасного действия
     */
    public static LocalizedButton createDanger(String key) {
        LocalizedButton button = new LocalizedButton(key);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #d9534f, #c9302c);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-radius: 5px;" +
            "-fx-border-color: #c9302c;" +
            "-fx-border-width: 1px;" +
            "-fx-font-size: 16px;" +
            "-fx-padding: 10px 20px;"
        );
        return button;
    }

    /**
     * Создает кнопку с вторичным стилем
     */
    public static LocalizedButton createSecondary(String key) {
        LocalizedButton button = new LocalizedButton(key);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #f0ad4e, #ec971f);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-radius: 5px;" +
            "-fx-border-color: #ec971f;" +
            "-fx-border-width: 1px;" +
            "-fx-font-size: 16px;" +
            "-fx-padding: 10px 20px;"
        );
        return button;
    }
}