package uwu.openjfx.cutscenes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uwu.openjfx.ui.LocalizedText;

/**
 * Вводная кат-сцена игры
 */
public class IntroCutscene extends Cutscene {

    private static final Duration[] TIMINGS = {
        Duration.seconds(2.0),  // Пауза перед появлением текста
        Duration.seconds(3.0),  // Длительность отображения первой части
        Duration.seconds(1.0),  // Пауза
        Duration.seconds(3.0),  // Длительность отображения второй части
        Duration.seconds(1.0),  // Пауза
        Duration.seconds(3.0),  // Длительность отображения третьей части
        Duration.seconds(2.0)   // Финальная пауза
    };

    private LocalizedText titleText;
    private LocalizedText subtitleText;
    private LocalizedText storyText;
    private LocalizedText continueText;
    private VBox textBox;

    private int currentStep = 0;
    private boolean canContinue = false;

    public IntroCutscene() {
        super();
        setupContent();
    }

    @Override
    protected void onStart() {
        currentStep = 0;
        canContinue = false;
        continueText.setOpacity(0.0);
        playIntroSequence();
    }

    @Override
    protected void updateContent() {
        // Контент обновляется автоматически через анимации
    }

    @Override
    protected void cleanup() {
        content.getChildren().clear();
        currentStep = 0;
        canContinue = false;
    }

    @Override
    protected void skipAnimation() {
        // При пропуске сразу переходим к завершению
        cleanup();
        if (onComplete != null) {
            onComplete.onComplete();
        }
    }

    /**
     * Настраивает контент кат-сцены
     */
    private void setupContent() {
        // Создаем текстовые элементы
        titleText = new LocalizedText("cutscene.intro.title", Color.GOLD, 48.0);
        titleText.setFont(Font.font("Arial Bold", 48.0));
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2.0);

        subtitleText = new LocalizedText("cutscene.intro.subtitle", Color.LIGHTGRAY, 32.0);
        subtitleText.setFont(Font.font("Arial Italic", 32.0));
        subtitleText.setStroke(Color.BLACK);
        subtitleText.setStrokeWidth(1.0);

        storyText = new LocalizedText("cutscene.intro.story", Color.WHITE, 20.0);
        storyText.setFont(Font.font("Arial", 20.0));
        storyText.setWrappingWidth(800);

        continueText = new LocalizedText("cutscene.continue", Color.YELLOW, 16.0);
        continueText.setFont(Font.font("Arial", 16.0));
        continueText.setStroke(Color.BLACK);
        continueText.setStrokeWidth(0.5);

        // Создаем контейнер для текста
        textBox = new VBox(30);
        textBox.setAlignment(javafx.geometry.Pos.CENTER);
        textBox.setPrefSize(1280, 720);
        textBox.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        // Добавляем элементы в контейнер
        textBox.getChildren().addAll(
            titleText,
            subtitleText,
            storyText,
            continueText
        );

        // Изначально все элементы скрыты
        titleText.setOpacity(0.0);
        subtitleText.setOpacity(0.0);
        storyText.setOpacity(0.0);
        continueText.setOpacity(0.0);

        content.getChildren().add(textBox);
    }

    /**
     * Запускает последовательность кат-сцены
     */
    private void playIntroSequence() {
        // Шаг 0: Появление заголовка
        animateText(titleText, Duration.seconds(2.0), () -> {
            currentStep = 1;

            // Шаг 1: Появление подзаголовка
            animateText(subtitleText, Duration.seconds(1.5), () -> {
                currentStep = 2;

                // Шаг 2: Появление основного текста
                animateText(storyText, Duration.seconds(2.0), () -> {
                    currentStep = 3;
                    canContinue = true;

                    // Показываем текст о продолжении
                    animateContinueText();
                });
            });
        });
    }

    /**
     * Анимирует текст продолжения
     */
    private void animateContinueText() {
        // Пульсирующая анимация для текста продолжения
        continueText.setOpacity(0.7);

        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(continueText.opacityProperty(), 0.7)),
            new KeyFrame(Duration.seconds(1.0),
                new KeyValue(continueText.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(2.0),
                new KeyValue(continueText.opacityProperty(), 0.7))
        );

        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
    }

    /**
     * Обрабатывает нажатие клавиши для продолжения
     */
    public void handleContinue() {
        if (canContinue && currentStep == 3) {
            stop();
        }
    }

    /**
     * Проверяет, можно ли продолжить
     */
    public boolean canContinue() {
        return canContinue;
    }

    @Override
    public void setSkippable(boolean skippable) {
        super.setSkippable(skippable);
        // Можно добавить дополнительную логику для отображения подсказки о пропуске
    }
}