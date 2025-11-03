package uwu.openjfx.core;

/**
 * Централизованные константы игры для устранения magic numbers.
 * Содержит все основные числовые значения, используемые в игре.
 */
public final class GameConstants {

    // Предотвращаем создание экземпляра
    private GameConstants() {}

    // === Параметры игрока ===
    public static class Player {
        public static final double DEFAULT_SPEED = 180.0;
        public static final double MAGIC_SPEED = 120.0;
        public static final int DEFAULT_HEALTH = 5;
        public static final int MAX_HEALTH = 999;
        public static final double VELOCITY_DECREMENTER = 5.0;
        public static final double SPRITE_OFFSET_X = 20.0;
        public static final double SPRITE_OFFSET_Y = 25.0;
        public static final double INVULNERABLE_OPACITY = 0.5;
        public static final double DEFAULT_ATTACK_POWER = 1.0;
        public static final int DEFAULT_PIERCE_POWER = 1;
    }

    // === Атаки и боевые параметры ===
    public static class Combat {
        public static final double ULTIMATE_COOLDOWN_MULTIPLIER = 1000.0;
        public static final double CHANNEL_DURATION = 2000.0;
        public static final int ATTACK_POWER_BUFF_DURATION = 300;
        public static final double ATTACK_POWER_BUFF_MULTIPLIER = 1.5;
        public static final int FAN_OF_KNIVES_COUNT = 10;
        public static final double FAN_OF_KNIVES_ANGLE_INCREMENT = 2 * Math.PI / FAN_OF_KNIVES_COUNT;
    }

    // === Размеры hitbox ===
    public static class HitBox {
        public static final double MELEE_WIDTH = 60.0;
        public static final double MELEE_HEIGHT = 40.0;
        public static final double RANGED_WIDTH = 20.0;
        public static final double RANGED_HEIGHT = 20.0;
        public static final double PROJECTILE_SPEED = 600.0;
        public static final double KNOCKBACK_FORCE = 150.0;
    }

    // === Параметры врагов ===
    public static class Enemy {
        public static final double DEFAULT_SPEED = 60.0;
        public static final double SMALL_MINION_SPEED = 40.0;
        public static final double FOREST_MINION_SPEED = 50.0;
        public static final double MINIBOSS_SPEED = 30.0;
        public static final double FINAL_BOSS_SPEED = 40.0;
        public static final int SMALL_MINION_HEALTH = 3;
        public static final int FOREST_MINION_HEALTH = 5;
        public static final int MINIBOSS_HEALTH = 10;
        public static final int FINAL_BOSS_HEALTH = 50;
        public static final double DEFAULT_ATTACK_RANGE = 100.0;
        public static final double DETECTION_RANGE = 200.0;
    }

    // === Параметры оружия ===
    public static class Weapons {
        // Золотые мечи
        public static class GoldenSword {
            public static final double[] ATTACK_DAMAGE = {50.0, 75.0, 100.0};
            public static final int[] LIGHT_DURATION = {300, 350, 400};
            public static final int[] HEAVY_DURATION = {600, 650, 700};
            public static final double[] ULTIMATE_COOLDOWN = {5.0, 4.5, 4.0};
        }

        // Луки
        public static class Bow {
            public static final double[] ATTACK_DAMAGE = {40.0, 60.0, 80.0};
            public static final int[] LIGHT_DURATION = {200, 200, 200};
            public static final int[] HEAVY_DURATION = {400, 400, 400};
            public static final double[] ULTIMATE_COOLDOWN = {6.0, 5.5, 5.0};
            public static final int[] MULTI_SHOT_COUNT = {3, 5, 7};
        }

        // Магические посохи
        public static class MagicStaff {
            public static final double[] ATTACK_DAMAGE = {35.0, 50.0, 70.0};
            public static final int[] LIGHT_DURATION = {250, 300, 350};
            public static final int[] HEAVY_DURATION = {2000, 1800, 1600};
            public static final double[] ULTIMATE_COOLDOWN = {8.0, 7.5, 7.0};
        }

        // Тяжелый меч
        public static class HeavySword {
            public static final double ATTACK_DAMAGE = 120.0;
            public static final int LIGHT_DURATION = 800;
            public static final int HEAVY_DURATION = 1200;
            public static final double ULTIMATE_COOLDOWN = 10.0;
        }
    }

    // === Параметры предметов ===
    public static class Items {
        public static final int HEALTH_POTION_HEAL = 2;
        public static final int RAGE_POTION_DURATION = 300;
        public static final double RAGE_POTION_MULTIPLIER = 2.0;
        public static final int HEART_HEAL = 3;
        public static final int MAX_HEALTH_POTIONS = 5;
        public static final int MAX_RAGE_POTIONS = 3;
    }

    // === Параметры сохранения ===
    public static class Save {
        public static final long AUTO_SAVE_INTERVAL = 300000; // 5 минут
        public static final int MAX_SAVE_SLOTS = 10;
        public static final String SAVE_FILE_EXTENSION = ".json";
        public static final String AUTO_SAVE_PREFIX = "autosave_";
        public static final String QUICK_SAVE_PREFIX = "quicksave_";
    }

    // === UI параметры ===
    public static class UI {
        public static final double HEALTH_BAR_X = 25.0;
        public static final double HEALTH_BAR_Y = 25.0;
        public static final double HEALTH_BAR_SPACING = 5.0;
        public static final int HEARTS_PER_ROW = 10;
        public static final double WINDOW_WIDTH = 960.0;
        public static final double WINDOW_HEIGHT = 640.0;
        public static final double VIEWPORT_BOUNDS = 32.0 * 70;
        public static final double VIEWPORT_OFFSET_X = -32.0 * 5;
        public static final double VIEWPORT_OFFSET_Y = -WINDOW_HEIGHT;
    }

    // === Параметры анимации ===
    public static class Animation {
        public static final double IDLE_DURATION = 0.5;
        public static final double WALK_DURATION = 0.5;
        public static final int ANIMATION_FRAMES = 9;
        public static final int SPRITE_WIDTH = 40;
        public static final int SPRITE_HEIGHT = 55;
        public static final int IDLE_START_FRAME = 0;
        public static final int IDLE_END_FRAME = 3;
        public static final int WALK_START_FRAME = 4;
        public static final int WALK_END_FRAME = 7;
    }

    // === Параметры прогрессии ===
    public static class Progression {
        public static final int[] EXPERIENCE_REQUIREMENTS = {
            0, 100, 250, 450, 700, 1000, 1350, 1750, 2200, 2700,
            3250, 3850, 4500, 5200, 5950, 6750, 7600, 8500, 9450, 10450
        };
        public static final int MAX_LEVEL = 20;
        public static final double HEALTH_PER_LEVEL = 1.0;
        public static final double ATTACK_POWER_PER_LEVEL = 0.1;
    }

    // === Параметры достижений ===
    public static class Achievements {
        public static final int KILLS_BRONZE = 10;
        public static final int KILLS_SILVER = 50;
        public static final int KILLS_GOLD = 100;
        public static final int DAMAGE_BRONZE = 1000;
        public static final int DAMAGE_SILVER = 5000;
        public static final int DAMAGE_GOLD = 10000;
        public static final int GOLD_BRONZE = 100;
        public static final int GOLD_SILVER = 500;
        public static final int GOLD_GOLD = 1000;
        public static final int ROOMS_BRONZE = 5;
        public static final int ROOMS_SILVER = 15;
        public static final int ROOMS_GOLD = 30;
    }

    // === Параметры комнаты и карты ===
    public static class Map {
        public static final int ROOM_COUNT = 10;
        public static final int MIN_ROOMS_PER_FLOOR = 5;
        public static final int MAX_ROOMS_PER_FLOOR = 8;
        public static final double ROOM_WIDTH = 32.0 * 30;
        public static final double ROOM_HEIGHT = 32.0 * 20;
        public static final int TILE_SIZE = 32;
    }

    // === Звуковые параметры ===
    public static class Audio {
        public static final double MASTER_VOLUME = 1.0;
        public static final double MUSIC_VOLUME = 0.03;
        public static final double SFX_VOLUME = 0.1;
        public static final String[] BACKGROUND_TRACKS = {
            "evil1.mp3", "evil2.mp3", "evil3.mp3", "evil4.mp3"
        };
        public static final String MENU_TRACK = "MainMenu.mp3";
    }

    // === Тайминги ===
    public static class Timing {
        public static final double UPDATE_INTERVAL = 1.0 / 60.0; // 60 FPS
        public static final double DAMAGE_FLASH_DURATION = 0.1;
        public static final double INVISIBLE_DURATION = 2.0;
        public static final double STUN_DURATION = 1.0;
        public static final double CHEST_OPEN_DURATION = 0.5;
    }

    // === Строковые константы ===
    public static class Strings {
        public static final String PLAYER_NAME_KEY = "playerName";
        public static final String GAME_DIFFICULTY_KEY = "gameDifficulty";
        public static final String COIN_KEY = "coin";
        public static final String PLAYER_KEY = "player";
        public static final String PLAYER_COMPONENT_KEY = "playerComponent";
        public static final String GAME_MAP_KEY = "gameMap";
        public static final String CURRENT_ROOM_KEY = "curRoom";
        public static final String INTERACTABLE_KEY = "Interactable";
        public static final String WEAPON_KEY = "weapon";
        public static final String DAMAGE_KEY = "damage";
        public static final String WIDTH_KEY = "width";
        public static final String HEIGHT_KEY = "height";
        public static final String DIRECTION_KEY = "dir";
        public static final String ULTIMATE_ACTIVE_KEY = "ultimateActive";
    }
}