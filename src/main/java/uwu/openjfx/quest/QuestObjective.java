package uwu.openjfx.quest;

/**
 * Цель квеста
 */
public class QuestObjective {
    private final String type;
    private final int target;
    private int current = 0;
    private boolean completed = false;

    public QuestObjective(String type, int target) {
        this.type = type;
        this.target = target;
    }

    /**
     * Добавляет прогресс к цели
     */
    public void addProgress(int amount) {
        if (!completed) {
            current = Math.min(current + amount, target);
            if (current >= target) {
                completed = true;
            }
        }
    }

    /**
     * Устанавливает текущий прогресс
     */
    public void setCurrentProgress(int current) {
        this.current = Math.min(current, target);
        this.completed = this.current >= target;
    }

    /**
     * Получает прогресс в процентах
     */
    public double getProgressPercentage() {
        return target > 0 ? (double) current / target * 100.0 : 100.0;
    }

    /**
     * Создает цель на убийство врагов
     */
    public static QuestObjective createKillObjective(String type, int target) {
        return new QuestObjective("kill_" + type, target);
    }

    /**
     * Создает цель на сбор предметов
     */
    public static QuestObjective createCollectObjective(String type, int target) {
        return new QuestObjective("collect_" + type, target);
    }

    /**
     * Создает цель на исследование
     */
    public static QuestObjective createExploreObjective(String type, int target) {
        return new QuestObjective("explore_" + type, target);
    }

    /**
     * Создает цель на достижение уровня
     */
    public static QuestObjective createLevelObjective(int target) {
        return new QuestObjective("reach_level", target);
    }

    // Getters
    public String getType() { return type; }
    public int getTarget() { return target; }
    public int getCurrent() { return current; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            current = target;
        }
    }

    /**
     * Форматированное отображение прогресса
     */
    public String getProgressText() {
        if (completed) {
            return "✓ " + current + "/" + target;
        }
        return current + "/" + target;
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%.1f%%)", type, getProgressText(), getProgressPercentage());
    }
}