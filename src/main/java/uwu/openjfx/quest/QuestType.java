package uwu.openjfx.quest;

/**
 * Типы квестов
 */
public enum QuestType {
    MAIN("Основной"),
    SIDE("Побочный"),
    TUTORIAL("Обучение"),
    DAILY("Ежедневный"),
    ACHIEVEMENT("Достижение");

    private final String displayName;

    QuestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}