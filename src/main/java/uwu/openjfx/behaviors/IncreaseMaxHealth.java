package uwu.openjfx.behaviors;


import com.almasb.fxgl.entity.Entity;
import uwu.openjfx.components.CreatureComponent;

public class IncreaseMaxHealth implements Behavior {

    private int healPoints;

    public IncreaseMaxHealth(int healPoints) {
        this.healPoints = healPoints;
    }

    @Override
    public void act(Entity entity) {
        // Add safe check to prevent errors when entity doesn't have CreatureComponent
        if (entity != null && entity.hasComponent(CreatureComponent.class)) {
            CreatureComponent creatureComponent = entity.getComponent(CreatureComponent.class);
            creatureComponent.setMaxHealthPoints(creatureComponent.getMaxHealthPoints() + healPoints);
        }
    }
}
