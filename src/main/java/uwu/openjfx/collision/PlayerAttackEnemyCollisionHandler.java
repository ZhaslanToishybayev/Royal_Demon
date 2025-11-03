package uwu.openjfx.collision;

import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import uwu.openjfx.RoyalType;
import uwu.openjfx.components.AttackDamageComponent;
import uwu.openjfx.components.DamageOverTimeComponent;
import uwu.openjfx.components.EnemyComponent;
import uwu.openjfx.components.ExplosionAtDistComponent;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.components.BossComponent;

public class PlayerAttackEnemyCollisionHandler extends CollisionHandler  {
    public PlayerAttackEnemyCollisionHandler() {
        super(RoyalType.PLAYERATTACK, RoyalType.ENEMY);
    }

    @Override
    public void onCollisionBegin(Entity weapon, Entity enemy) {
        if (((weapon.hasComponent(ProjectileComponent.class))
            && (weapon.hasComponent(AttackDamageComponent.class))
            && (!weapon.getComponent(ProjectileComponent.class).isPaused())
            && (!weapon.getComponent(AttackDamageComponent.class).isActive()))
            || ((!weapon.hasComponent(ProjectileComponent.class))
            && (weapon.hasComponent(AttackDamageComponent.class))
            && (!weapon.getComponent(AttackDamageComponent.class).isActive()))) {
            if (weapon.hasComponent(ExplosionAtDistComponent.class)
                && weapon.getComponent(ExplosionAtDistComponent.class).getExplodeColl()) {
                weapon.getComponent(ExplosionAtDistComponent.class).explode();
            }
            weapon.removeFromWorld();
        }

        if (((weapon.hasComponent(ProjectileComponent.class))
            && (!weapon.getComponent(ProjectileComponent.class).isPaused()))
            || (!weapon.hasComponent(ProjectileComponent.class))) {
            EnemyComponent enemyComponent;
            if (enemy.hasComponent(BossComponent.class)) {
                enemyComponent = enemy.getComponent(BossComponent.class);
            } else {
                enemyComponent = enemy.getComponent(EnemyComponent.class);
            }
            enemyComponent.turnSpriteRed();
            if (!weapon.hasComponent(DamageOverTimeComponent.class)) {
                enemyComponent.knockBackFromPlayer();
            }
            
            double baseDamage = weapon.getComponent(AttackDamageComponent.class).getAttackDamage();

            // Fixed: Pass base damage without multiplying - let deductHealth() handle the calculation
            enemyComponent.deductHealth(
                baseDamage,
                PlayerComponent.getAttackPower(),
                enemyComponent.getBlockProbability(),
                enemyComponent.getArmorStat(),
                PlayerComponent.getPiercePow());
        }
    }

    @Override
    public void onCollision(Entity weapon, Entity enemy) {
        if (weapon.hasComponent(DamageOverTimeComponent.class)) {
            EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
            enemyComponent.turnSpriteRed();
            if (!enemyComponent.isInvulnerable()) {
                enemyComponent.deductHealth(
                    weapon.getComponent(AttackDamageComponent.class).getAttackDamage(),
                    PlayerComponent.getAttackPower(),
                    enemyComponent.getBlockProbability(),
                    enemyComponent.getArmorStat(),
                    PlayerComponent.getPiercePow());
            }
        }
    }
}
