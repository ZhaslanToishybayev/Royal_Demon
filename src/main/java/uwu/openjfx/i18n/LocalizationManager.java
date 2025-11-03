package uwu.openjfx.i18n;

import com.almasb.fxgl.dsl.FXGL;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Менеджер локализации для поддержки многоязычности
 */
public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;
    private static boolean isTesting = false;
    
    private LocalizationManager() {
        if (!isTesting) {
            // По умолчанию используем русский язык
            setLocale(new Locale("ru"));
        }
    }
    
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }
    
    public static void setTesting(boolean testing) {
        isTesting = testing;
    }

    public static boolean isTesting() {
        return isTesting;
    }
    
    /**
     * Установить язык локализации
     */
    public void setLocale(Locale locale) {
        try {
            this.currentLocale = locale;
            this.resourceBundle = ResourceBundle.getBundle("i18n.strings", locale);
        } catch (MissingResourceException e) {
            // Если ресурс не найден, используем русский по умолчанию
            this.currentLocale = new Locale("ru");
            this.resourceBundle = ResourceBundle.getBundle("i18n.strings", this.currentLocale);
        }
    }
    
    /**
     * Получить локализованную строку по ключу
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            // Возвращаем ключ, если строка не найдена
            return "[" + key + "]";
        }
    }
    
    /**
     * Получить локализованную строку с форматированием
     */
    public String getString(String key, Object... args) {
        try {
            String format = resourceBundle.getString(key);
            return String.format(format, args);
        } catch (MissingResourceException e) {
            return "[" + key + "]";
        }
    }
    
    /**
     * Получить текущий язык
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Проверить, поддерживается ли язык
     */
    public boolean isLocaleSupported(Locale locale) {
        try {
            ResourceBundle.getBundle("i18n.strings", locale);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }
}