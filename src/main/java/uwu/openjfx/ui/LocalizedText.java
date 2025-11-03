package uwu.openjfx.ui;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import uwu.openjfx.i18n.EnhancedLocalizationManager;

/**
 * Локализованный текстовый компонент, который автоматически обновляется при смене языка
 */
public class LocalizedText extends Text {

    private final EnhancedLocalizationManager localizationManager;
    private String localizationKey;
    private Object[] formatArgs;

    public LocalizedText(String key) {
        this(key, Color.WHITE, 16.0);
    }

    public LocalizedText(String key, Color color, double fontSize) {
        this.localizationManager = EnhancedLocalizationManager.getInstance();
        this.localizationKey = key;
        this.formatArgs = null;

        // Настраиваем внешний вид
        setFill(color);
        setFont(Font.font("Arial", fontSize));

        // Устанавливаем начальный текст
        updateText();

        // Добавляем слушатель для обновления при смене языка
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateText();
        });
    }

    public LocalizedText(String key, Object... args) {
        this(key, Color.WHITE, 16.0, args);
    }

    public LocalizedText(String key, Color color, double fontSize, Object... args) {
        this.localizationManager = EnhancedLocalizationManager.getInstance();
        this.localizationKey = key;
        this.formatArgs = args;

        // Настраиваем внешний вид
        setFill(color);
        setFont(Font.font("Arial", fontSize));

        // Устанавливаем начальный текст
        updateText();

        // Добавляем слушатель для обновления при смене языка
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateText();
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
     * Создает локализованный текст с указанными параметрами
     */
    public static LocalizedText create(String key) {
        return new LocalizedText(key);
    }

    /**
     * Создает локализованный текст с указанием цвета и размера
     */
    public static LocalizedText create(String key, Color color, double fontSize) {
        return new LocalizedText(key, color, fontSize);
    }

    /**
     * Создает локализованный текст с параметрами форматирования
     */
    public static LocalizedText create(String key, Object... args) {
        return new LocalizedText(key, args);
    }

    /**
     * Создает заголовок с локализацией
     */
    public static LocalizedText createTitle(String key) {
        return new LocalizedText(key, Color.GOLD, 24.0);
    }

    /**
     * Создает подзаголовок с локализацией
     */
    public static LocalizedText createSubtitle(String key) {
        return new LocalizedText(key, Color.LIGHTGRAY, 18.0);
    }

    /**
     * Создает текст для кнопки с локализацией
     */
    public static LocalizedText createButtonText(String key) {
        return new LocalizedText(key, Color.WHITE, 14.0);
    }

    /**
     * Создает текст для описания с локализацией
     */
    public static LocalizedText createDescription(String key) {
        return new LocalizedText(key, Color.LIGHTBLUE, 12.0);
    }
}