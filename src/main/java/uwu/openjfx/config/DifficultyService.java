package uwu.openjfx.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public final class DifficultyService {

    private static final String DEFAULT_RESOURCE = "config/difficulty.json";

    private final Map<String, DifficultyStats> difficulties;
    private String activeDifficulty = "normal";

    public DifficultyService() {
        this.difficulties = load();
    }

    @SuppressFBWarnings(value = "NP_LOAD_OF_KNOWN_NULL_VALUE", justification = "Resource stream is explicitly checked before use.")
    private Map<String, DifficultyStats> load() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = DifficultyService.class
                .getClassLoader()
                .getResourceAsStream(DEFAULT_RESOURCE);
        if (stream == null) {
            return Collections.emptyMap();
        }
        try (InputStream in = stream) {
            Map<String, DifficultyStats> loaded =
                    mapper.readValue(in, new TypeReference<Map<String, DifficultyStats>>() { });
            return Collections.unmodifiableMap(loaded);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load difficulty configuration", e);
        }
    }

    public void setActiveDifficulty(String difficultyId) {
        if (difficulties.containsKey(difficultyId)) {
            activeDifficulty = difficultyId;
        } else {
            throw new IllegalArgumentException("Unknown difficulty: " + difficultyId);
        }
    }

    public String getActiveDifficulty() {
        return activeDifficulty;
    }

    public double getEnemyHealthMultiplier() {
        DifficultyStats stats = difficulties.get(activeDifficulty);
        return stats != null ? stats.getEnemyHealthMultiplier() : 1.0;
    }

    public double getEnemyDamageMultiplier() {
        DifficultyStats stats = difficulties.get(activeDifficulty);
        return stats != null ? stats.getEnemyDamageMultiplier() : 1.0;
    }
}
