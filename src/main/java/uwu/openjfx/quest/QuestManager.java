package uwu.openjfx.quest;

import java.util.*;

/**
 * Менеджер системы заданий/квестов
 */
public class QuestManager {
    private static QuestManager instance;
    private Map<String, Quest> activeQuests = new HashMap<>();
    private Map<String, Quest> completedQuests = new HashMap<>();
    private List<QuestListener> listeners = new ArrayList<>();

    private QuestManager() {
        initializeMainQuests();
    }

    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }

    private void initializeMainQuests() {
        // Основной квест
        Quest mainQuest = new Quest(
                "main_dungeon",
                "Подземелье демона",
                "Исследуйте подземелье и победите финального босса",
                QuestType.MAIN,
                QuestObjective.createExploreObjective("rooms_explored", 10)
        );

        // Квест на первое убийство
        Quest firstKill = new Quest(
                "first_kill",
                "Первый шаг",
                "Убейте своего первого врага",
                QuestType.TUTORIAL,
                QuestObjective.createKillObjective("enemies_killed", 1)
        );

        // Квест на сбор золота
        Quest goldCollector = new Quest(
                "gold_collector",
                "Собиратель золота",
                "Соберите 100 золотых монет",
                QuestType.SIDE,
                QuestObjective.createCollectObjective("gold_collected", 100)
        );

        // Квест на исследование
        Quest explorer = new Quest(
                "explorer",
                "Исследователь",
                "Посетите 5 различных комнат",
                QuestType.SIDE,
                QuestObjective.createExploreObjective("unique_rooms", 5)
        );

        addQuest(mainQuest);
        addQuest(firstKill);
        addQuest(goldCollector);
        addQuest(explorer);
    }

    /**
     * Добавляет новый квест
     */
    public void addQuest(Quest quest) {
        activeQuests.put(quest.getId(), quest);
        notifyQuestAdded(quest);
    }

    /**
     * Обновляет прогресс квеста
     */
    public void updateQuestProgress(String objectiveType, int amount) {
        for (Quest quest : activeQuests.values()) {
            if (quest.updateProgress(objectiveType, amount)) {
                if (quest.isCompleted()) {
                    completeQuest(quest);
                }
            }
        }
    }

    /**
     * Завершает квест
     */
    private void completeQuest(Quest quest) {
        activeQuests.remove(quest.getId());
        completedQuests.put(quest.getId(), quest);

        // Выдаем награду
        giveQuestReward(quest);

        notifyQuestCompleted(quest);
    }

    private void giveQuestReward(Quest quest) {
        QuestReward reward = quest.getReward();
        if (reward != null) {
            // Здесь должна быть логика выдачи награды
            // Например: добавление опыта, золота, предметов и т.д.
            System.out.println("Выдана награда за квест: " + quest.getTitle());
        }
    }

    /**
     * Возвращает все активные квесты
     */
    public Collection<Quest> getActiveQuests() {
        return new ArrayList<>(activeQuests.values());
    }

    /**
     * Возвращает все завершенные квесты
     */
    public Collection<Quest> getCompletedQuests() {
        return new ArrayList<>(completedQuests.values());
    }

    /**
     * Получает квест по ID
     */
    public Quest getQuest(String questId) {
        return activeQuests.getOrDefault(questId, completedQuests.get(questId));
    }

    /**
     * Добавляет слушателя квестов
     */
    public void addListener(QuestListener listener) {
        listeners.add(listener);
    }

    private void notifyQuestAdded(Quest quest) {
        for (QuestListener listener : listeners) {
            listener.onQuestAdded(quest);
        }
    }

    private void notifyQuestCompleted(Quest quest) {
        for (QuestListener listener : listeners) {
            listener.onQuestCompleted(quest);
        }
    }

    /**
     * Интерфейс для слушателей событий квестов
     */
    public interface QuestListener {
        void onQuestAdded(Quest quest);
        void onQuestCompleted(Quest quest);
    }

    /**
     * Получает прогресс по всем квестам в процентах
     */
    public double getOverallProgress() {
        if (activeQuests.isEmpty()) return 100.0;

        double totalProgress = 0;
        for (Quest quest : activeQuests.values()) {
            totalProgress += quest.getProgressPercentage();
        }

        return totalProgress / activeQuests.size();
    }
}