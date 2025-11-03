package uwu.openjfx.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DifficultyStats {

    @JsonProperty("enemyHealthMultiplier")
    private Double enemyHealthMultiplier;

    @JsonProperty("enemyDamageMultiplier")
    private Double enemyDamageMultiplier;

    public double getEnemyHealthMultiplier() {
        return enemyHealthMultiplier != null ? enemyHealthMultiplier : 1.0;
    }

    public double getEnemyDamageMultiplier() {
        return enemyDamageMultiplier != null ? enemyDamageMultiplier : 1.0;
    }
}
