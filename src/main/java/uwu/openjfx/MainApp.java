package uwu.openjfx;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uwu.openjfx.map.GameMap;
import uwu.openjfx.behaviors.Interactable;
import uwu.openjfx.collision.*;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.events.InteractEvent;
import uwu.openjfx.input.*;
import uwu.openjfx.items.Heart;
import uwu.openjfx.items.Item;
import uwu.openjfx.core.*;
import uwu.openjfx.core.modules.CoreModule;
import uwu.openjfx.core.modules.AssetModule;
import uwu.openjfx.weapons.*;
import uwu.openjfx.integration.GameIntegration;
import uwu.openjfx.i18n.LocalizationManager;
import uwu.openjfx.hud.GameHUD;
import uwu.openjfx.utils.GameLogger;

import java.io.File;
import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class MainApp extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;
    private GameMap gameMap;
    private List<String> normalMinionList;
    private List<String> forestMinionList;
    private List<String> miniBossList;
    private List<String> roomTypeList;
    private Set<String> weaponsSet;
    private Map<String, String> itemNameAssetMap;
    private Map<String, Object> itemNameObjMap;
    private final Boolean developerCheat = false;

    // Top priority : (


    // Tier 2 priority
    // Todo: Boss special attack??
    // Todo: Alice try making some roomsg


    // Tier 3 priority : )
    // Todo: Jason makes more weapons / special effects
    // Todo: Devan more sound effects
    // Todo: Ray more traps, lever
    // Todo: Ray open and close door
    // Todo: Ray tile animation for waterfall
    // Todo: make more rooms
    // Todo: Devan UI - heart/health (more polished)

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(960);
        settings.setHeight(640);
        settings.setTitle("Royal Demons");
        settings.setVersion("0.1");
        settings.setAppIcon("ui/weapon_box_ui.png");
        settings.setFontUI("ThaleahFat.ttf");
        settings.setMainMenuEnabled(true);
        // Always use custom menu
        settings.setSceneFactory(new MainMenuSceneFactory());
        settings.setGameMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        //settings.setDeveloperMenuEnabled(true);
        //settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }


    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.03);
        getSettings().setGlobalSoundVolume(0.1);
        loopBGM("MainMenu.mp3");

        getEventBus().addEventHandler(InteractEvent.ANY, event -> {
            Interactable interactable = event.getEntity().getObject("Interactable");
            interactable.interact();
        });
    }

    @Override
    protected void initInput() {
        if (developerCheat) {
            getInput().addAction(new KillAllEnemy("KillAll"), KeyCode.K);
            getInput().addAction(new TeleportToBossRoom("TeleportToBossRoom"), KeyCode.B);
            UI.ragePotProperty().set(5);
        }

    }

    @Override
    protected void initGame() {
        // Инициализируем модульную архитектуру
        initializeModules();

        // Инициализация систем улучшений (совместимость)
        try {
            Class<?> gameIntegrationClass = Class.forName("uwu.openjfx.integration.GameIntegration");
            java.lang.reflect.Method initMethod = gameIntegrationClass.getMethod("initialize");
            initMethod.invoke(null);
            GameLogger.system("Legacy systems initialized");
        } catch (ClassNotFoundException e) {
            GameLogger.debug("GameIntegration class not found - skipping legacy systems");
        } catch (NoSuchMethodException e) {
            GameLogger.warn("GameIntegration.initialize() method not found - skipping legacy systems");
        } catch (Exception e) {
            GameLogger.error("Failed to initialize legacy systems", e);
        }

        if (developerCheat) {
            int i = 9;
            switch (i) {
            case 0:
                GoldenSword0 goldenSword0 = new GoldenSword0();
                PlayerComponent.setCurrentWeapon(goldenSword0);
                PlayerComponent.addWeaponToInventory(goldenSword0);
                PlayerComponent.setGold(1000);
                break;
            case 1:
                GoldenSword1 goldenSword1 = new GoldenSword1();
                PlayerComponent.setCurrentWeapon(goldenSword1);
                PlayerComponent.addWeaponToInventory(goldenSword1);
                PlayerComponent.setGold(1000);
                break;
            case 2:
                GoldenSword2 goldenSword2 = new GoldenSword2();
                PlayerComponent.setCurrentWeapon(goldenSword2);
                PlayerComponent.addWeaponToInventory(goldenSword2);
                PlayerComponent.setGold(1000);
                break;
            case 3:
                Bow0 bow0 = new Bow0();
                PlayerComponent.setCurrentWeapon(bow0);
                PlayerComponent.addWeaponToInventory(bow0);
                break;
            case 4:
                Bow1 bow1 = new Bow1();
                PlayerComponent.setCurrentWeapon(bow1);
                PlayerComponent.addWeaponToInventory(bow1);
                break;
            case 5:
                Bow2 bow2 = new Bow2();
                PlayerComponent.setCurrentWeapon(bow2);
                PlayerComponent.addWeaponToInventory(bow2);
                break;
            case 6:
                MagicStaff0 magicStaff0 = new MagicStaff0();
                PlayerComponent.setCurrentWeapon(magicStaff0);
                PlayerComponent.addWeaponToInventory(magicStaff0);
                break;
            case 7:
                MagicStaff1 magicStaff1 = new MagicStaff1();
                PlayerComponent.setCurrentWeapon(magicStaff1);
                PlayerComponent.addWeaponToInventory(magicStaff1);
                break;
            case 8:
                MagicStaff2 magicStaff2 = new MagicStaff2();
                PlayerComponent.setCurrentWeapon(magicStaff2);
                PlayerComponent.addWeaponToInventory(magicStaff2);
                break;
            case 9:
                HeavySword heavySword = new HeavySword();
                PlayerComponent.setCurrentWeapon(heavySword);
                PlayerComponent.addWeaponToInventory(heavySword);
                break;
            default:
            }
        }
        // Initialize Epressed to false. During the time player press E, this will
        // become true
        set("Epressed", false);
        set("Fpressed", false);
        set("developerCheat", developerCheat);
        set("coin", 0);

        // Используем AssetModule вместо прямого обращения к файлам
        loadAssetsThroughModule();
        gameMap = new GameMap(10);
        // gameMap.setRandomSeed(63);
        gameMap.generateRooms();
        set("gameMap", gameMap);
        set("curRoom", gameMap.getInitialRoom());

        getGameWorld().addEntityFactory(new StructureFactory());
        getGameWorld().addEntityFactory(new CreatureFactory());
        getGameWorld().addEntityFactory(new WeaponFactory());
        getGameScene().setBackgroundColor(Color.BLACK);

        getAudioPlayer().stopMusic(FXGL.getAssetLoader().loadMusic("MainMenu.mp3"));
        loopBGM("evil4.mp3");
        player = spawn("player", 0, 0);
        set("player", player);
        playerComponent = player.getComponent(PlayerComponent.class);
        set("playerComponent", playerComponent);
        if (developerCheat) {
            player.getComponent(PlayerComponent.class).setHealthPoints(200);
            player.getComponent(PlayerComponent.class).setMaxHealthPoints(999);
            player.getComponent(PlayerComponent.class).setSpeed(300);
        }

        gameMap.loadRoom(gameMap.getInitialRoom(), "center");

        // Регистрируем все игровые действия через рефакторенный input класс
        GameInputActions.registerAllActions(player);

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-32 * 5, -getAppHeight(), 32 * 70, 32 * 70);
        viewport.bindToEntity(player, getAppWidth() / 2.0, getAppHeight() / 2.0);
        viewport.setLazy(true);

    }

    /**
     * Инициализировать модульную архитектуру
     */
    private void initializeModules() {
        try {
            GameLogger.system("=== Initializing Modular Architecture ===");

            // Регистрируем базовые модули
            ModuleManager moduleManager = ModuleManager.getInstance();
            moduleManager.registerModule(new CoreModule());
            moduleManager.registerModule(new AssetModule());

            // Инициализируем все модули
            moduleManager.initializeAll();

            GameLogger.system("=== Modular Architecture Initialized Successfully ===");
            GameLogger.system("Active modules: " + moduleManager.getActiveModules().size());

        } catch (Exception e) {
            GameLogger.error("Failed to initialize modules", e);
            throw new RuntimeException("Module initialization failed", e);
        }
    }

    /**
     * Загрузка ресурсов через модульную архитектуру (кроссплатформенно)
     */
    private void loadAssetsThroughModule() {
        try {
            AssetModule assetModule = (AssetModule) ModuleManager.getInstance().getModule("Assets");
            if (assetModule == null) {
                GameLogger.warn("AssetModule not found, falling back to old method");
                loadRoomAsset();
                loadEnemiesAsset();
                initItemsNameAssetMappingAndWeaponsList();
                return;
            }

            GameLogger.info("Loading assets through AssetModule...");

            // Загружаем через модуль (кроссплатформенно)
            normalMinionList = assetModule.getNormalMinions();
            forestMinionList = assetModule.getForestMinions();
            miniBossList = assetModule.getMiniBosses();
            roomTypeList = assetModule.getRoomTypes();

            set("normalMinionList", normalMinionList);
            set("forestMinionList", forestMinionList);
            set("miniBossList", miniBossList);
            set("roomTypeList", roomTypeList);

            // Загружаем оружие и предметы (старый способ для совместимости)
            initItemsNameAssetMappingAndWeaponsList();

            GameLogger.info("Assets loaded through AssetModule");

        } catch (Exception e) {
            GameLogger.error("Failed to load assets through module", e);
            // Fallback к старому способу
            loadRoomAsset();
            loadEnemiesAsset();
            initItemsNameAssetMappingAndWeaponsList();
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("pixelsMoved", 0);
        vars.put("developerCheat", developerCheat);
    }


    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().setGravity(0, 0);
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerEnemyCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerAttackEnemyCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new EnemyAttackPlayerCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new EnemyProjDoorCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new EnemyProjWallCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerProjWallCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerProjDoorCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerTriggerCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerDoorCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerCoinCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerDroppedItemCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerChestCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerNPCCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerFinalDoorCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerSpikeCollisionHandler());
        FXGL.getPhysicsWorld().addCollisionHandler(new PlayerSmashedGroundCollisionHandler());
    }

    @Override
    protected void initUI() {
        new GameHUD(player).initUI();
    }


    public void loadEnemiesAsset() {
        normalMinionList = new ArrayList<>();
        File dir = new File("src/main/resources/assets/textures/creatures/minions/normal");
        File[] normalFiles = dir.listFiles();
        if (normalFiles != null) {
            for (File file : normalFiles) {
                if (file.getName().endsWith(".png")) {
                    normalMinionList.add(file.getName());
                }
            }
        }
        set("normalMinionList", normalMinionList);

        forestMinionList = new ArrayList<>();
        dir = new File("src/main/resources/assets/textures/creatures/minions/forest");
        File[] forestFiles = dir.listFiles();
        if (forestFiles != null) {
            for (File file : forestFiles) {
                if (file.getName().endsWith(".png")) {
                    forestMinionList.add(file.getName());
                }
            }
        }
        set("forestMinionList", forestMinionList);

        miniBossList = new ArrayList<>();
        dir = new File("src/main/resources/assets/textures/creatures/miniBoss");
        File[] bossFiles = dir.listFiles();
        if (bossFiles != null) {
            for (File file : bossFiles) {
                if (file.getName().endsWith(".png")) {
                    miniBossList.add(file.getName());
                }
            }
        }
        set("miniBossList", miniBossList);

    }

    public void initItemsNameAssetMappingAndWeaponsList() {
        itemNameAssetMap = new HashMap<>();
        itemNameAssetMap.put("HealthPotion", "items/healthPotion.png");
        itemNameAssetMap.put("RagePotion", "items/ragePotion.png");
        itemNameAssetMap.put("Heart", "items/ui_heart_full_32x32.png");
        itemNameAssetMap.put("Heavy Sword", "ui/inventory/sword2.png");

        itemNameObjMap = new HashMap<>();
        itemNameObjMap.put("Heart", new Heart("Heart", 3));
        itemNameObjMap.put("Heavy Sword", new HeavySword());


        weaponsSet = new HashSet<>();
        File dir = new File("src/main/resources/assets/textures/ui/inventory/");
        File[] inventoryFiles = dir.listFiles();
        if (inventoryFiles != null) {
            for (File file : inventoryFiles) {
                if (file.getName().endsWith(".png")) {
                    weaponsSet.add(file.getName().replace(".png", ""));
                    itemNameAssetMap.put(file.getName().replace(".png", ""),
                            "ui/inventory/" + file.getName());
                }
            }
        }

        set("weaponsSet", weaponsSet);
        set("itemsNameAssetMap", itemNameAssetMap);
        set("itemNameObjMap", itemNameObjMap);
    }

    public void loadRoomAsset() {
        roomTypeList = new ArrayList<>();
        File dir = new File("src/main/resources/assets/levels/tmx");
        File[] roomFiles = dir.listFiles();
        if (roomFiles != null) {
            for (File file : roomFiles) {
                if (file.getName().endsWith(".tmx") && !file.getName().equals("initialRoom.tmx")
                        && !file.getName().equals("bossRoom.tmx")
                        && !file.getName().equals("challengeRoom.tmx")
                        && !file.getName().equals("finalWinRoom.tmx")
                        && !file.getName().equals("weaponsShowcaseRoom.tmx")) {
                    roomTypeList.add(file.getName().replaceAll(".tmx", ""));
                }
            }
        }
        set("roomTypeList", roomTypeList);
    }

    public static boolean isIsTesting() {
        return GameEnvironment.get().isTesting();
    }

    public static void setIsTesting(boolean isTesting) {
        GameEnvironment.get().setTesting(isTesting);
    }

    public static Random getRandom() {
        return GameEnvironment.get().getRandom();
    }

    public static void main(String[] args) {
        launch(args);
    }
}