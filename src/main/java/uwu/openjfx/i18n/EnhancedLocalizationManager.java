package uwu.openjfx.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Улучшенный менеджер локализации с поддержкой динамической смены языка
 */
public class EnhancedLocalizationManager {
    private static EnhancedLocalizationManager instance;

    private final Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();
    private final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>(RUSSIAN);

    // Поддерживаемые языки
    public static final Locale RUSSIAN = Locale.forLanguageTag("ru");
    public static final Locale ENGLISH = Locale.ENGLISH;

    private EnhancedLocalizationManager() {
        loadTranslations();
    }

    public static EnhancedLocalizationManager getInstance() {
        if (instance == null) {
            instance = new EnhancedLocalizationManager();
        }
        return instance;
    }

    /**
     * Загружает все переводы
     */
    private void loadTranslations() {
        // Русские переводы
        Map<String, String> ruTranslations = new HashMap<>();
        ruTranslations.put("game.title", "Королевские Демоны");
        ruTranslations.put("game.ui.health", "Здоровье");
        ruTranslations.put("game.ui.gold", "Золото");
        ruTranslations.put("game.ui.level", "Уровень");
        ruTranslations.put("game.ui.experience", "Опыт");
        ruTranslations.put("game.ui.damage", "Урон");
        ruTranslations.put("game.ui.defense", "Защита");
        ruTranslations.put("game.ui.speed", "Скорость");

        // Меню
        ruTranslations.put("menu.new_game", "Новая Игра");
        ruTranslations.put("menu.continue", "Продолжить");
        ruTranslations.put("menu.options", "Настройки");
        ruTranslations.put("menu.exit", "Выход");
        ruTranslations.put("menu.resume", "Продолжить");
        ruTranslations.put("menu.main_menu", "Главное меню");
        ruTranslations.put("menu.save_game", "Сохранить игру");
        ruTranslations.put("menu.load_game", "Загрузить игру");
        ruTranslations.put("menu.settings", "Настройки");
        ruTranslations.put("menu.language", "Язык");
        ruTranslations.put("menu.volume", "Громкость");
        ruTranslations.put("menu.difficulty", "Сложность");
        ruTranslations.put("menu.weapon", "Оружие");
        ruTranslations.put("menu.lets_go", "Давай!");
        ruTranslations.put("menu.name", "Имя");
        ruTranslations.put("menu.who_are_we", "Кто мы?");
        ruTranslations.put("menu.credits", "Авторы");
        ruTranslations.put("menu.game_over", "Игра окончена");
        ruTranslations.put("menu.game_over_message", "Вы погибли в подземелье");
        ruTranslations.put("menu.restart", "Начать заново");
        ruTranslations.put("menu.exit_main_menu", "Вы уверены, что хотите выйти в главное меню?");
        ruTranslations.put("menu.unsaved_progress", "Несохраненный прогресс будет потерян.");
        ruTranslations.put("menu.reset_settings", "Сбросить настройки");
        ruTranslations.put("menu.settings_restore_confirm", "Вы уверены, что хотите восстановить настройки по умолчанию?");
        ruTranslations.put("menu.click_me", "Нажмите");
        ruTranslations.put("menu.enter_name", "Введите ваше имя");
        ruTranslations.put("menu.easy", "Легко");
        ruTranslations.put("menu.medium", "Средне");
        ruTranslations.put("menu.hard", "Сложно");
        ruTranslations.put("menu.sword", "Меч");
        ruTranslations.put("menu.wand", "Магический посох");
        ruTranslations.put("menu.bow", "Лук");
        ruTranslations.put("menu.back", "Назад");
        ruTranslations.put("menu.start_game_confirm", "Вы уверены, что хотите начать новую игру?");
        ruTranslations.put("menu.setup_required", "Пожалуйста, настройте своего персонажа (имя, сложность, оружие).");
        ruTranslations.put("game.paused", "Пауза");
        ruTranslations.put("game.game_over", "ПОРАЖЕНИЕ");
        ruTranslations.put("game.defeat_message", "Вы были побеждены в битве");

        // Квесты и задания
        ruTranslations.put("quest.new", "Новый квест!");
        ruTranslations.put("quest.completed", "Квест выполнен!");
        ruTranslations.put("quest.objective", "Цель:");
        ruTranslations.put("quest.reward", "Награда:");
        ruTranslations.put("quest.main", "Основной");
        ruTranslations.put("quest.side", "Побочный");

        // Уведомления
        ruTranslations.put("notification.save.success", "Игра сохранена");
        ruTranslations.put("notification.save.failed", "Не удалось сохранить игру");
        ruTranslations.put("notification.load.success", "Игра загружена");
        ruTranslations.put("notification.load.failed", "Не удалось загрузить игру");

        // Оружие
        ruTranslations.put("weapon.sword", "Меч");
        ruTranslations.put("weapon.bow", "Лук");
        ruTranslations.put("weapon.staff", "Посох");
        ruTranslations.put("weapon.damage", "Урон");
        ruTranslations.put("weapon.attack_speed", "Скорость атаки");
        ruTranslations.put("weapon.critical_chance", "Шанс крит. урона");

        // Предметы
        ruTranslations.put("item.health_potion", "Зелье здоровья");
        ruTranslations.put("item.rage_potion", "Зелье ярости");
        ruTranslations.put("item.gold_coin", "Золотая монета");
        ruTranslations.put("item.picked_up", "Подобрано:");

        // Сообщения
        ruTranslations.put("message.level_up", "Повышение уровня!");
        ruTranslations.put("message.level_up_to", "Новый уровень:");
        ruTranslations.put("message.critical_hit", "Критический удар!");
        ruTranslations.put("message.dodge", "Уклонение!");
        ruTranslations.put("message.block", "Блок!");

        // Игровые настройки
        ruTranslations.put("settings.title", "Настройки");
        ruTranslations.put("settings.language", "Язык");
        ruTranslations.put("settings.volume", "Громкость");
        ruTranslations.put("settings.difficulty", "Сложность");
        ruTranslations.put("settings.controls", "Управление");
        ruTranslations.put("settings.video", "Видео");
        ruTranslations.put("settings.audio", "Аудио");
        ruTranslations.put("settings.apply", "Применить");
        ruTranslations.put("settings.cancel", "Отмена");
        ruTranslations.put("settings.save", "Сохранить");

        // Подсказки
        ruTranslations.put("tooltip.move", "Движение");
        ruTranslations.put("tooltip.attack", "Атака");
        ruTranslations.put("tooltip.interact", "Взаимодействовать");
        ruTranslations.put("tooltip.inventory", "Инвентарь");
        ruTranslations.put("tooltip.map", "Карта");
        ruTranslations.put("tooltip.quests", "Задания");

        // Статусы эффектов
        ruTranslations.put("effect.poisoned", "Отравлен");
        ruTranslations.put("effect.regeneration", "Регенерация");
        ruTranslations.put("effect.buffed", "Усилен");
        ruTranslations.put("effect.weakened", "Ослаблен");
        ruTranslations.put("effect.frozen", "Заморожен");
        ruTranslations.put("effect.burning", "Горит");
        ruTranslations.put("effect.stunned", "Оглушен");
        ruTranslations.put("effect.shield", "Щит");

        // Оружие
        ruTranslations.put("weapon.sword0", "Железный Меч");
        ruTranslations.put("weapon.sword1", "Стальной Меч");
        ruTranslations.put("weapon.sword2", "Пылающий Меч");
        ruTranslations.put("weapon.bow0", "Простой Лук");
        ruTranslations.put("weapon.bow1", "Длинный Лук");
        ruTranslations.put("weapon.bow2", "Эльфийский Лук");
        ruTranslations.put("weapon.staff0", "Посох Новичка");
        ruTranslations.put("weapon.staff1", "Магический Посох");
        ruTranslations.put("weapon.staff2", "Посох Архимага");
        ruTranslations.put("weapon.heavy", "Тяжелый Меч");

        // Клавиши
        ruTranslations.put("keys.move.movement", "Движение: WASD / Стрелки");
        ruTranslations.put("keys.move.attack", "Атака: ЛКМ");
        ruTranslations.put("keys.move.ultimate", "Ультимейт: Пробел");
        ruTranslations.put("keys.move.interact", "Взаимодействие: E");
        ruTranslations.put("keys.move.use_item", "Использовать предмет: F");
        ruTranslations.put("keys.move.inventory", "Инвентарь: I");
        ruTranslations.put("keys.move.potion1", "Зелье 1: 1");
        ruTranslations.put("keys.move.potion2", "Зелье 2: 2");
        ruTranslations.put("keys.move.map", "Карта: M");
        ruTranslations.put("keys.move.teleport", "Телепорт: P");

        // Прогресс
        ruTranslations.put("progress.enemies_killed", "Врагов убито");
        ruTranslations.put("progress.damage_dealt", "Нанесено урона");
        ruTranslations.put("progress.gold_collected", "Собрано золота");
        ruTranslations.put("progress.play_time", "Время игры");
        ruTranslations.put("progress.difficulty_level", "Уровень сложности");

        // Враги
        ruTranslations.put("enemy.slime", "Слизень");
        ruTranslations.put("enemy.bat", "Летучая мышь");
        ruTranslations.put("enemy.skeleton", "Скелет");
        ruTranslations.put("enemy.zombie", "Зомби");
        ruTranslations.put("enemy.orc", "Орк");
        ruTranslations.put("enemy.troll", "Тролль");
        ruTranslations.put("enemy.dragon", "Дракон");
        ruTranslations.put("enemy.boss", "Босс");

        // Разное
        ruTranslations.put("misc.loading", "Загрузка...");
        ruTranslations.put("misc.saving", "Сохранение...");
        ruTranslations.put("misc.error", "Ошибка");
        ruTranslations.put("misc.warning", "Предупреждение");
        ruTranslations.put("misc.success", "Успех");
        ruTranslations.put("misc.yes", "Да");
        ruTranslations.put("misc.no", "Нет");
        ruTranslations.put("misc.ok", "OK");
        ruTranslations.put("misc.cancel", "Отмена");

        // Кат-сцены
        ruTranslations.put("cutscene.intro.title", "Королевские Демоны");
        ruTranslations.put("cutscene.intro.subtitle", "Легенда о затерянном королевстве");
        ruTranslations.put("cutscene.intro.story",
            "В древние времена, когда мир был молод, существовало могущественное королевство. " +
            "Но тьма спустилась на земли, и демоны захватили трон. " +
            "Вы — последний надежный воин, который должен вернуть мир в королевство...");
        ruTranslations.put("cutscene.continue", "Нажмите ПРОБЕЛ для продолжения...");
        ruTranslations.put("cutscene.skip", "Нажмите ESC для пропуска");

        translations.put("ru", ruTranslations);

        // Английские переводы
        Map<String, String> enTranslations = new HashMap<>();
        enTranslations.put("game.title", "Royal Demons");
        enTranslations.put("game.ui.health", "Health");
        enTranslations.put("game.ui.gold", "Gold");
        enTranslations.put("game.ui.level", "Level");
        enTranslations.put("game.ui.experience", "Experience");
        enTranslations.put("game.ui.damage", "Damage");
        enTranslations.put("game.ui.defense", "Defense");
        enTranslations.put("game.ui.speed", "Speed");

        // Меню
        enTranslations.put("menu.new_game", "New Game");
        enTranslations.put("menu.continue", "Continue");
        enTranslations.put("menu.options", "Options");
        enTranslations.put("menu.exit", "Exit");
        enTranslations.put("menu.resume", "Resume");
        enTranslations.put("menu.main_menu", "Main Menu");
        enTranslations.put("menu.save_game", "Save Game");
        enTranslations.put("menu.load_game", "Load Game");
        enTranslations.put("menu.settings", "Settings");
        enTranslations.put("menu.language", "Language");
        enTranslations.put("menu.volume", "Volume");
        enTranslations.put("menu.difficulty", "Difficulty");
        enTranslations.put("menu.weapon", "Weapon");
        enTranslations.put("menu.lets_go", "Let's Go");
        enTranslations.put("menu.name", "Name");
        enTranslations.put("menu.who_are_we", "Who Are We");
        enTranslations.put("menu.credits", "Credits");
        enTranslations.put("menu.game_over", "Game Over");
        enTranslations.put("menu.game_over_message", "You died in the dungeon");
        enTranslations.put("menu.restart", "Try Again");
        enTranslations.put("game.paused", "Paused");
        enTranslations.put("menu.reset_settings", "Reset Settings");
        enTranslations.put("menu.settings_restore_confirm", "Are you sure you want to restore default settings?");
        enTranslations.put("menu.exit_main_menu", "Are you sure you want to exit to main menu?");
        enTranslations.put("menu.unsaved_progress", "Unsaved progress will be lost.");
        enTranslations.put("menu.click_me", "Click Me");
        enTranslations.put("menu.enter_name", "Enter your name");
        enTranslations.put("menu.easy", "Easy");
        enTranslations.put("menu.medium", "Medium");
        enTranslations.put("menu.hard", "Hard");
        enTranslations.put("menu.sword", "Sword");
        enTranslations.put("menu.wand", "Wand");
        enTranslations.put("menu.start_game_confirm", "Are you sure you want to start a new game?");
        enTranslations.put("menu.setup_required", "Please set up your character (name, difficulty, weapon).");
        enTranslations.put("menu.back", "Back");
        enTranslations.put("game.game_over", "DEFEAT");
        enTranslations.put("game.defeat_message", "You were defeated in battle");

        // Квесты и задания
        enTranslations.put("quest.new", "New Quest!");
        enTranslations.put("quest.completed", "Quest Completed!");
        enTranslations.put("quest.objective", "Objective:");
        enTranslations.put("quest.reward", "Reward:");
        enTranslations.put("quest.main", "Main");
        enTranslations.put("quest.side", "Side");

        // Уведомления
        enTranslations.put("notification.save.success", "Game Saved");
        enTranslations.put("notification.save.failed", "Failed to Save Game");
        enTranslations.put("notification.load.success", "Game Loaded");
        enTranslations.put("notification.load.failed", "Failed to Load Game");

        // Оружие
        enTranslations.put("weapon.sword", "Sword");
        enTranslations.put("weapon.bow", "Bow");
        enTranslations.put("weapon.staff", "Staff");
        enTranslations.put("weapon.damage", "Damage");
        enTranslations.put("weapon.attack_speed", "Attack Speed");
        enTranslations.put("weapon.critical_chance", "Crit Chance");

        // Предметы
        enTranslations.put("item.health_potion", "Health Potion");
        enTranslations.put("item.rage_potion", "Rage Potion");
        enTranslations.put("item.gold_coin", "Gold Coin");
        enTranslations.put("item.picked_up", "Picked up:");

        // Сообщения
        enTranslations.put("message.level_up", "Level Up!");
        enTranslations.put("message.level_up_to", "New Level:");
        enTranslations.put("message.critical_hit", "Critical Hit!");
        enTranslations.put("message.dodge", "Dodge!");
        enTranslations.put("message.block", "Block!");

        // Игровые настройки
        enTranslations.put("settings.title", "Settings");
        enTranslations.put("settings.language", "Language");
        enTranslations.put("settings.volume", "Volume");
        enTranslations.put("settings.difficulty", "Difficulty");
        enTranslations.put("settings.controls", "Controls");
        enTranslations.put("settings.video", "Video");
        enTranslations.put("settings.audio", "Audio");
        enTranslations.put("settings.apply", "Apply");
        enTranslations.put("settings.cancel", "Cancel");
        enTranslations.put("settings.save", "Save");

        // Подсказки
        enTranslations.put("tooltip.move", "Move");
        enTranslations.put("tooltip.attack", "Attack");
        enTranslations.put("tooltip.interact", "Interact");
        enTranslations.put("tooltip.inventory", "Inventory");
        enTranslations.put("tooltip.map", "Map");
        enTranslations.put("tooltip.quests", "Quests");

        // Статусы эффектов
        enTranslations.put("effect.poisoned", "Poisoned");
        enTranslations.put("effect.regeneration", "Regeneration");
        enTranslations.put("effect.buffed", "Buffed");
        enTranslations.put("effect.weakened", "Weakened");
        enTranslations.put("effect.frozen", "Frozen");
        enTranslations.put("effect.burning", "Burning");

        // Кат-сцены
        enTranslations.put("cutscene.intro.title", "Royal Demons");
        enTranslations.put("cutscene.intro.subtitle", "Legend of the Lost Kingdom");
        enTranslations.put("cutscene.intro.story",
            "In ancient times, when the world was young, there existed a mighty kingdom. " +
            "But darkness descended upon the lands, and demons seized the throne. " +
            "You are the last hope warrior who must restore peace to the kingdom...");
        enTranslations.put("cutscene.continue", "Press SPACE to continue...");
        enTranslations.put("cutscene.skip", "Press ESC to skip");

        translations.put("en", enTranslations);
    }

    /**
     * Получает строку на текущем языке
     */
    public String getString(String key) {
        return getString(key, currentLocale.get());
    }

    /**
     * Получает строку на указанном языке
     */
    public String getString(String key, Locale locale) {
        Map<String, String> langMap = translations.get(locale.getLanguage());
        if (langMap != null) {
            String value = langMap.get(key);
            return value != null ? value : "[" + key + "]";
        }
        return "[" + key + "]";
    }

    /**
     * Получает форматированную строку
     */
    public String getString(String key, Object... args) {
        String template = getString(key);
        return String.format(template, args);
    }

    /**
     * Получает форматированную строку на указанном языке
     */
    public String getString(String key, Locale locale, Object... args) {
        String template = getString(key, locale);
        return String.format(template, args);
    }

    /**
     * Устанавливает текущий язык
     */
    public void setLocale(Locale locale) {
        currentLocale.set(locale);
    }

    /**
     * Получает текущий язык
     */
    public Locale getCurrentLocale() {
        return currentLocale.get();
    }

    /**
     * Получает свойство текущего языка
     */
    public ObjectProperty<Locale> localeProperty() {
        return currentLocale;
    }

    /**
     * Получает все поддерживаемые языки
     */
    public List<Locale> getSupportedLocales() {
        return Arrays.asList(RUSSIAN, ENGLISH);
    }

    /**
     * Получает отображаемое имя языка
     */
    public String getLanguageDisplayName(Locale locale) {
        switch (locale.getLanguage()) {
            case "ru": return "Русский";
            case "en": return "English";
            default: return locale.getDisplayName();
        }
    }

    /**
     * Проверяет, поддерживается ли язык
     */
    public boolean isSupported(Locale locale) {
        return translations.containsKey(locale.getLanguage());
    }

    /**
     * Добавляет или обновляет перевод
     */
    public void addTranslation(Locale locale, String key, String value) {
        translations.computeIfAbsent(locale.getLanguage(), k -> new HashMap<>()).put(key, value);
    }

    /**
     * Получает перевод с резервным значением
     */
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value.equals("[" + key + "]") ? defaultValue : value;
    }
}