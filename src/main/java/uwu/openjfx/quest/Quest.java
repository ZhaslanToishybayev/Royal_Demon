package uwu.openjfx.quest;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс представления квеста/задания
 */
public class Quest {
    private final String id;
    private final String title;
    private final String description;
    private final QuestType type;
    private final QuestObjective objective;
    private final QuestReward reward;
    private boolean completed = false;

    public Quest(String id, String title, String description, QuestType type, QuestObjective objective) {
        this(id, title, description, type, objective, null);
    }

    public Quest(String id, String title, String description, QuestType type,
                 QuestObjective objective, QuestReward reward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.objective = objective;
        this.reward = reward;
    }

    /**
     * Обновляет прогресс квеста
     * @return true если квест завершен
     */
    public boolean updateProgress(String objectiveType, int amount) {
        if (completed || !objective.getType().equals(objectiveType)) {
            return false;
        }

        objective.addProgress(amount);
        return objective.isCompleted();
    }

    /**
     * Проверяет завершен ли квест
     */
    public boolean isCompleted() {
        return completed || objective.isCompleted();
    }

    /**
     * Завершает квест
     */
    public void complete() {
        this.completed = true;
        objective.setCompleted(true);
    }

    /**
     * Получает прогресс в процентах
     */
    public double getProgressPercentage() {
        return objective.getProgressPercentage();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestType getType() { return type; }
    public QuestObjective getObjective() { return objective; }
    public QuestReward getReward() { return reward; }

    @Override
    public String toString() {
        return String.format("%s - %s (%.1f%%)", title, type, getProgressPercentage());
    }
}