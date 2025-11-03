package uwu.openjfx.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import java.util.Random;

/**
 * Компонент адаптивного AI для врагов
 * Изменяет поведение в зависимости от ситуации
 */
public class AdaptiveAI extends Component {

    private Entity player;
    private EnemyComponent enemyComponent;
    private long lastDecisionTime = 0;
    private long decisionCooldown = 2000; // 2 секунды между решениями

    // Параметры AI
    private double aggressiveness = 0.5; // 0.0 - пассивный, 1.0 - агрессивный
    private double intelligence = 0.7;     // 0.0 - глупый, 1.0 - умный
    private double cautiousness = 0.3;   // 0.0 - безрассудный, 1.0 - осторожный

    private Random random = new Random();

    private double playerHealthPercentage;
    private int nearbyAlliesCount;
    private boolean isPlayerRanged;
    private boolean isInCover;
    private AIState currentState;
    private BehaviorPattern currentPattern;

    /**
     * Инициализация AI
     */
    public AdaptiveAI(Entity player, double aggressiveness, double intelligence, double cautiousness) {
        this.player = player;
        this.aggressiveness = aggressiveness;
        this.intelligence = intelligence;
        this.cautiousness = cautiousness;
    }

    @Override
    public void onUpdate(double tpf) {
        /*
        if (player == null || enemyComponent == null) {
            return;
        }

        // Собираем информацию об окружающей среде
        updateBattlefieldInfo();

        // Принимаем решение каждые decisionCooldown миллисекунд
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDecisionTime > decisionCooldown) {
            makeTacticalDecision();
            lastDecisionTime = currentTime;
        }

        // Выполняем текущее поведение
        executeCurrentBehavior(tpf);
        */
    }

    /**
     * Обновить информацию о поле боя
     */
    private void updateBattlefieldInfo() {
        // Процент здоровья игрока
        if (player.getComponent(CreatureComponent.class) != null) {
            playerHealthPercentage = (double) player.getComponent(CreatureComponent.class).getHealthPoints() /
                                   player.getComponent(CreatureComponent.class).getMaxHealthPoints();
        }

        // Количество союзников поблизости
        nearbyAlliesCount = countNearbyAllies();

        // Тип атаки игрока
        isPlayerRanged = isPlayerUsingRangedWeapon();

        // Находится ли враг в укрытии
        isInCover = isInCoverPosition();
    }

    /**
     * Принять тактическое решение
     */
    private void makeTacticalDecision() {
        AIState newState = determineOptimalState();
        BehaviorPattern newPattern = determineOptimalPattern();

        if (newState != currentState || newPattern != currentPattern) {
            currentState = newState;
            currentPattern = newPattern;
            onBehaviorChange();
        }
    }

    /**
     * Определить оптимальное состояние
     */
    private AIState determineOptimalState() {
        double healthPercentage = getHealthPercentage();

        // Низкое здоровье - отступление или ярость
        if (healthPercentage < 0.3) {
            if (aggressiveness > 0.7 && nearbyAlliesCount == 0) {
                return AIState.ENRAGED; // Ярость если агрессивный и один
            } else {
                return AIState.RETREATING; // Отступление если осторожный
            }
        }

        // Среднее здоровье - тактический подход
        if (healthPercentage < 0.6) {
            if (nearbyAlliesCount > 1) {
                return AIState.TACTICAL; // Тактика если есть союзники
            } else {
                return AIState.DEFENSIVE; // Защита если один
            }
        }

        // Высокое здоровье - агрессивный подход
        return AIState.AGGRESSIVE;
    }

    /**
     * Определить оптимальный паттерн поведения
     */
    private BehaviorPattern determineOptimalPattern() {
        double healthPercentage = getHealthPercentage();

        // Умные враги используют сложные паттерны
        if (intelligence > 0.7) {
            if (isPlayerRanged && !isInCover) {
                return BehaviorPattern.FLANK_ATTACK; // Атака с фланга
            }
            if (nearbyAlliesCount > 2) {
                return BehaviorPattern.GANG_UP; // Объединение
            }
            if (isInCover && healthPercentage < 0.5) {
                return BehaviorPattern.AMBUSH; // Засада
            }
        }

        // Осторожные враги держат дистанцию
        if (cautiousness > 0.6) {
            return BehaviorPattern.KITING; // Удерживание дистанции
        }

        // По умолчанию - кружение
        return BehaviorPattern.CIRCLE_PLAYER;
    }

    /**
     * Выполнить текущее поведение
     */
    private void executeCurrentBehavior(double tpf) {
        switch (currentPattern) {
            case CIRCLE_PLAYER:
                executeCirclePlayer(tpf);
                break;
            case FLANK_ATTACK:
                executeFlankAttack(tpf);
                break;
            case AMBUSH:
                executeAmbush(tpf);
                break;
            case KITING:
                executeKiting(tpf);
                break;
            case GANG_UP:
                executeGangUp(tpf);
                break;
        }
    }

    /**
     * Кружение вокруг игрока
     */
    private void executeCirclePlayer(double tpf) {
        if (enemyComponent == null) return;

        double distanceToPlayer = getDistanceToPlayer();
        double optimalDistance = 150.0; // Оптимальная дистанция для кружения

        if (distanceToPlayer > optimalDistance + 50) {
            // Приближаемся
            moveTowardsPlayer(tpf);
        } else if (distanceToPlayer < optimalDistance - 50) {
            // Отдаляемся
            moveAwayFromPlayer(tpf);
        } else {
            // Движемся по кругу
            moveCircularAroundPlayer(tpf);
        }

        // Периодическая атака
        if (distanceToPlayer < optimalDistance + 20) {
            enemyComponent.setPrepAttack(true);
        }
    }

    /**
     * Атака с фланга
     */
    private void executeFlankAttack(double tpf) {
        if (enemyComponent == null) return;

        // Определяем лучший фланг (слева или справа)
        boolean flankingLeft = shouldFlankLeft();

        double targetAngle = flankingLeft ? -90 : 90; // -90 = левый, 90 = правый
        moveToFlankPosition(targetAngle, tpf);

        // Атакуем когда достигли позиции
        if (isInFlankPosition()) {
            enemyComponent.setPrepAttack(true);
        }
    }

    /**
     * Засада из укрытия
     */
    private void executeAmbush(double tpf) {
        if (enemyComponent == null || !isInCover) return;

        // Ждем подхода игрока
        double distanceToPlayer = getDistanceToPlayer();
        if (distanceToPlayer > 200) {
            // Стоим на месте
            enemyComponent.normalizeVelocityX();
            enemyComponent.normalizeVelocityY();
        } else if (distanceToPlayer < 100) {
            // Внезапная атака
            enemyComponent.setPrepAttack(true);
            currentPattern = BehaviorPattern.CIRCLE_PLAYER; // Переходим к обычному поведению
        }
    }

    /**
     * Удерживание дистанции
     */
    private void executeKiting(double tpf) {
        if (enemyComponent == null) return;

        double optimalDistance = 250.0;
        double distanceToPlayer = getDistanceToPlayer();

        if (distanceToPlayer < optimalDistance - 20) {
            // Отдаляемся
            moveAwayFromPlayer(tpf);
        } else if (distanceToPlayer > optimalDistance + 20) {
            // Приближаемся медленно
            moveTowardsPlayer(tpf * 0.5);
        }

        // Дистанционная атака
        if (distanceToPlayer < optimalDistance && distanceToPlayer > 150) {
            enemyComponent.setPrepAttack(true);
        }
    }

    /**
     * Объединение с союзниками
     */
    private void executeGangUp(double tpf) {
        if (enemyComponent == null) return;

        // Двигаемся к ближайшему союзнику
        Entity nearestAlly = findNearestAlly();
        if (nearestAlly != null) {
            moveTowardsEntity(nearestAlly, tpf);
        }

        // Когда объединились, переходим к агрессивному поведению
        if (nearbyAlliesCount >= 3) {
            currentPattern = BehaviorPattern.CIRCLE_PLAYER;
        }
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

    private int countNearbyAllies() {
        // Подсчет врагов в радиусе 200 пикселей
        return (int) FXGL.getGameWorld().getEntitiesInRange(
            getEntity().getBoundingBoxComponent().range(200, 200)
        ).stream()
        .filter(e -> e.getComponent(EnemyComponent.class) != null && e != getEntity())
        .count();
    }

    private boolean isPlayerUsingRangedWeapon() {
        // Проверяем использует ли игрок дистанционное оружие
        return player != null && player.getPosition().distance(getEntity().getPosition()) > 150;
    }

    private boolean isInCoverPosition() {
        // Простая проверка - находимся ли рядом с препятствием
        return false; // Упрощено для примера
    }

    private boolean shouldFlankLeft() {
        // Логика определения фланга
        return random.nextBoolean();
    }

    private boolean isInFlankPosition() {
        // Проверяем находимся ли сбоку от игрока
        double angleToPlayer = Math.toDegrees(Math.atan2(
            player.getY() - getEntity().getY(),
            player.getX() - getEntity().getX()
        ));
        return Math.abs(angleToPlayer) > 60 && Math.abs(angleToPlayer) < 120;
    }

    private void moveTowardsPlayer(double tpf) {
        moveTowardsEntity(player, tpf);
    }

    private void moveAwayFromPlayer(double tpf) {
        double dx = getEntity().getX() - player.getX();
        double dy = getEntity().getY() - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;

            enemyComponent.left(dx * 50 * tpf);
            enemyComponent.up(-dy * 50 * tpf);
        }
    }

    private void moveCircularAroundPlayer(double tpf) {
        double angle = System.currentTimeMillis() * 0.002; // Медленное вращение
        double radius = 150;

        double targetX = player.getX() + Math.cos(angle) * radius;
        double targetY = player.getY() + Math.sin(angle) * radius;

        moveTowardsPosition(targetX, targetY, tpf);
    }

    private void moveToFlankPosition(double targetAngle, double tpf) {
        double currentAngle = Math.toDegrees(Math.atan2(
            player.getY() - getEntity().getY(),
            player.getX() - getEntity().getX()
        ));

        double angleDiff = targetAngle - currentAngle;
        // Нормализуем угол
        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        if (Math.abs(angleDiff) > 10) {
            // Двигаемся к фланговой позиции
            moveWithRotation(angleDiff * 0.5, tpf);
        }
    }

    private void moveTowardsEntity(Entity target, double tpf) {
        if (target == null || getEntity() == null) return;

        double dx = target.getX() - getEntity().getX();
        double dy = target.getY() - getEntity().getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;

            enemyComponent.left(dx * 50 * tpf);
            enemyComponent.up(dy * 50 * tpf);
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

            enemyComponent.left(dx * 50 * tpf);
            enemyComponent.up(dy * 50 * tpf);
        }
    }

    private Entity findNearestAlly() {
        return FXGL.getGameWorld().getEntitiesInRange(
            getEntity().getBoundingBoxComponent().range(300, 300)
        ).stream()
        .filter(e -> e.getComponent(EnemyComponent.class) != null && e != getEntity())
        .min((e1, e2) -> Double.compare(
            e1.getPosition().distance(getEntity().getPosition()),
            e2.getPosition().distance(getEntity().getPosition())
        ))
        .orElse(null);
    }

    private void moveWithRotation(double rotationSpeed, double tpf) {
        double currentRotation = getEntity().getRotation();
        double targetRotation = currentRotation + rotationSpeed * tpf;

        // Двигаемся в направлении нового вращения
        double angle = Math.toRadians(targetRotation);
        enemyComponent.left(Math.cos(angle) * 50 * tpf);
        enemyComponent.up(-Math.sin(angle) * 50 * tpf);
    }

    private void onBehaviorChange() {
        // Логирование изменения поведения
        System.out.println("AI changed behavior: " + currentState + " -> " + currentPattern);
    }

    @Override
    public void onAdded() {
        enemyComponent = getEntity().getComponent(EnemyComponent.class);
    }

    @Override
    public void onRemoved() {
        // Очистка
    }

    // Getters для параметров AI

    public AIState getCurrentState() {
        return currentState;
    }

    public BehaviorPattern getCurrentPattern() {
        return currentPattern;
    }

    public double getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(double aggressiveness) {
        this.aggressiveness = Math.max(0.0, Math.min(1.0, aggressiveness));
    }

    public double getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(double intelligence) {
        this.intelligence = Math.max(0.0, Math.min(1.0, intelligence));
    }

    public double getCautiousness() {
        return cautiousness;
    }

    public void setCautiousness(double cautiousness) {
        this.cautiousness = Math.max(0.0, Math.min(1.0, cautiousness));
    }
}
