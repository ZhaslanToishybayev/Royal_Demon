package uwu.openjfx.quest;

import java.util.HashMap;
import java.util.Map;

/**
 * Награда за выполнение квеста
 */
public class QuestReward {
    private final int experience;
    private final int gold;
    private final Map<String, Integer> items = new HashMap<>();

    public QuestReward(int experience, int gold) {
        this.experience = experience;
        this.gold = gold;
    }

    public QuestReward(int experience, int gold, Map<String, Integer> items) {
        this.experience = experience;
        this.gold = gold;
        if (items != null) {
            this.items.putAll(items);
        }
    }

    /**
     * Добавляет предмет в награду
     */
    public void addItem(String itemId, int quantity) {
        items.put(itemId, quantity);
    }

    // Getters
    public int getExperience() { return experience; }
    public int getGold() { return gold; }
    public Map<String, Integer> getItems() { return new HashMap<>(items); }

    /**
     * Проверяет есть ли награда
     */
    public boolean hasReward() {
        return experience > 0 || gold > 0 || !items.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (experience > 0) {
            sb.append("Опыт: ").append(experience).append(" ");
        }
        if (gold > 0) {
            sb.append("Золото: ").append(gold).append(" ");
        }
        if (!items.isEmpty()) {
            sb.append("Предметы: ").append(items.size());
        }
        return sb.toString().trim();
    }
}