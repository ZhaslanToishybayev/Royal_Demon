package uwu.openjfx.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import uwu.openjfx.MainApp;
import uwu.openjfx.items.Item;

/**
 * Component that applies artifact effects to the player
 */
public class ArtifactComponent extends Component {
    private double damageMultiplier = 1.0;
    private double speedMultiplier = 1.0;
    private double critChanceBonus = 0.0;
    private double lifeSteal = 0.0;
    private int healthBonus = 0;
    private int manaBonus = 0;
    private double goldMultiplier = 1.0;

    public ArtifactComponent() {
        // Default constructor
    }

    /**
     * Apply an artifact effect
     */
    public void applyArtifact(String artifactId) {
        switch (artifactId) {
            case "ring_of_power":
                damageMultiplier *= 1.2;
                break;

            case "amulet_of_health":
                // ИСПРАВЛЕНО: уменьшено влияние для баланса (20 HP = 4 сердца)
                healthBonus += 10;
                if (entity != null && entity.hasComponent(PlayerComponent.class)) {
                    PlayerComponent player = entity.getComponent(PlayerComponent.class);
                    // НЕ изменяем max HP, это нарушает баланс!
                    player.increaseHealth(10); // Лечение 10 HP (2 сердца)
                }
                break;

            case "boots_of_speed":
                speedMultiplier *= 1.3;
                break;

            case "charm_of_crit":
                critChanceBonus += 0.15;
                break;

            case "orb_of_mana":
                manaBonus += 100;
                if (entity != null && entity.hasComponent(PlayerComponent.class)) {
                    PlayerComponent player = entity.getComponent(PlayerComponent.class);
                    // Add mana system integration here if needed
                }
                break;

            case "necklace_of_life":
                lifeSteal += 0.05;
                break;

            case "crown_of_kings":
                damageMultiplier *= 1.5;
                goldMultiplier *= 2.0;
                break;
        }
    }

    /**
     * Get damage multiplier from artifacts
     */
    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    /**
     * Get speed multiplier from artifacts
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Get crit chance bonus from artifacts
     */
    public double getCritChanceBonus() {
        return critChanceBonus;
    }

    /**
     * Get life steal from artifacts
     */
    public double getLifeSteal() {
        return lifeSteal;
    }

    /**
     * Get gold multiplier from artifacts
     */
    public double getGoldMultiplier() {
        return goldMultiplier;
    }

    /**
     * Get health bonus from artifacts
     */
    public int getHealthBonus() {
        return healthBonus;
    }

    /**
     * Get mana bonus from artifacts
     */
    public int getManaBonus() {
        return manaBonus;
    }

    /**
     * Apply life steal healing when damaging an enemy
     */
    public void applyLifeSteal(double damageDealt) {
        if (lifeSteal > 0 && entity != null && entity.hasComponent(PlayerComponent.class)) {
            int healAmount = (int) (damageDealt * lifeSteal);
            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            player.increaseHealth(healAmount);
        }
    }
}
