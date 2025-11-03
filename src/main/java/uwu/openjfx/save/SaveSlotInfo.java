package uwu.openjfx.save;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Информация о слоте сохранения
 */
public class SaveSlotInfo {
    private final String filename;
    private final int playerLevel;
    private final LocalDateTime saveTime;
    private final long playTime;
    private final boolean isCorrupted;

    public SaveSlotInfo(String filename, int playerLevel, LocalDateTime saveTime, long playTime) {
        this.filename = filename;
        this.playerLevel = playerLevel;
        this.saveTime = saveTime;
        this.playTime = playTime;
        this.isCorrupted = saveTime == null;
    }

    public String getFilename() {
        return filename;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public LocalDateTime getSaveTime() {
        return saveTime;
    }

    public long getPlayTime() {
        return playTime;
    }

    public boolean isCorrupted() {
        return isCorrupted;
    }

    public String getFormattedSaveTime() {
        if (isCorrupted) {
            return "Повреждено";
        }
        return saveTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getFormattedPlayTime() {
        long hours = playTime / 3600;
        long minutes = (playTime % 3600) / 60;
        long seconds = playTime % 60;

        if (hours > 0) {
            return String.format("%dч %dм", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dм %dс", minutes, seconds);
        } else {
            return String.format("%dс", seconds);
        }
    }

    public String getDisplayName() {
        if (isCorrupted) {
            return "Поврежденное сохранение";
        }
        return String.format("Уровень %d - %s", playerLevel, getFormattedSaveTime());
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}