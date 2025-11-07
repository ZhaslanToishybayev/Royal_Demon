package uwu.openjfx.leaderboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import uwu.openjfx.utils.GameLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Менеджер таблицы лидеров
 * Управляет рейтингами игроков по количеству собранных монет
 */
public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.json";
    private static final String LEADERBOARD_DIRECTORY =
            System.getProperty("user.home") + "/.royal-demons";
    private static final int MAX_ENTRIES = 100; // Максимум записей в таблице

    private static LeaderboardManager instance;

    // Thread-safe коллекция для записей
    private final List<LeaderboardEntry> entries;
    private final ObjectMapper objectMapper;

    private LeaderboardManager() {
        this.entries = new CopyOnWriteArrayList<>();
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        loadLeaderboard();
    }

    public static synchronized LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    /**
     * Загрузить таблицу лидеров из файла
     */
    private void loadLeaderboard() {
        try {
            Path leaderboardPath = Paths.get(LEADERBOARD_DIRECTORY, LEADERBOARD_FILE);
            File file = leaderboardPath.toFile();

            if (file.exists()) {
                LeaderboardEntry[] loadedEntries = objectMapper.readValue(file, LeaderboardEntry[].class);
                entries.addAll(Arrays.asList(loadedEntries));

                // Сортируем при загрузке
                Collections.sort(entries);
                GameLogger.info("Загружено " + entries.size() + " записей таблицы лидеров");
            } else {
                GameLogger.info("Файл таблицы лидеров не найден, создаём новый");
                saveLeaderboard(); // Создаём пустой файл
            }
        } catch (IOException e) {
            GameLogger.error("Ошибка загрузки таблицы лидеров: " + e.getMessage());
            // Создаём новую пустую таблицу
            saveLeaderboard();
        }
    }

    /**
     * Сохранить таблицу лидеров в файл
     */
    public void saveLeaderboard() {
        try {
            // Создаём директорию если не существует
            Path dirPath = Paths.get(LEADERBOARD_DIRECTORY);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Сортируем перед сохранением
            Collections.sort(entries);

            Path filePath = Paths.get(LEADERBOARD_DIRECTORY, LEADERBOARD_FILE);
            objectMapper.writeValue(filePath.toFile(), entries);

            GameLogger.debug("Таблица лидеров сохранена");
        } catch (IOException e) {
            GameLogger.error("Ошибка сохранения таблицы лидеров: " + e.getMessage());
        }
    }

    /**
     * Добавить новую запись в таблицу лидеров
     */
    public void addEntry(String playerName, int maxGold, int playerLevel, long playTimeSeconds) {
        // Создаём новую запись
        LeaderboardEntry newEntry = new LeaderboardEntry(playerName, maxGold, playerLevel, playTimeSeconds);

        // Добавляем в коллекцию
        entries.add(newEntry);

        // Сортируем
        Collections.sort(entries);

        // Убираем лишние записи если их больше MAX_ENTRIES
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }

        // Сохраняем в файл
        saveLeaderboard();

        GameLogger.info("Добавлена запись в таблицу лидеров: " + playerName + " - " + maxGold + " монет, уровень " + playerLevel);
    }

    /**
     * Обновить запись для игрока - всегда обновляем с текущими монетами
     */
    public void updatePlayerEntry(String playerName, int currentGold, int playerLevel, long playTimeSeconds) {
        System.out.println("🔍 Ищем запись игрока: " + playerName);

        // Ищем существующую запись для этого игрока
        Optional<LeaderboardEntry> existingEntry = entries.stream()
                .filter(entry -> playerName.equals(entry.getPlayerName()))
                .findFirst();

        if (existingEntry.isPresent()) {
            LeaderboardEntry entry = existingEntry.get();
            System.out.println("📝 Найдена запись игрока. Старый максимум: " + entry.getMaxGold() +
                              ", Новые монеты: " + currentGold);

            // ВСЕГДА обновляем с текущими монетами (для отображения текущего прогресса)
            System.out.println("✅ Обновляем запись с текущими монетами!");

            entry.setMaxGold(currentGold);
            entry.setPlayerLevel(playerLevel);
            entry.setPlayTimeSeconds(playTimeSeconds);
            entry.setAchievedAt(new java.util.Date().toString());

            // НЕ ПЕРЕСОРТИРУЕМ здесь - сортировка нужна только при добавлении/удалении
            // Collections.sort(entries);

            GameLogger.info("Обновлён результат игрока " + playerName + ": " + currentGold + " монет");
            saveLeaderboard();
        } else {
            // Если записи нет, добавляем новую
            System.out.println("➕ Запись не найдена, добавляем новую: " + currentGold + " монет");
            addEntry(playerName, currentGold, playerLevel, playTimeSeconds);
        }
    }

    /**
     * Получить топ N записей
     */
    public List<LeaderboardEntry> getTopEntries(int count) {
        if (entries.isEmpty()) {
            return Collections.emptyList();
        }

        return entries.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Получить позицию игрока в таблице
     */
    public int getPlayerPosition(String playerName) {
        for (int i = 0; i < entries.size(); i++) {
            if (playerName.equals(entries.get(i).getPlayerName())) {
                return i + 1; // Позиция начинается с 1
            }
        }
        return -1; // Игрок не найден в таблице
    }

    /**
     * Получить запись игрока
     */
    public Optional<LeaderboardEntry> getPlayerEntry(String playerName) {
        return entries.stream()
                .filter(entry -> playerName.equals(entry.getPlayerName()))
                .findFirst();
    }

    /**
     * Получить все записи
     */
    public List<LeaderboardEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Проверить попал ли игрок в топ
     */
    public boolean isPlayerInTop(String playerName, int topCount) {
        int position = getPlayerPosition(playerName);
        return position > 0 && position <= topCount;
    }

    /**
     * Получить статистику таблицы
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEntries", entries.size());
        stats.put("maxEntries", MAX_ENTRIES);

        if (!entries.isEmpty()) {
            stats.put("topGold", entries.get(0).getMaxGold());
            stats.put("topPlayer", entries.get(0).getPlayerName());

            int totalGold = entries.stream()
                    .mapToInt(LeaderboardEntry::getMaxGold)
                    .sum();
            stats.put("totalGoldAllPlayers", totalGold);

            double averageGold = entries.stream()
                    .mapToInt(LeaderboardEntry::getMaxGold)
                    .average()
                    .orElse(0.0);
            stats.put("averageGold", Math.round(averageGold * 100.0) / 100.0);
        } else {
            stats.put("topGold", 0);
            stats.put("topPlayer", "N/A");
            stats.put("totalGoldAllPlayers", 0);
            stats.put("averageGold", 0.0);
        }

        return stats;
    }

    /**
     * Очистить таблицу лидеров (для тестирования)
     */
    public void clearLeaderboard() {
        entries.clear();
        saveLeaderboard();
        GameLogger.info("Таблица лидеров очищена");
    }

    /**
     * Получить размер таблицы
     */
    public int getSize() {
        return entries.size();
    }

    /**
     * Проверить пуста ли таблица
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
