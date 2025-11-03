package uwu.openjfx.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uwu.openjfx.MainApp;
import uwu.openjfx.behaviors.Behavior;
import uwu.openjfx.behaviors.DoNothing;
import uwu.openjfx.behaviors.HasLife;

import java.util.Timer;

public class CreatureComponent extends Component implements HasLife {

    private int healthPoints;
    private int maxHealthPoints;
    private boolean isInvulnerable;
    private IntegerProperty playerHealthIntegerProperty;
    private Behavior dieBehavior;

    public CreatureComponent(int healthPoints, int maxHealthPoints, Behavior dieBehavior) {
        this.healthPoints = healthPoints;
        this.maxHealthPoints = maxHealthPoints;
        this.playerHealthIntegerProperty = new SimpleIntegerProperty(healthPoints);
        setDieBehavior(dieBehavior);
    }

    public CreatureComponent(int healthPoints, int maxHealthPoints) {
        this(healthPoints, maxHealthPoints, new DoNothing());
    }

    @Override
    public void increaseHealth(int point) {
        this.healthPoints = Math.min(this.healthPoints + point, maxHealthPoints);
        playerHealthIntegerProperty.set(healthPoints);
        if (this instanceof BossComponent) {
            BossComponent.setBossHealthProperty(healthPoints);
        }
    }

    public void deductHealth(double point, double attackPower,
                             double blockProb, double armor, int pierce) {
        if (armor <= 0) {
            armor = 1; // Минимальная броня для предотвращения деления на 0
        }

        int blockRand = MainApp.getRandom().nextInt(100) + 1;
        if (blockRand <= blockProb * pierce) {
            if (!MainApp.isIsTesting()) {
                FXGL.play("block.wav");
            }
            return;
        }

        double damageDealt = (point * attackPower) / armor;
        if (this instanceof EnemyComponent) {
            if (damageDealt > getHealthPoints()) {
                PlayerComponent.addToDamageDealt(getHealthPoints());
            } else {
                PlayerComponent.addToDamageDealt(damageDealt);
            }
        }
        if (this instanceof BossComponent) {
            BossComponent.setBossHealthProperty(
                (int) (healthPoints - damageDealt));
        }
        // Исправлено: правильное округление урона для избежания проблем с дробными значениями
        healthPoints -= (int) Math.ceil(damageDealt);
        if (healthPoints <= 0) { // die
            healthPoints = 0;
            die();
            if (this instanceof EnemyComponent) {
                PlayerComponent.addToMonstersKilled();
            }
        } else {
            isInvulnerable = true;
            invulnerability();
        }
        playerHealthIntegerProperty.set(healthPoints);
    }

    private void invulnerability() {
        isInvulnerable = true;
        // Fixed: Use FXGL game timer instead of unbounded Timer to prevent memory leaks
        // Исправлено: При HP = 5, неуязвимость сокращена с 2000мс до 500мс для баланса
        com.almasb.fxgl.dsl.FXGL.getGameTimer().runOnceAfter(() -> {
            isInvulnerable = false;
        }, javafx.util.Duration.millis(!(this instanceof EnemyComponent) ? 500 : 100));
    }

    @Override
    public int getHealthPoints() {
        return healthPoints;
    }

    @Override
    public void setHealthPoints(int healthPoints) {
        playerHealthIntegerProperty.set(healthPoints);
        this.healthPoints = healthPoints;
    }

    @Override
    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    @Override
    public void setMaxHealthPoints(int maxHealthPoints) {
        this.maxHealthPoints = maxHealthPoints;
    }

    @Override
    public void setDieBehavior(Behavior dieBehavior) {
        this.dieBehavior = dieBehavior;
    }

    @Override
    public Behavior getDieBehavior() {
        return dieBehavior;
    }

    @Override
    public boolean dead() {
        return healthPoints <= 0;
    }

    @Override
    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Health property is intentionally shared for UI bindings.")
    @Override
    public IntegerProperty getHealthIntegerProperty() {
        return playerHealthIntegerProperty;
    }

    @Override
    public void die() {
        dieBehavior.act(getEntity());
    }
    
}
