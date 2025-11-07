package uwu.openjfx.leaderboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Запись в таблице лидеров
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    @JsonProperty("playerName")
    private String playerName;

    @JsonProperty("maxGold")
    private int maxGold;

    @JsonProperty("achievedAt")
    private String achievedAt;

    @JsonProperty("playerLevel")
    private int playerLevel;

    @JsonProperty("playTimeSeconds")
    private long playTimeSeconds;

    // Конструктор по умолчанию для Jackson
    public LeaderboardEntry() {}

    public LeaderboardEntry(String playerName, int maxGold, int playerLevel, long playTimeSeconds) {
        this.playerName = playerName;
        this.maxGold = maxGold;
        this.playerLevel = playerLevel;
        this.playTimeSeconds = playTimeSeconds;
        this.achievedAt = new java.util.Date().toString();
    }

    // Геттеры и сеттеры
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getMaxGold() {
        return maxGold;
    }

    public void setMaxGold(int maxGold) {
        this.maxGold = maxGold;
    }

    public String getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(String achievedAt) {
        this.achievedAt = achievedAt;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public long getPlayTimeSeconds() {
        return playTimeSeconds;
    }

    public void setPlayTimeSeconds(long playTimeSeconds) {
        this.playTimeSeconds = playTimeSeconds;
    }

    /**
     * Сравнение по количеству монет (по убыванию)
     */
    @Override
    public int compareTo(LeaderboardEntry other) {
        // Сортируем по убыванию количества монет
        return Integer.compare(other.maxGold, this.maxGold);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardEntry that = (LeaderboardEntry) o;
        return maxGold == that.maxGold &&
               playerLevel == that.playerLevel &&
               playTimeSeconds == that.playTimeSeconds &&
               Objects.equals(playerName, that.playerName) &&
               Objects.equals(achievedAt, that.achievedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, maxGold, achievedAt, playerLevel, playTimeSeconds);
    }

    @Override
    public String toString() {
        return String.format("LeaderboardEntry{name='%s', gold=%d, level=%d, time=%ds}",
                            playerName, maxGold, playerLevel, playTimeSeconds);
    }
}
