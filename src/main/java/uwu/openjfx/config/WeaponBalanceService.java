package uwu.openjfx.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public final class WeaponBalanceService {

    private static final String DEFAULT_RESOURCE = "config/weapons.json";
    private final Map<String, WeaponStats> stats;

    public WeaponBalanceService() {
        this.stats = loadStats();
    }

    private Map<String, WeaponStats> loadStats() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream resourceStream = WeaponBalanceService.class
                .getClassLoader()
                .getResourceAsStream(DEFAULT_RESOURCE);
        if (resourceStream == null) {
            return Collections.emptyMap();
        }
        try (InputStream in = resourceStream) {
            Map<String, WeaponStats> loaded = mapper.readValue(in, new TypeReference<Map<String, WeaponStats>>() { });
            return Collections.unmodifiableMap(loaded);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load weapon configuration", e);
        }
    }

    public double getAttackDamage(String weaponId, double defaultValue) {
        WeaponStats weaponStats = stats.get(weaponId);
        if (weaponStats == null || weaponStats.getAttackDamage() == null) {
            return defaultValue;
        }
        return weaponStats.getAttackDamage();
    }
}
