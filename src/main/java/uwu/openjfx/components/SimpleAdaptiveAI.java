package uwu.openjfx.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import java.util.Random;

/**
 * Простая адаптивная AI система для врагов
 */
public class SimpleAdaptiveAI extends Component {
    
    public enum AIType {
        AGGRESSIVE,   // Прямое нападение
        DEFENSIVE,   // Держит дистанцию
        TACTICAL,     // Кружит вокруг
        ENRAGED,       // Ярость при низком HP
        BOSS
    }
    
    private AIType currentBehavior = AIType.AGGRESSIVE;
    private Entity player;
    private EnemyComponent enemyComponent;
    private Random random = new Random();
    
    // Параметры AI
    private double aggressiveness = 0.5;
    private double intelligence = 0.5;
    private double cautiousness = 0.5;
    
    private long lastDecisionTime = 0;
    private long decisionCooldown = 3000; // 3 секунды
    
    public SimpleAdaptiveAI(Entity player, double aggressiveness, double intelligence, double cautiousness) {
        this.player = player;
        this.aggressiveness = aggressiveness;
        this.intelligence = intelligence;
        this.cautiousness = cautiousness;
    }
    
    @Override
    public void onUpdate(double tpf) {
        if (player == null || enemyComponent == null) {
            return;
        }
        
        // Принимаем решение каждые decisionCooldown
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDecisionTime > decisionCooldown) {
            makeDecision();
            lastDecisionTime = currentTime;
        }
        
        // Выполняем текущее поведение
        executeBehavior(tpf);
    }
    
    /**
     * Принять решение о поведении
     */
    private void makeDecision() {
        double healthPercentage = getHealthPercentage();
        double distanceToPlayer = getDistanceToPlayer();
        
        // Определяем поведение на основе здоровья и параметров
        if (healthPercentage < 0.3) {
            // Низкое здоровье
            if (aggressiveness > 0.7) {
                currentBehavior = AIType.ENRAGED;
            } else {
                currentBehavior = AIType.DEFENSIVE;
            }
        } else if (healthPercentage < 0.6) {
            // Среднее здоровье
            if (intelligence > 0.6) {
                currentBehavior = AIType.TACTICAL;
            } else if (cautiousness > 0.5) {
                currentBehavior = AIType.DEFENSIVE;
            } else {
                currentBehavior = AIType.AGGRESSIVE;
            }
        } else {
            // Высокое здоровье
            if (aggressiveness > 0.6) {
                currentBehavior = AIType.AGGRESSIVE;
            } else if (intelligence > 0.6) {
                currentBehavior = AIType.TACTICAL;
            } else {
                currentBehavior = AIType.AGGRESSIVE;
            }
        }
    }
    
    /**
     * Выполнить текущее поведение
     */
    private void executeBehavior(double tpf) {
        double distanceToPlayer = getDistanceToPlayer();
        
        switch (currentBehavior) {
            case AGGRESSIVE:
                executeAggressive(tpf, distanceToPlayer);
                break;
            case DEFENSIVE:
                executeDefensive(tpf, distanceToPlayer);
                break;
            case TACTICAL:
                executeTactical(tpf, distanceToPlayer);
                break;
            case ENRAGED:
                executeEnraged(tpf, distanceToPlayer);
                break;
            case BOSS:
                executeBoss(tpf, distanceToPlayer);
                break;
        }
    }
    
    /**
     * Агрессивное поведение - прямое движение к игроку
     */
    private void executeAggressive(double tpf, double distance) {
        double optimalDistance = 100;
        
        if (distance > optimalDistance + 20) {
            moveTowardsPlayer(tpf * 1.2); // Быстрое приближение
        } else if (distance < optimalDistance - 20) {
            moveTowardsPlayer(tpf * 0.5); // Медленное движение
        } else {
            triggerAttack();
        }
    }
    
    /**
     * Защитное поведение - держит дистанцию
     */
    private void executeDefensive(double tpf, double distance) {
        double optimalDistance = 200;
        
        if (distance < optimalDistance - 20) {
            moveAwayFromPlayer(tpf); // Отдаляемся
        } else if (distance > optimalDistance + 50) {
            moveTowardsPlayer(tpf * 0.7); // Медленное приближение
        }
        
        // Атакуем только если безопасно
        if (distance < optimalDistance && distance > 100) {
            if (random.nextDouble() < 0.3) { // 30% шанс атаки
                triggerAttack();
            }
        }
    }
    
    /**
     * Тактическое поведение - кружение
     */
    private void executeTactical(double tpf, double distance) {
        double optimalDistance = 150;
        
        if (distance > optimalDistance + 30) {
            moveTowardsPlayer(tpf * 0.8);
        } else if (distance < optimalDistance - 30) {
            moveAwayFromPlayer(tpf * 0.5);
        } else {
            // Движемся по кругу
            moveCircularAroundPlayer(tpf);
        }
        
        // Умная атака - ждем момента
        if (distance < optimalDistance + 10 && distance > optimalDistance - 10) {
            if (random.nextDouble() < 0.4) { // 40% шанс атаки
                triggerAttack();
            }
        }
    }
    
    /**
     * Поведение ярости - быстрое нападение
     */
    private void executeEnraged(double tpf, double distance) {
        double speedMultiplier = 1.5; // Увеличенная скорость
        
        if (distance > 80) {
            moveTowardsPlayer(tpf * speedMultiplier);
        } else {
            triggerAttack();
        }
        
        // Частые атаки
        if (random.nextDouble() < 0.6) { // 60% шанс атаки
            triggerAttack();
        }
    }

    private void executeBoss(double tpf, double distance) {
        if (distance > 200) {
            moveTowardsPlayer(tpf);
        } else {
            if (random.nextDouble() < 0.5) {
                triggerAttack();
            } else {
                enemyComponent.rangedAttack();
            }
        }
    }
    
    /**
     * Движение по кругу вокруг игрока
     */
    private void moveCircularAroundPlayer(double tpf) {
        double angle = System.currentTimeMillis() * 0.003; // Вращение
        double radius = 150;
        
        double targetX = player.getX() + Math.cos(angle) * radius;
        double targetY = player.getY() + Math.sin(angle) * radius;
        
        moveTowardsPosition(targetX, targetY, tpf);
    }
    
    // Вспомогательные методы
    
    private double getDistanceToPlayer() {
        if (player == null || getEntity() == null) return Double.MAX_VALUE;
        return getEntity().getPosition().distance(player.getPosition());
    }
    
    private double getHealthPercentage() {
        if (enemyComponent == null) return 1.0;
        return (double) enemyComponent.getHealthPoints() / enemyComponent.getMaxHealthPoints();
    }
    
    private void moveTowardsPlayer(double tpf) {
        if (player == null || getEntity() == null) return;
        
        double dx = player.getX() - getEntity().getX();
        double dy = player.getY() - getEntity().getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            try {
                enemyComponent.move(dx * 50 * tpf, -dy * 50 * tpf);
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
    }
    
    private void moveAwayFromPlayer(double tpf) {
        if (player == null || getEntity() == null) return;
        
        double dx = getEntity().getX() - player.getX();
        double dy = getEntity().getY() - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            try {
                enemyComponent.move(dx * 40 * tpf, -dy * 40 * tpf);
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
    }
    
    private void moveTowardsPosition(double x, double y, double tpf) {
        if (getEntity() == null) return;
        
        double dx = x - getEntity().getX();
        double dy = y - getEntity().getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            try {
                enemyComponent.move(dx * 40 * tpf, -dy * 40 * tpf);
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
    }
    
    private void triggerAttack() {
        try {
            enemyComponent.setPrepAttack(true);
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    @Override
    public void onAdded() {
        enemyComponent = getEntity().getComponent(EnemyComponent.class);
    }
    
    @Override
    public void onRemoved() {
        // Очистка
    }
    
    // Getters
    
    public AIType getCurrentBehavior() {
        return currentBehavior;
    }

    public void setCurrentBehavior(AIType behavior) {
        this.currentBehavior = behavior;
    }
    
    public double getAggressiveness() {
        return aggressiveness;
    }
    
    public double getIntelligence() {
        return intelligence;
    }
    
    public double getCautiousness() {
        return cautiousness;
    }
    
    /**
     * Получить множитель скорости от AI
     */
    public double getSpeedMultiplier() {
        switch (currentBehavior) {
            case AGGRESSIVE:
                return 1.1;
            case DEFENSIVE:
                return 0.9;
            case TACTICAL:
                return 1.0;
            case ENRAGED:
                return 1.3;
            default:
                return 1.0;
        }
    }
    
    /**
     * Получить множитель урона от AI
     */
    public double getDamageMultiplier() {
        switch (currentBehavior) {
            case AGGRESSIVE:
                return 1.2;
            case DEFENSIVE:
                return 0.9;
            case TACTICAL:
                return 1.0;
            case ENRAGED:
                return 1.5;
            default:
                return 1.0;
        }
    }
}
