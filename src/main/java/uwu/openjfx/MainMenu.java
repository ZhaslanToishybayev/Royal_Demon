package uwu.openjfx;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FXGLScrollPane;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.core.GameEnvironment;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.input.GameInputActions;
import uwu.openjfx.leaderboard.LeaderboardUI;
import uwu.openjfx.weapons.Bow0;
import uwu.openjfx.weapons.GoldenSword1;
import uwu.openjfx.weapons.MagicStaff0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Supplier;

import static com.almasb.fxgl.core.math.FXGLMath.noise1D;
import static com.almasb.fxgl.dsl.FXGL.getSettings;
import static com.almasb.fxgl.dsl.FXGL.loopBGM;
import static com.almasb.fxgl.dsl.FXGLForKtKt.random;

public class MainMenu extends FXGLMenu {

    private final Pane menuRoot = new Pane();
    private final Pane menuContentRoot = new Pane();
    private final MenuContent emptyMenuContent = new MenuContent();
    private final MenuBox menu;
    private final ArrayList<Animation<?>> animations = new ArrayList<>();
    private ParticleSystem particleSystem = new ParticleSystem();

    //private PressAnyKeyState pressAnyKeyState = new PressAnyKeyState();
    private ObjectProperty<Color> titleColor = new SimpleObjectProperty<>();
    private double t = 0.0;

    public MainMenu(@NotNull MenuType type) {
        super(type);

        // Инициализация локализации
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();

        // code to customize the view of your menu

        String title = (type == MenuType.MAIN_MENU)
                ? loc.getString("game.title")
                : loc.getString("game.paused");

        getContentRoot().getChildren().addAll(
                createBackground(getAppWidth(), getAppHeight()),
                //replace with function to get title
                createTitleView(title),
                //replace with function to get version string
                createVersionView(FXGL.getSettings().getVersion()),
                menuRoot,
                menuContentRoot
        );

        menu = (type == MenuType.MAIN_MENU)
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        double menuX = getAppWidth() / 5.0 + 25;
        double menuY = getAppHeight() * 3.0 / 5.0 + menu.getLayoutHeight() / 2.0;

        menuRoot.setTranslateX(menuX);
        menuRoot.setTranslateY(menuY);

        menuContentRoot.setTranslateX(getAppWidth() * 3.0 / 5.0 - 100);
        menuContentRoot.setTranslateY(menuY);

        // particle smoke
        Texture t = FXGL.texture("particles/smoke.png", 128.0, 128.0).brighter().brighter();

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSourceImage(t.getImage());
        emitter.setSize(150.0, 220.0);
        emitter.setNumParticles(10);
        emitter.setEmissionRate(0.01);
        emitter.setVelocityFunction(e -> new Point2D(random() * 2.5, -random() * random(80, 120)));
        emitter.setExpireFunction(e -> Duration.seconds(random(4, 7)));
        emitter.setScaleFunction(e -> new Point2D(0.15, 0.10));
        emitter.setSpawnPointFunction(e -> new Point2D(random(0.0, getAppWidth() - 200.0), 120.0));

        particleSystem.addParticleEmitter(emitter, 0.0, FXGL.getAppHeight());

        getContentRoot().getChildren().add(3, particleSystem.getPane());

        menuRoot.getChildren().addAll(menu);
        menuContentRoot.getChildren().add(emptyMenuContent);
    }

    public static void resetToMainMenu() {
        FXGL.getGameController().gotoMainMenu();
        FXGL.getAudioPlayer().stopAllMusic();
        loopBGM("MainMenu.mp3");

        // Сбрасываем флаг регистрации input действий
        GameInputActions.resetActionsRegistration();

        // Безопасный сброс всех игровых данных
        PlayerComponent.resetAllGameData();

        // Сброс здоровья игрока (если игрок существует)
        Entity player = FXGL.geto("player");
        if (player != null && player.hasComponent(PlayerComponent.class)) {
            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
            playerComp.setHealthPoints(20); // Полное восстановление здоровья (4 сердца)
        }
    }

    @Override
    public void onCreate() {
        animations.clear();

        VBox menuBox = (VBox) menuRoot.getChildren().get(0);

        for (int i = 0; i < menuBox.getChildren().size(); i++) {
            Node node = menuBox.getChildren().get(i);
            node.setTranslateX(-250.0);

            Animation<?> animation = FXGL.animationBuilder()
                    .delay(Duration.seconds(i * 0.07))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .duration(Duration.seconds(0.66))
                    .translate(node)
                    .from(new Point2D(-250.0, 0.0))
                    .to(new Point2D(0.0, 0.0))
                    .build();

            animations.add(animation);

            animation.stop();
            animation.start();
        }
    }

    @Override
    public void onDestroy() {
        // the scene is no longer active so reset everything
        // so that next time scene is active everything is loaded properly
        switchMenuTo(menu);
        switchMenuContentTo(emptyMenuContent);
    }

    @Override
    public void onUpdate(double tpf) {
        for (Animation<?> animation : animations) {
            animation.onUpdate(tpf);
        }

        double frequency = 1.7;

        t += tpf * frequency;

        particleSystem.onUpdate(tpf);

        Color color = Color.color(1.0, 1.0, 1.0, noise1D(t));
        titleColor.set(color);
    }

    private Texture createBackground(double w, double h) {
        Texture pausebg = FXGL.texture("background/background.jpg", w, h);
        if (getType() != MenuType.MAIN_MENU) {
            pausebg.setOpacity(0.25);
        }
        return pausebg;
    }

    private StackPane createTitleView(String title) {
        titleColor = new SimpleObjectProperty<>(Color.WHITE);

        Text text = FXGL.getUIFactoryService().newText(title.substring(0, 1), 60.0);
        text.strokeProperty().bind(titleColor);
        text.setStyle("-fx-fill: transparent;-fx-stroke-width: 1.5");

        Text text2 = FXGL.getUIFactoryService().newText(title.substring(1), 60.0);
        text2.setFill(null);
        text2.setStroke(titleColor.getValue());
        text2.setStrokeWidth(2.5);

        double textWidth = text.getLayoutBounds().getWidth() + text2.getLayoutBounds().getWidth();

        Rectangle border = new Rectangle(textWidth + 30, 65.0, null);
        border.setStyle("-fx-stroke: white;"
                + "-fx-stroke-width: 4.0;-fx-arc-width: 25.0;-fx-arc-height: 25.0");

        ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(50);

        Texture t = FXGL.texture("particles/trace_horizontal.png", 64.0, 64.0);

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(t.getImage());
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setSize(18.0, 22.0);
        emitter.setNumParticles(2);
        emitter.setEmissionRate(0.2);
        emitter.setVelocityFunction(e -> {
            if (e % 2 == 0) {
                return new Point2D(random(-10, 0), random(0, 0));
            } else {
                return new Point2D(random(0, 10), random(0, 0));
            }
        });
        emitter.setExpireFunction(e -> Duration.seconds(random(4, 6)));
        emitter.setScaleFunction(e -> new Point2D(-0.03, -0.03));
        emitter.setSpawnPointFunction(e -> new Point2D(random(0, 0), random(0, 0)));
        emitter.setAccelerationFunction(() -> new Point2D(random(-1, 1), random(0, 0)));

        HBox box = new HBox(text, text2);
        box.setAlignment(Pos.CENTER);

        StackPane titleRoot = new StackPane();
        titleRoot.getChildren().addAll(border, box);

        titleRoot.setTranslateX(getAppWidth() / 2.0 - (textWidth + 30) / 2);
        titleRoot.setTranslateY(100);

        particleSystem = new ParticleSystem();

        particleSystem.addParticleEmitter(emitter, getAppWidth() / 2.0 - 30,
                titleRoot.getTranslateY() + border.getHeight());

        return titleRoot;
    }

    private Text createVersionView(String version) {
        Text view = FXGL.getUIFactoryService().newText(version);
        view.setTranslateY(getAppHeight() - 2);
        return view;
    }

    private void switchMenuTo(Node menu) {
        Node oldMenu = menuRoot.getChildren().get(0);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), oldMenu);
        ft.setToValue(0.0);
        ft.setOnFinished(event -> {
            menu.setOpacity(0.0);
            menuRoot.getChildren().set(0, menu);
            oldMenu.setOpacity(1.0);

            FadeTransition ft2 = new FadeTransition(Duration.seconds(0.33), menu);
            ft2.setToValue(1.0);
            ft2.play();
        });
        ft.play();
    }

    private void switchMenuContentTo(Node content) {
        menuContentRoot.getChildren().set(0, content);
    }

    private MenuBox createMenuBodyMainMenu() {
        MenuBox box = new MenuBox();
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();

        //val enabledItems = FXGL.getSettings().enabledMenuItems;

        MenuButton itemNewGame = new MenuButton(loc.getString("menu.new_game"));
        itemNewGame.setChild(createNewGameMenu());
        box.add(itemNewGame);

        MenuButton itemLeaderboard = new MenuButton("🏆 Таблица лидеров");
        itemLeaderboard.setOnAction(event -> {
            LeaderboardUI leaderboardUI = new LeaderboardUI();
            leaderboardUI.show();
        });
        box.add(itemLeaderboard);

        MenuButton itemOptions = new MenuButton(loc.getString("menu.options"));
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        MenuButton itemExit = new MenuButton(loc.getString("menu.exit"));
        itemExit.setOnAction(event -> fireExit());
        box.add(itemExit);

        return box;
    }

    private MenuBox createMenuBodyGameMenu() {
        MenuBox box = new MenuBox();
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();

        MenuButton itemResume = new MenuButton(loc.getString("menu.resume"));
        itemResume.setOnAction(event -> fireResume());
        box.add(itemResume);

        MenuButton itemOptions = new MenuButton(loc.getString("menu.options"));
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        MenuButton itemMain = new MenuButton(loc.getString("menu.main_menu"));
        itemMain.setOnAction(event -> {
            String text = loc.getString("menu.exit_main_menu") + "\n"
                    + loc.getString("menu.unsaved_progress");

            FXGL.getDialogService().showConfirmationBox(text, yes -> {
                if (yes) {
                    resetToMainMenu();
                }
            });
        });
        box.add(itemMain);

        MenuButton itemExit = new MenuButton(loc.getString("menu.exit"));
        itemExit.setOnAction(event -> fireExit());
        box.add(itemExit);

        /*
        if (enabledItems.contains(MenuItem.SAVE_LOAD)) {
            val itemSave = MenuButton("menu.save")
            itemSave.setOnAction { fireSave() }

            val itemLoad = MenuButton("menu.load")
            itemLoad.setMenuContent({ createContentLoad() }, isCached = false)

            box.add(itemSave)
            box.add(itemLoad)
        }
         */

        return box;
    }

    private MenuBox createOptionsMenu() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemAudio = new MenuButton(loc.getString("menu.volume"));
        itemAudio.setMenuContent(this::createContentAudio, false);

        MenuButton itemLanguage = new MenuButton(loc.getString("menu.language"));
        itemLanguage.setMenuContent(this::createContentLanguage, false);

        MenuButton btnRestore = new MenuButton(loc.getString("menu.reset_settings"));
        String text = loc.getString("menu.settings_restore_confirm");
        btnRestore.setOnAction(e -> FXGL.getDialogService().showConfirmationBox(text, yes -> {
            if (yes) {
                switchMenuContentTo(emptyMenuContent);
                restoreDefaultSettings();
                getSettings().setGlobalMusicVolume(0.25);
            }
        }));

        return new MenuBox(itemAudio, itemLanguage, btnRestore);
    }

    private MenuBox createExtraMenu() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemWho = new MenuButton(loc.getString("menu.who_are_we"));
        itemWho.setMenuContent(this::createContentWho, false);

        MenuButton itemCredits = new MenuButton(loc.getString("menu.credits"));
        itemCredits.setMenuContent(this::createContentCredits, false);

        return new MenuBox(itemWho, itemCredits);
    }

    private MenuContent createContentWho() {

        FXGLScrollPane pane = new FXGLScrollPane();
        pane.setPrefWidth(500.0);
        pane.setPrefHeight(getAppHeight() / 2.0);
        pane.setStyle("-fx-background:black;");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPrefWidth(pane.getPrefWidth() - 15);

        ArrayList<String> credits = new ArrayList<>(Arrays.asList(
                "Alice Wang",
                "",
                "Devan Moses",
                "",
                "James Johnson",
                "",
                "Jason Ng",
                "",
                "Ray Hung"
        ));

        for (String credit : credits) {
            vbox.getChildren().add(FXGL.getUIFactoryService().newText(credit));
        }

        pane.setContent(vbox);

        return new MenuContent(pane);
    }

    private MenuContent createContentCredits() {

        FXGLScrollPane pane = new FXGLScrollPane();
        pane.setPrefWidth(500.0);
        pane.setPrefHeight(getAppHeight() / 2.0);
        pane.setStyle("-fx-background:black;");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPrefWidth(pane.getPrefWidth() - 15);

        ArrayList<String> credits = new ArrayList<>(Arrays.asList(
                "Asset by 0x72, aekae13, superdark, o-lobster from itch.io",
                "0x72.itch.io/dungeontileset-ii",
                "0x72.itch.io/dungeonui",
                "aekae13.itch.io/16x16-dungeon-walls-reconfig",
                "superdark.itch.io/16x16-free-npc-pack",
                "superdark.itch.io/enchanted-forest-characters",
                "o-lobster.itch.io/simple-dungeon-crawler-16x16-pixel-pack",
                "",
                "Powered by FXGL " + FXGL.getVersion(),
                "Author: Almas Baimagambetov",
                "https://github.com/AlmasB/FXGL",
                "",
                "Skill assets by ppeldo & XYEzawr from itch.io"
        ));

        for (String credit : credits) {
            vbox.getChildren().add(FXGL.getUIFactoryService().newText(credit));
        }

        pane.setContent(vbox);

        return new MenuContent(pane);
    }

    private MenuBox createNewGameMenu() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemName = new MenuButton(loc.getString("menu.name"));
        itemName.setMenuContent(this::createContentName, false);

        MenuButton itemDifficulty = new MenuButton(loc.getString("menu.difficulty"));
        itemDifficulty.setMenuContent(this::createContentDifficulty, false);

        MenuButton itemWeapon = new MenuButton(loc.getString("menu.weapon"));
        itemWeapon.setMenuContent(this::createContentWeapon, false);

        MenuButton itemLetsGo = new MenuButton(loc.getString("menu.lets_go"));
        itemLetsGo.setOnAction(e -> {
            if (PlayerComponent.getPlayerName() != null
                    && !PlayerComponent.getPlayerName().isEmpty()
                    && !PlayerComponent.getPlayerName().trim().isEmpty()
                    && PlayerComponent.getCurrentWeapon() != null
                    && PlayerComponent.getGameDifficulty() != null) {
                FXGL.getDialogService().showConfirmationBox(loc.getString("menu.start_game_confirm"), yes -> {
                    if (yes) {
                        fireNewGame();
                    }
                });
            } else {
                FXGL.getDialogService().showMessageBox(loc.getString("menu.setup_required"));
            }
        });

        return new MenuBox(itemName, itemDifficulty, itemWeapon, itemLetsGo);
    }

    protected MenuContent createContentName() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemName = new MenuButton(loc.getString("menu.click_me"));
        itemName.setOnAction(e -> FXGL.getDialogService().showInputBoxWithCancel(
                loc.getString("menu.enter_name"),
            t -> {
                return !(t == null || t.isEmpty()
                        || t.trim().isEmpty());
            },
            s -> {
                PlayerComponent.setPlayerName(s);
                itemName.updateText(PlayerComponent.getPlayerName());
            }));

        HBox hboxName = new HBox(15.0, itemName);

        hboxName.setAlignment(Pos.CENTER_RIGHT);

        return new MenuContent(hboxName);
    }

    protected MenuContent createContentDifficulty() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemEasy = new MenuButton(loc.getString("menu.easy"));
        itemEasy.setOnAction(
            e -> {
                String difficultyName = loc.getString("menu.easy");
                PlayerComponent.setGameDifficulty(difficultyName);
                GameEnvironment.get().getDifficultyService().setActiveDifficulty("easy");
                PlayerComponent.setGold(0);
                itemEasy.updateText(difficultyName);
            });

        MenuButton itemMedium = new MenuButton(loc.getString("menu.medium"));
        itemMedium.setOnAction(
            e -> {
                String difficultyName = loc.getString("menu.medium");
                PlayerComponent.setGameDifficulty(difficultyName);
                GameEnvironment.get().getDifficultyService().setActiveDifficulty("normal");
                PlayerComponent.setGold(0);
                itemMedium.updateText(difficultyName);
            });

        MenuButton itemHard = new MenuButton(loc.getString("menu.hard"));
        itemHard.setOnAction(
            e -> {
                String difficultyName = loc.getString("menu.hard");
                PlayerComponent.setGameDifficulty(difficultyName);
                GameEnvironment.get().getDifficultyService().setActiveDifficulty("hard");
                PlayerComponent.setGold(0);
                itemHard.updateText(difficultyName);
            });

        return new MenuContent(itemEasy, itemMedium, itemHard);
    }

    protected MenuContent createContentWeapon() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
        
        MenuButton itemSword = new MenuButton(loc.getString("menu.sword"));
        itemSword.setOnAction(
            e -> {
                GoldenSword1 goldenSword1 = new GoldenSword1();
                PlayerComponent.setCurrentWeapon(goldenSword1);
                PlayerComponent.addWeaponToInventory(goldenSword1);
                itemSword.updateText(loc.getString("menu.sword"));
            });

        MenuButton itemWand = new MenuButton(loc.getString("menu.wand"));
        itemWand.setOnAction(
            e -> {
                MagicStaff0 magicStaff0 = new MagicStaff0();
                PlayerComponent.setCurrentWeapon(magicStaff0);
                PlayerComponent.addWeaponToInventory(magicStaff0);
                itemWand.updateText(loc.getString("menu.wand"));
            });

        MenuButton itemBow = new MenuButton(loc.getString("menu.bow"));
        itemBow.setOnAction(
            e -> {
                Bow0 bow0 = new Bow0();
                PlayerComponent.setCurrentWeapon(bow0);
                PlayerComponent.addWeaponToInventory(bow0);
                itemBow.updateText(loc.getString("menu.bow"));
            });

        return new MenuContent(itemSword, itemWand, itemBow);
    }

    /**
     * @return menu content containing music and sound volume sliders
     */
    protected MenuContent createContentAudio() {
        Slider sliderMusic = new Slider(0.0, 1.0, 1.0);
        sliderMusic.valueProperty().bindBidirectional(FXGL.getSettings()
                .globalMusicVolumeProperty());

        Text textMusic = FXGL.getUIFactoryService().newText(
                FXGL.localizedStringProperty("menu.music.volume").concat(": "));

        Text percentMusic = FXGL.getUIFactoryService().newText("");

        percentMusic.textProperty().bind(sliderMusic.valueProperty()
                .multiply(100).asString("%.0f"));

        Slider sliderSound = new Slider(0.0, 1.0, 1.0);

        sliderSound.valueProperty().bindBidirectional(FXGL.getSettings()
                .globalSoundVolumeProperty());

        Text textSound = FXGL.getUIFactoryService().newText(
                FXGL.localizedStringProperty("menu.sound.volume").concat(": "));

        Text percentSound = FXGL.getUIFactoryService().newText("");

        percentSound.textProperty().bind(sliderSound.valueProperty()
                .multiply(100).asString("%.0f"));

        HBox hboxMusic = new HBox(15.0, textMusic, sliderMusic, percentMusic);
        HBox hboxSound = new HBox(15.0, textSound, sliderSound, percentSound);

        hboxMusic.setAlignment(Pos.CENTER_RIGHT);
        hboxSound.setAlignment(Pos.CENTER_RIGHT);

        return new MenuContent(hboxMusic, hboxSound);
    }

    protected MenuContent createContentLanguage() {
        EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();

        MenuButton itemRussian = new MenuButton("Русский");
        itemRussian.setOnAction(e -> {
            loc.setLocale(new Locale("ru"));
            FXGL.getGameController().gotoMainMenu(); // Refresh menu
        });

        MenuButton itemEnglish = new MenuButton("English");
        itemEnglish.setOnAction(e -> {
            loc.setLocale(new Locale("en"));
            FXGL.getGameController().gotoMainMenu(); // Refresh menu
        });

        return new MenuContent(itemRussian, itemEnglish);
    }

    private class MenuBox extends VBox {

        private final double layoutHeight = 6.0 * getChildren().size();

        public MenuBox(MenuButton... items) {
            for (MenuButton item : items) {
                getChildren().add(item);
            }
        }

        public double getLayoutHeight() {
            return layoutHeight;
        }

        public void add(MenuButton item) {
            item.setParent(this);
            getChildren().addAll(item);
        }
    }

    private class MenuContent extends VBox {

        private Runnable onOpen;
        private Runnable onClose;

        private int maxW;

        public MenuContent(Node... items) {
            if (Arrays.stream(items).toArray().length > 0) {
                maxW = (int) (items[0].getLayoutBounds().getWidth());

                for (Node n : items) {
                    int w = (int) (n.getLayoutBounds().getWidth());
                    if (w > maxW) {
                        maxW = w;
                    }
                }

                for (Node item : items) {
                    getChildren().addAll(item);
                }
            }

            sceneProperty().addListener((a, b, newScene) -> {
                if (newScene != null) {
                    onOpen();
                } else {
                    onClose();
                }
            });
        }

        /**
         * Set on open handler.
         *
         * @param onOpenAction method to be called when content opens
         */
        private void setOnOpen(Runnable onOpenAction) {
            this.onOpen = onOpenAction;
        }

        /**
         * Set on close handler.
         *
         * @param onCloseAction method to be called when content closes
         */
        private void setOnClose(Runnable onCloseAction) {
            this.onClose = onCloseAction;
        }

        private void onOpen() {
            if (onOpen != null) {
                onOpen.run();
            }
        }

        private void onClose() {
            if (onClose != null) {
                onClose.run();
            }
        }
    }

    private class MenuButton extends Pane {
        private final Button btn;
        private MenuBox parent;
        private MenuContent cachedContent;
        private String text;
        private boolean isAnimating = false;

        public MenuButton(String stringKey) {
            text = stringKey;
            btn = new Button(text);
            btn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: rgba(255, 215, 0, 0.8);" +
                        "-fx-border-width: 2px;");
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setMinSize(250, 50);
            btn.setPrefSize(250, 50);
            btn.setOnMouseEntered(event -> {
                btn.setStyle("-fx-background-color: rgba(255, 215, 0, 0.8);" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 3px;");
                FXGL.play("ui/ui_hover.wav");
            });
            btn.setOnMouseExited(event -> {
                btn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: rgba(255, 215, 0, 0.8);" +
                            "-fx-border-width: 2px;");
            });
            btn.setOnMouseClicked(event -> {
                FXGL.play("ui/ui_confirm.wav");
            });

            // Decorative polygon (always visible but subtle)
            Polygon p = new Polygon(0.0, 0.0, 220.0, 0.0, 250.0, 35.0, 0.0, 35.0);
            p.setMouseTransparent(true);

            LinearGradient g = new LinearGradient(0.0, 1.0, 1.0, 0.2, true, CycleMethod.NO_CYCLE,
                    new Stop(0.6, Color.color(1.0, 0.8, 0.0, 0.15)),
                    new Stop(0.85, Color.color(1.0, 0.8, 0.0, 0.35)),
                    new Stop(1.0, Color.color(1.0, 1.0, 0.0, 0.5)));

            p.fillProperty().bind(
                    Bindings.when(btn.pressedProperty())
                            .then((Paint) Color.color(1.0, 0.8, 0.0, 0.5))
                            .otherwise(g)
            );

            p.setStroke(Color.color(1.0, 0.9, 0.5, 0.8));
            p.setEffect(new GaussianBlur());

            // Always visible decorative background
            p.setVisible(true);

            getChildren().addAll(btn, p);

            btn.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    boolean isOK = !isAnimating;
                    // original in kt but idk what it means in java
                    //boolean isOK = animations.none(it.isAnimating) && !isAnimating;
                    for (Animation<?> animation : animations) {
                        if (animation.isAnimating()) {
                            isOK = false;
                            break;
                        }
                    }
                    if (isOK) {
                        isAnimating = true;

                        FXGL.animationBuilder()
                                .onFinished(() -> isAnimating = false)
                                .bobbleDown(this)
                                .buildAndPlay(MainMenu.this);
                    }
                }
            });
        }

        public void updateText(String newText) {
            text = newText;
            btn.setText(text);
        }

        public String getText() {
            return text;
        }

        public void setOnAction(EventHandler<ActionEvent> e) {
            btn.setOnAction(e);
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(Supplier<MenuContent> contentSupplier, Boolean isCached) {

            btn.addEventHandler(ActionEvent.ACTION, e -> {
                if (cachedContent == null || !isCached) {
                    cachedContent = contentSupplier.get();
                }

                switchMenuContentTo(cachedContent);
            });
        }

        public void setChild(MenuBox menu) {
            EnhancedLocalizationManager loc = EnhancedLocalizationManager.getInstance();
            MenuButton back = new MenuButton(loc.getString("menu.back"));
            menu.getChildren().add(back);

            back.addEventHandler(ActionEvent.ACTION, e -> {
                switchMenuTo(parent);
                switchMenuContentTo(emptyMenuContent);
            });

            btn.addEventHandler(ActionEvent.ACTION, e -> switchMenuTo(menu));
        }
    }
}