package uwu.openjfx.cutscenes;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер для управления кат-сценами в игре
 */
public class CutsceneManager {

    private static CutsceneManager instance;
    private final Map<String, Cutscene> cutscenes;
    private Cutscene currentCutscene;
    private StackPane cutsceneLayer;
    private boolean isInitialized = false;

    private CutsceneManager() {
        this.cutscenes = new HashMap<>();
        initialize();
    }

    public static CutsceneManager getInstance() {
        if (instance == null) {
            instance = new CutsceneManager();
        }
        return instance;
    }

    /**
     * Инициализирует менеджер кат-сцен
     */
    private void initialize() {
        if (isInitialized) return;

        // Создаем слой для кат-сцен
        cutsceneLayer = new StackPane();
        cutsceneLayer.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        cutsceneLayer.setMouseTransparent(false);

        // Добавляем слой на игровую сцену
        FXGL.getGameScene().addUINode(cutsceneLayer);
        cutsceneLayer.setVisible(false);

        // Настраиваем обработчики клавиш
        setupInputHandlers();

        isInitialized = true;
    }

    /**
     * Настраивает обработчики ввода для кат-сцен
     */
    private void setupInputHandlers() {
        // Упрощенная версия - обработчик будет добавлен через систему меню
        // Полноценная реализация может потребовать интеграции с игровой системой ввода
    }

    /**
     * Обрабатывает ввод во время кат-сцены (вызывается извне)
     */
    public void handleInput(javafx.scene.input.KeyCode keyCode) {
        if (currentCutscene == null || !currentCutscene.isPlaying()) return;

        if (keyCode == javafx.scene.input.KeyCode.SPACE || keyCode == javafx.scene.input.KeyCode.ENTER) {
            // Попытка продолжить кат-сцену
            if (currentCutscene instanceof IntroCutscene) {
                IntroCutscene intro = (IntroCutscene) currentCutscene;
                if (intro.canContinue()) {
                    intro.handleContinue();
                }
            }
        } else if (keyCode == javafx.scene.input.KeyCode.ESCAPE) {
            // Попытка пропустить кат-сцену
            if (currentCutscene.isSkippable()) {
                currentCutscene.skip();
                currentCutscene = null;
                hideCutsceneLayer();
            }
        }
    }

    /**
     * Регистрирует новую кат-сцену
     */
    public void registerCutscene(String name, Cutscene cutscene) {
        cutscenes.put(name, cutscene);
    }

    /**
     * Воспроизводит кат-сцену по имени
     */
    public void playCutscene(String name, CutsceneCallback callback) {
        Cutscene cutscene = cutscenes.get(name);
        if (cutscene == null) {
            System.err.println("Cutscene not found: " + name);
            if (callback != null) {
                callback.onComplete();
            }
            return;
        }

        playCutscene(cutscene, callback);
    }

    /**
     * Воспроизводит указанную кат-сцену
     */
    public void playCutscene(Cutscene cutscene, CutsceneCallback callback) {
        if (currentCutscene != null && currentCutscene.isPlaying()) {
            System.err.println("Another cutscene is already playing");
            if (callback != null) {
                callback.onComplete();
            }
            return;
        }

        currentCutscene = cutscene;
        showCutsceneLayer();

        // Очищаем слой и добавляем кат-сцену
        cutsceneLayer.getChildren().clear();
        cutsceneLayer.getChildren().add(cutscene.getNode());

        // Воспроизводим кат-сцену
        cutscene.play(() -> {
            currentCutscene = null;
            hideCutsceneLayer();
            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    /**
     * Останавливает текущую кат-сцену
     */
    public void stopCurrentCutscene() {
        if (currentCutscene != null && currentCutscene.isPlaying()) {
            currentCutscene.stop();
            currentCutscene = null;
            hideCutsceneLayer();
        }
    }

    /**
     * Пропускает текущую кат-сцену
     */
    public void skipCurrentCutscene() {
        if (currentCutscene != null && currentCutscene.isPlaying() && currentCutscene.isSkippable()) {
            currentCutscene.skip();
            currentCutscene = null;
            hideCutsceneLayer();
        }
    }

    /**
     * Проверяет, воспроизводится ли кат-сцена
     */
    public boolean isPlaying() {
        return currentCutscene != null && currentCutscene.isPlaying();
    }

    /**
     * Получает имя текущей кат-сцены
     */
    public String getCurrentCutsceneName() {
        if (currentCutscene == null) return null;

        for (Map.Entry<String, Cutscene> entry : cutscenes.entrySet()) {
            if (entry.getValue() == currentCutscene) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Показывает слой кат-сцен
     */
    private void showCutsceneLayer() {
        cutsceneLayer.setVisible(true);
        cutsceneLayer.setMouseTransparent(false);
    }

    /**
     * Скрывает слой кат-сцен
     */
    private void hideCutsceneLayer() {
        cutsceneLayer.setVisible(false);
        cutsceneLayer.setMouseTransparent(true);
        cutsceneLayer.getChildren().clear();
    }

    /**
     * Создает и регистрирует стандартные кат-сцены
     */
    public void createStandardCutscenes() {
        // Вводная кат-сцена
        registerCutscene("intro", new IntroCutscene());

        // Можно добавить другие стандартные кат-сцены здесь
        // registerCutscene("victory", new VictoryCutscene());
        // registerCutscene("defeat", new DefeatCutscene());
    }

    /**
     * Воспроизводит вводную кат-сцену
     */
    public void playIntro(CutsceneCallback callback) {
        playCutscene("intro", callback);
    }

    /**
     * Очищает все ресурсы
     */
    public void cleanup() {
        if (currentCutscene != null) {
            currentCutscene.stop();
            currentCutscene = null;
        }

        cutscenes.clear();

        if (cutsceneLayer != null) {
            FXGL.getGameScene().removeUINode(cutsceneLayer);
            cutsceneLayer = null;
        }

        isInitialized = false;
    }

    /**
     * Интерфейс обратного вызова для завершения кат-сцены
     */
    @FunctionalInterface
    public interface CutsceneCallback {
        void onComplete();
    }
}