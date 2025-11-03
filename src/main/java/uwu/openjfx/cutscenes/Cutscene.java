package uwu.openjfx.cutscenes;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uwu.openjfx.i18n.EnhancedLocalizationManager;

/**
 * Базовый класс для кат-сцен
 */
public abstract class Cutscene {

    protected final EnhancedLocalizationManager localization;
    protected final StackPane container;
    protected final Pane content;
    protected final Rectangle fadeOverlay;

    // Состояние кат-сцены
    protected boolean isPlaying = false;
    protected boolean isSkippable = true;
    protected CutsceneCallback onComplete;

    public Cutscene() {
        this.localization = EnhancedLocalizationManager.getInstance();

        // Создаем основной контейнер
        container = new StackPane();
        container.setPrefSize(1280, 720); // Стандартный размер
        container.setStyle("-fx-background-color: black;");

        // Создаем контейнер для контента
        content = new Pane();
        content.setPrefSize(1280, 720);

        // Создаем оверлей для затемнения
        fadeOverlay = new Rectangle(1280, 720, Color.BLACK);
        fadeOverlay.setOpacity(0.0);

        // Собираем все вместе
        container.getChildren().addAll(content, fadeOverlay);
    }

    /**
     * Запускает кат-сцену
     */
    public final void play(CutsceneCallback onComplete) {
        if (isPlaying) return;

        this.onComplete = onComplete;
        this.isPlaying = true;

        // Начальное затемнение
        fadeIn(() -> {
            onStart();
            updateContent();
        });
    }

    /**
     * Пропускает кат-сцену
     */
    public final void skip() {
        if (!isPlaying || !isSkippable) return;

        skipAnimation();
    }

    /**
     * Останавливает кат-сцену
     */
    public final void stop() {
        if (!isPlaying) return;

        fadeOut(() -> {
            isPlaying = false;
            cleanup();
            if (onComplete != null) {
                onComplete.onComplete();
            }
        });
    }

    /**
     * Получает корневой узел кат-сцены
     */
    public Node getNode() {
        return container;
    }

    /**
     * Устанавливает возможность пропуска
     */
    public void setSkippable(boolean skippable) {
        this.isSkippable = skippable;
    }

    // Абстрактные методы для реализации в конкретных кат-сценах

    /**
     * Вызывается при запуске кат-сцены
     */
    protected abstract void onStart();

    /**
     * Обновляет контент кат-сцены
     */
    protected abstract void updateContent();

    /**
     * Вызывается при очистке ресурсов
     */
    protected abstract void cleanup();

    /**
     * Вызывается при пропуске кат-сцены
     */
    protected void skipAnimation() {
        stop();
    }

    // Вспомогательные методы для анимаций

    /**
     * Затемнение (появление)
     */
    protected void fadeIn(Runnable onComplete) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.0), fadeOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        fadeIn.play();
    }

    /**
     * Затемнение (исчезновение)
     */
    protected void fadeOut(Runnable onComplete) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.0), fadeOverlay);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        fadeOut.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        fadeOut.play();
    }

    /**
     * Создает простую анимацию движения
     */
    protected void animateNode(Node node, double fromX, double fromY, double toX, double toY,
                              Duration duration, Runnable onComplete) {
        node.setLayoutX(fromX);
        node.setLayoutY(fromY);

        TranslateTransition moveX = new TranslateTransition(duration, node);
        moveX.setByX(toX - fromX);

        TranslateTransition moveY = new TranslateTransition(duration, node);
        moveY.setByY(toY - fromY);

        ParallelTransition parallel = new ParallelTransition(moveX, moveY);
        if (onComplete != null) {
            parallel.setOnFinished(e -> onComplete.run());
        }
        parallel.play();
    }

    /**
     * Создает анимацию появления текста
     */
    protected void animateText(Node text, Duration duration, Runnable onComplete) {
        text.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(duration, text);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        if (onComplete != null) {
            fadeIn.setOnFinished(e -> onComplete.run());
        }
        fadeIn.play();
    }

    /**
     * Интерфейс обратного вызова для завершения кат-сцены
     */
    @FunctionalInterface
    public interface CutsceneCallback {
        void onComplete();
    }

    /**
     * Проверяет, проигрывается ли кат-сцена
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Проверяет, можно ли пропустить кат-сцену
     */
    public boolean isSkippable() {
        return isSkippable;
    }
}