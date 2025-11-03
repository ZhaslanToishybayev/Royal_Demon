package uwu.openjfx.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uwu.openjfx.utils.GameLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер системы обучения и туториалов
 */
public class TutorialManager {
    private static TutorialManager instance;
    private List<TutorialStep> tutorialSteps;
    private int currentStepIndex = 0;
    private boolean isActive = false;
    private VBox tutorialUI;

    private TutorialManager() {
        initializeTutorialSteps();
        createTutorialUI();
    }

    public static TutorialManager getInstance() {
        if (instance == null) {
            instance = new TutorialManager();
        }
        return instance;
    }

    private void initializeTutorialSteps() {
        tutorialSteps = new ArrayList<>();

        // Шаг 1: Приветствие
        tutorialSteps.add(new TutorialStep(
                "Добро пожаловать в Royal Demons!",
                "Это игра о приключениях в подземельях. Используйте WASD для движения.",
                "Движение"
        ));

        // Шаг 2: Базовое управление
        tutorialSteps.add(new TutorialStep(
                "Основы управления",
                "Левый клик мыши - обычная атака\n" +
                "Пробел - специальная атака\n" +
                "E - подобрать предметы",
                "Атаки"
        ));

        // Шаг 3: Здоровье и зелья
        tutorialSteps.add(new TutorialStep(
                "Здоровье и предметы",
                "Ваше здоровье показано вверху экрана.\n" +
                "Подбирайте красные сердца для восстановления здоровья.\n" +
                "Используйте клавиши 1 и 2 для зелий.",
                "Здоровье"
        ));

        // Шаг 4: Оружие
        tutorialSteps.add(new TutorialStep(
                "Оружие и бои",
                "Разные типы оружия имеют уникальные характеристики.\n" +
                "Экспериментируйте с разными атаками!\n" +
                "Собирайте новое оружие из сундуков.",
                "Оружие"
        ));

        // Шаг 5: Цель игры
        tutorialSteps.add(new TutorialStep(
                "Ваша цель",
                "Исследуйте подземелья, сражайтесь с врагами\n" +
                "и доберитесь до финального босса.\n" +
                "Удачи, герой!",
                "Цель"
        ));
    }

    private void createTutorialUI() {
        tutorialUI = new VBox(20);
        tutorialUI.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 20px; -fx-background-radius: 10px;");
        tutorialUI.setPrefWidth(400);
        tutorialUI.setLayoutX(50);
        tutorialUI.setLayoutY(50);
        tutorialUI.setVisible(false);
    }

    /**
     * Запускает туториал для нового игрока
     */
    public void startTutorial() {
        if (tutorialSteps.isEmpty()) return;

        isActive = true;
        currentStepIndex = 0;
        showCurrentStep();
        GameLogger.gameplay("Туториал начат");
    }

    /**
     * Показывает следующий шаг туториала
     */
    public void nextStep() {
        if (!isActive) return;

        currentStepIndex++;
        if (currentStepIndex >= tutorialSteps.size()) {
            completeTutorial();
        } else {
            showCurrentStep();
        }
    }

    /**
     * Пропускает текущий шаг туториала
     */
    public void skipStep() {
        if (!isActive) return;
        nextStep();
    }

    /**
     * Завершает туториал
     */
    public void completeTutorial() {
        isActive = false;
        hideTutorial();
        FXGL.getGameScene().removeUINode(tutorialUI);
        GameLogger.gameplay("Туториал завершен");
    }

    /**
     * Проверяет, активен ли туториал
     */
    public boolean isActive() {
        return isActive;
    }

    private void showCurrentStep() {
        if (currentStepIndex >= tutorialSteps.size()) return;

        TutorialStep currentStep = tutorialSteps.get(currentStepIndex);

        tutorialUI.getChildren().clear();

        Text title = FXGL.getUIFactoryService().newText(currentStep.getTitle(), Color.GOLD, 24);
        title.setStyle("-fx-font-weight: bold;");

        Text content = FXGL.getUIFactoryService().newText(currentStep.getContent(), Color.WHITE, 16);
        content.setStyle("-fx-wrap-text: true;");

        Text instruction = FXGL.getUIFactoryService().newText("Нажмите ПРОБЕЛ для продолжения или ESC для пропуска", Color.LIGHTGRAY, 14);

        tutorialUI.getChildren().addAll(title, content, instruction);

        // Добавляем UI на сцену, если еще не добавлен
        if (FXGL.getGameScene().getUINodes().stream().noneMatch(node -> node == tutorialUI)) {
            FXGL.getGameScene().addUINode(tutorialUI);
        }

        tutorialUI.setVisible(true);
    }

    private void hideTutorial() {
        tutorialUI.setVisible(false);
    }

    /**
     * Обрабатывает ввод для туториала
     */
    public void handleTutorialInput(String action) {
        if (!isActive) return;

        switch (action.toLowerCase()) {
            case "space":
            case "enter":
                nextStep();
                break;
            case "escape":
                skipStep();
                break;
        }
    }

    /**
     * Класс для представления шага туториала
     */
    public static class TutorialStep {
        private final String title;
        private final String content;
        private final String category;

        public TutorialStep(String title, String content, String category) {
            this.title = title;
            this.content = content;
            this.category = category;
        }

        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getCategory() { return category; }
    }
}