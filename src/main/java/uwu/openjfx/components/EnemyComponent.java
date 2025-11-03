package uwu.openjfx.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IDComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import uwu.openjfx.MainApp;
import uwu.openjfx.map.Room;
import uwu.openjfx.RoyalType;
import uwu.openjfx.utils.EntityCleanup;
import uwu.openjfx.integration.GameIntegration;

import java.util.Timer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import static com.almasb.fxgl.dsl.FXGL.spawn;

public class EnemyComponent extends CreatureComponent {
    private PhysicsComponent physics;

    private double playerX;
    private double playerY;

    private final String type;
    private String fighterClass;
    private boolean massEffect = true;
    private double blockProbability;
    private double armorStat;

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Asset name reused when animations reload (e.g., transformBoss).")
    private String assetName;
    private int width;
    private int height;
    private AnimatedTexture texture;
    private AnimationChannel animIdle;
    private AnimationChannel animWalk;
    private AnimationChannel animMeleeAttack;
    private double enemyX;
    private double enemyY;
    private boolean playerLeavesRadius = false;
    private boolean collidingWithPlayer = false;
    private int attackDuration = 0;
    private static final double VELOCITY_DECREMENTER = 10;
    private double speed;
    private double dist;
    private int kiteTimer;
    private boolean kiteBack;
    private boolean kiteCircular;
    private boolean clockwise;
    private boolean kiting = false;
    private boolean overDrive = false;
    private boolean isStunned = false;
    protected boolean attackCD = false;
    protected boolean prepAttack = false;
    protected boolean startAttacking = false;
    protected boolean startShrink = false;
    private double scaler = 1.0;
    private LocalTimer moveTimer;

    private static final double PLAYER_HIT_BOX_OFFSET_X = 3;
    private static final double PLAYER_HIT_BOX_OFFSET_Y = 15;
    private static final double PLAYER_HIT_BOX_WIDTH = 35;
    private static final double PLAYER_HIT_BOX_HEIGHT = 40;
    private Entity dizzyEffect;

    private int attackBreaktime;
    private static final int ULTIMATE_DURATION = 2000;
    private boolean isHammerSmashing = false;
    private int novaCounter;
    private boolean isMagic360Firing = false;
    private int ricochetCounter;
    private boolean isRicochetFiring = false;

    public EnemyComponent(int maxHP, String assetName, int width, int height, int frames,
                          String type, String fighterClass) {
        super(maxHP, maxHP);

        this.assetName = assetName;
        this.width = width;
        this.height = height;
        this.type = type;
        this.fighterClass = fighterClass;

        if (!MainApp.isIsTesting()) {
            animIdle = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds(0.5), 0, frames / 2 - 1);
            animWalk = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds(0.5), frames / 2, frames - 1);
            animMeleeAttack = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds((double) attackDuration / 1000),
                frames / 2, frames / 2);

            texture = new AnimatedTexture(animIdle);
            texture.loop();
        }
    }

    public void applyDifficulty(double healthMultiplier) {
        if (healthMultiplier <= 0) {
            return;
        }
        int scaledMax = Math.max(1, (int) Math.round(getMaxHealthPoints() * healthMultiplier));
        int scaledCurrent = Math.max(1, (int) Math.round(getHealthPoints() * healthMultiplier));
        setMaxHealthPoints(scaledMax);
        setHealthPoints(scaledCurrent);
    }

    public EnemyComponent(int healthPoints, String assetName, int width, int height) {
        this(healthPoints, assetName, width, height, 8, "small", "melee");
    }

    @Override
    public void onAdded() {
        if (entity.hasComponent(PhysicsComponent.class)) {
            physics = entity.getComponent(PhysicsComponent.class);
        }
        switch (type) {
        case "small":
            massEffect = false;
            blockProbability = 10;
            armorStat = 1;
            break;
        case "miniboss":
            massEffect = true;
            blockProbability = 25;
            armorStat = 4.5;
            break;
        case "finalboss":
            massEffect = true;
            blockProbability = 30;
            armorStat = 25;
            break;
        default:
        }
        speed = !fighterClass.equals("finalboss") ? 25 : 40; // Уменьшил: обычные с 50 до 25, босс с 80 до 40
        attackDuration = fighterClass.equals("melee") ? 900 : 1100;
        if (type.equals("finalboss")) {
            attackBreaktime = 1500;
        } else {
            attackBreaktime = fighterClass.equals("melee") ? 2000 : 2500;
        }

        if (!MainApp.isIsTesting()) {
            entity.getTransformComponent().setScaleOrigin(
                new Point2D(width / 2.0, height / 2.0));
            entity.getViewComponent().addChild(texture);

            moveTimer = FXGL.newLocalTimer();
            moveTimer.capture();
        } else {
            entity.setProperty("isDead", false);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (type.equals("finalboss")) {
            if (getFighterClass().equals("melee") && getHealthPoints() <= 50 && !prepAttack) {
                // Fixed: Transform first, then adjust health for new form
                Entity poof = spawn("weapon",
                    new SpawnData(
                        enemyX - 80, enemyY - 80).
                        put("weaponFile", "poof").
                        put("duration", 1000).
                        put("frameWidth", 32).
                        put("frameHeight", 32).
                        put("fpr", 5));
                poof.setZIndex(2000);
                poof.setScaleX(5);
                poof.setScaleY(5);
                transformBoss("creatures/boss/wizard_40x70.png", 40, 70,
                    9, "ranged");
            }
        }
        Entity player = FXGL.geto("player");
        playerX = player.getX() + PLAYER_HIT_BOX_OFFSET_X + (PLAYER_HIT_BOX_WIDTH / 2);
        playerY = player.getY() + PLAYER_HIT_BOX_OFFSET_Y + (PLAYER_HIT_BOX_HEIGHT / 2);
        enemyX = getEntity().getX() + getEntity().getWidth() / 2;
        enemyY = getEntity().getY() + getEntity().getHeight() / 2;


        double xPythag = Math.abs(playerX - enemyX);
        double yPythag = Math.abs(playerY - enemyY);
        dist = Math.sqrt(xPythag * xPythag + yPythag * yPythag);
        if (dist > 150) {
            playerLeavesRadius = true;
        }
        moveToPlayer();
        if (kiting) {
            kitePlayer();
        }

        if (dizzyEffect != null) {
            dizzyEffect.setAnchoredPosition(
                getEntity().getX() + (getEntity().getWidth() / 2) - (55.0 / 2),
                getEntity().getY() + (getEntity().getHeight() / 2) - (30.0 / 2) - 10);
        }

        if (isStunned) {
            normalizeVelocityX();
            normalizeVelocityY();
            Timeline stunTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(1500), e -> isStunned = false)
            );
            stunTimeline.play();
        }

        if (prepAttack && (physics.getVelocityX() != 0 || physics.getVelocityY() != 0)) {
            normalizeVelocityX();
            normalizeVelocityY();
        }

        if (Math.abs(physics.getVelocityX()) - Math.abs(speed) > 5.0
            || Math.abs(physics.getVelocityY()) - Math.abs(speed) > 5.0) {
            overDrive = true;
        }

        if (overDrive) {
            normalizeVelocityX();
            normalizeVelocityY();
        }

        if (!prepAttack) {
            if (physics.isMoving()) {
                if (texture.getAnimationChannel() != animWalk) {
                    texture.loopAnimationChannel(animWalk);
                }
            } else {
                if (texture.getAnimationChannel() != animIdle) {
                    texture.loopAnimationChannel(animIdle);
                }
            }
        } else {
            attackCD = true;
            normalizeVelocityX();
            normalizeVelocityY();
            if (!type.equals("finalboss")) {
                enlarge();
            }
        }

        if (startAttacking) {
            performAttack();
        }

        if (startShrink) {
            shrink();
        }
    }

    private void moveToPlayer() {
        int attackDist;
        int moveDist;
        if (type.equals("finalboss")) {
            attackDist = fighterClass.equals("melee") ? 150 : 300;
            moveDist = 2000;
        } else {
            attackDist = fighterClass.equals("melee") ? 120 : 300;
            moveDist = fighterClass.equals("melee") ? 300 : 500;
        }
        double reactionTime = type.equals("finalboss") ? 0.25 : 1;
        if (moveTimer.elapsed(Duration.seconds(reactionTime)) && !isStunned) {
            if (fighterClass.equals("melee")) { 
                if (dist < attackDist && !attackCD) { 
                    playerLeavesRadius = false;
                    if (type.equals("finalboss")) {
                        int chooseAttack = MainApp.getRandom().nextInt(101);
                        if (chooseAttack < 42) {
                            prepAttack = true;
                            isHammerSmashing = true;
                            stop();
                            texture.playAnimationChannel(animMeleeAttack);
                            Timeline ultimateTimeline = new Timeline(
                                new KeyFrame(javafx.util.Duration.millis(ULTIMATE_DURATION), e -> startAttacking = true)
                            );
                            ultimateTimeline.play();
                            hammerUltimatePrepAttack();
                        } else {
                            initiateAutoAttack();
                        }
                    } else {
                        initiateAutoAttack();
                    }
                } else if (dist < moveDist && !prepAttack) { 
                    double xDir = playerX - enemyX > 0 ? 1 : -1;
                    double yDir = playerY - enemyY > 0 ? 1 : -1;
                    physics.setVelocityX(speed * xDir);
                    physics.setVelocityY(speed * yDir);
                    entity.setScaleX(xDir);
                } else {
                    stop();
                }
            } else { 
                if (dist < attackDist) {
                    if (!attackCD) {
                        if (type.equals("finalboss")) {
                            int chooseAttack = MainApp.getRandom().nextInt(101);
                            if (chooseAttack < 58) {
                                int chooseUltimate;
                                do { 
                                    chooseUltimate = MainApp.getRandom().nextInt(101);
                                } while ((novaCounter >= 2 && chooseUltimate < 50)
                                    || (ricochetCounter >= 2 && chooseUltimate >= 50));
                                if (chooseUltimate < 50) {
                                    ricochetCounter = 0;
                                    prepAttack = true;
                                    stop();
                                    texture.playAnimationChannel(animMeleeAttack);
                                    magicUltimate360PrepAttack();
                                    isMagic360Firing = true;
                                    novaCounter++;
                                } else {
                                    novaCounter = 0;
                                    prepAttack = true;
                                    stop();
                                    texture.playAnimationChannel(animMeleeAttack);
                                    magicUltimateRicochetPrepAttack();
                                    isRicochetFiring = true;
                                    ricochetCounter++;
                                }
                                Timeline ultimateTimeline2 = new Timeline(
                                    new KeyFrame(javafx.util.Duration.millis(ULTIMATE_DURATION), e -> startAttacking = true)
                                );
                                ultimateTimeline2.play();
                            } else {
                                initiateAutoAttack();
                            }
                        } else {
                            initiateAutoAttack();
                        }
                    }
                    if (!prepAttack && !kiting) {
                        kiting = true;
                    }
                } else if (dist < moveDist && !prepAttack) {
                    double xDir = playerX - enemyX > 0 ? 1 : -1;
                    double yDir = playerY - enemyY > 0 ? 1 : -1;
                    physics.setVelocityX(speed * xDir);
                    physics.setVelocityY(speed * yDir);
                    entity.setScaleX(xDir);
                } else {
                    stop();
                }
            }
            moveTimer.capture();
        }
    }

    public void normalizeVelocityX() {
        if (physics.getVelocityX() != 0) {
            if (physics.getVelocityX() > 0) {
                physics.setVelocityX(physics.getVelocityX() - VELOCITY_DECREMENTER);
                if (physics.getVelocityX() < 0) {
                    physics.setVelocityX(0);
                    overDrive = false;
                }
            } else if (physics.getVelocityX() < 0) {
                physics.setVelocityX(physics.getVelocityX() + VELOCITY_DECREMENTER);
                if (physics.getVelocityX() > 0) {
                    physics.setVelocityX(0);
                    overDrive = false;
                }
            }
        }
    }

    public void normalizeVelocityY() {
        if (physics.getVelocityY() != 0) {
            if (physics.getVelocityY() > 0) {
                physics.setVelocityY(physics.getVelocityY() - VELOCITY_DECREMENTER);
                if (physics.getVelocityY() < 0) {
                    physics.setVelocityY(0);
                    overDrive = false;
                }
            } else if (physics.getVelocityY() < 0) {
                physics.setVelocityY(physics.getVelocityY() + VELOCITY_DECREMENTER);
                if (physics.getVelocityY() > 0) {
                    physics.setVelocityY(0);
                    overDrive = false;
                }
            }
        }
    }

    public void knockBackFromPlayer() {
        if (physics != null && !type.equals("finalboss")) {
            isStunned = true;
            double knockBackPower = 400;
            double adjacent = (getEntity().getX() + ((double) width) / 2) - playerX;
            double opposite = (getEntity().getY() + ((double) height) / 2) - playerY;
            double angle = Math.atan2(opposite, adjacent);
            angle = Math.toDegrees(angle);
            Vec2 dir = Vec2.fromAngle(angle);
            double xPow = dir.toPoint2D().getX() * knockBackPower;
            double yPow = dir.toPoint2D().getY() * knockBackPower;
            physics.setLinearVelocity(new Point2D(xPow, yPow));
            if (dizzyEffect != null) {
                dizzyEffect.removeFromWorld();
            }
            dizzyEffect = spawn("weapon",
                new SpawnData(
                    enemyX, enemyY).
                    put("weaponFile", "dizzyEffect_55x30").
                    put("duration", 1500).
                    put("frameWidth", 55).
                    put("frameHeight", 30).
                    put("fpr", 6));
            dizzyEffect.setZIndex(5);
        }
    }

    private void kitePlayer() {
        if (physics != null && !prepAttack) {
            getEntity().setScaleX(playerX - enemyX > 0 ? 1 : -1);
            if (!kiteBack && !kiteCircular) {
                int random = MainApp.getRandom().nextInt(101);
                kiteBack = random < 35;
                kiteCircular = random >= 35;
                if ((physics.getVelocityX() == 0 && physics.getVelocityY() == 0) && !prepAttack) {
                    kiteTimer++;
                    if (kiteTimer >= 60) {
                        if (kiteCircular) {
                            clockwise = !clockwise;
                        }
                        kiteBack = false;
                        kiteCircular = true;
                        kiteTimer = 0;
                    }
                }
            }
            double adjacent = (getEntity().getX()
                + ((double) width) / 2) - playerX;
            double opposite = (getEntity().getY()
                + ((double) height) / 2) - playerY;
            double radians = (Math.atan2(opposite, adjacent));
            if (kiteCircular) {
                radians = radians + (clockwise ? Math.PI / 2 : -(Math.PI / 2));
            }
            Vec2 dir = Vec2.fromAngle(Math.toDegrees(radians));
            double xPow = dir.toPoint2D().getX() * speed;
            double yPow = dir.toPoint2D().getY() * speed;
            physics.setLinearVelocity(xPow, yPow);
            FXGL.getGameTimer().runAtInterval(() -> {
                kiteBack = false;
                kiteCircular = false;
            }, Duration.seconds(1));
            if (dist >= 300) {
                kiting = false;
                stop();
            }
        }
    }

    private void stop() {
        if (!collidingWithPlayer) {
            physics.setVelocityX(0);
            physics.setVelocityY(0);
        }
    }

    public double getBlockProbability() {
        return blockProbability;
    }

    public void setBlockProbability(double blockProbability) {
        this.blockProbability = blockProbability;
    }

    public double getArmorStat() {
        return armorStat;
    }

    public void setArmorStat(double armorStat) {
        this.armorStat = armorStat;
    }

    private void performAttack() {
        if (fighterClass.equals("melee")) { 
            if (!type.equals("finalboss")) { 
                meleePunch();
                int random = MainApp.getRandom().nextBoolean() ? 1 : 0;
                String attSound = random == 1 ? "mob/minion_1.wav" : "mob/minion_2.wav";
                FXGL.play(attSound);
            } else { 
                if (!isHammerSmashing) { 
                    hammerAttack();
                    FXGL.play("skills/sword_basic.wav");
                } else {
                    hammerUltimateSmash();
                    FXGL.play("skills/explosion_largest2.wav");
                }
            }
        } else { 
            if (!type.equals("finalboss")) {
                magicAutoAttack();
                FXGL.play("skills/fireball 2.wav");
            } else {
                if (isMagic360Firing) {
                    magicUltimate360Fire();
                    FXGL.play("skills/explosion_largest.wav");
                } else if (isRicochetFiring) {
                    magicUltimateRicochetFire();
                    FXGL.play("skills/fireball3.wav");
                } else {
                    magicAutoAttack();
                    FXGL.play("skills/fireball3.wav");
                }
            }
        }
        startShrink = true;
        startAttacking = false;
        prepAttack = false;
        isHammerSmashing = false;
        isMagic360Firing = false;
        isRicochetFiring = false;
        Timeline cooldownTimeline = new Timeline(
            new KeyFrame(javafx.util.Duration.millis(attackBreaktime), e -> attackCD = false)
        );
        cooldownTimeline.play();
    }

    private void initiateAutoAttack() {
        prepAttack = true;
        stop();
        texture.playAnimationChannel(animMeleeAttack);
        if (type.equals("finalboss")) {
            bossPrepAttack();
        } else if (fighterClass.equals("ranged")) {
            int width = 16;
            int height = 16;
            int handOffset = 7;
            Entity magicHB = spawn("weapon",
                new SpawnData(
                    getEntity().getX(), getEntity().getY()).
                    put("weaponFile", "fireCharge_16x16").
                    put("duration", attackDuration).
                    put("frameWidth", width).
                    put("frameHeight", height).
                    put("fpr", 15));
            magicHB.getTransformComponent().setAnchoredPosition(
                new Point2D(entity.getX() - ((double) width / 2) + entity.getWidth() / 2
                    + handOffset,
                    entity.getY() - ((double) height / 2) + entity.getHeight() / 2 + 15));
            magicHB.setZIndex(5);
            magicHB.setScaleX(1.5);
            magicHB.setScaleY(1.5);
            if (entity.getScaleX() == 1) {
                magicHB.setScaleX(1.5);
            } else {
                magicHB.translateX(width - 2 * handOffset);
                magicHB.setScaleX(-1.5);
            }
        }
        Timer t = new java.util.Timer();
        t.schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    startAttacking = true;
                    t.cancel();
                }
            }, attackDuration
        );
    }

    public void rangedAttack() {
        double adjacent = playerX - enemyX;
        double opposite = playerY - enemyY;
        double angle = Math.atan2(opposite, adjacent);
        angle = Math.toDegrees(angle);
        Vec2 dir = Vec2.fromAngle(angle);

        int topBottomOffset = 20;
        int leftOffset = 30;
        int rightOffset = 20;
        int frameWidth = 64;
        int frameHeight = 64;

        double centerX = ((double) (leftOffset + (frameWidth - rightOffset)) / 2);
        double centerY = ((double) (topBottomOffset + (frameHeight - topBottomOffset)) / 2);

        int speed = 200; // Уменьшил скорость снарядов для лучшего баланса

        entity.setScaleX(playerX - enemyX > 0 ? 1 : -1);
        Entity magicRegAutoHB = spawn("rangedMagicHitBox",
            new SpawnData(
                enemyX, enemyY).
                put("dir", dir.toPoint2D()).
                put("speed", speed).
                put("weapon", !type.equals("finalboss")
                    ? "fireball_enemy_64x64" : "fireball_boss_64x64").
                put("duration", 500).
                put("fpr", 60).
                put("ultimateActive", true).
                put("topBotOffset", topBottomOffset).
                put("leftOffset", leftOffset).
                put("rightOffset", rightOffset).
                put("frameWidth", frameWidth).
                put("frameHeight", frameHeight).
                put("isArrow", false).
                put("isMagic", true).
                put("damage", 1.0).
                put("royalType", RoyalType.ENEMYATTACK));
        magicRegAutoHB.setLocalAnchor(new Point2D(centerX, centerY));
        magicRegAutoHB.setAnchoredPosition(enemyX, enemyY);
        magicRegAutoHB.getTransformComponent().setRotationOrigin(
            new Point2D(centerX, ((double) (frameHeight)) / 2));
        magicRegAutoHB.setZIndex(5);
    }

    private void magicAutoAttack() {
        rangedAttack();
    }

    private void meleePunch() {
        int hitBoxWidth = (int) getEntity().getBoundingBoxComponent().getWidth() * 2;
        int hitBoxHeight = (int) getEntity().getBoundingBoxComponent().getHeight() * 2;
        double swordOffset = (double) hitBoxWidth / 2;

        Entity punch = spawn("meleeEnemyPunch",
            new SpawnData(getEntity().getX(), getEntity().getY()).
                put("widthBox", hitBoxWidth).put("heightBox", hitBoxHeight));
        punch.getTransformComponent().setAnchoredPosition(
            new Point2D(
                enemyX - ((double) hitBoxWidth / 2)
                    + (getEntity().getScaleX() > 0 ? swordOffset : -swordOffset),
                enemyY - ((double) hitBoxHeight / 2)));
        EntityCleanup.removeNextFrame(punch);
    }

    private void bossPrepAttack() {
        if (fighterClass.equals("melee")) {
            int width = 255;
            int height = 130;
            int offsetX = 35;
            int offsetY = -10;
            Entity hammerAutoHB = spawn("weapon",
                new SpawnData(
                    getEntity().getX(), getEntity().getY()).
                    put("weaponFile", "bossAttack_255x130_2").
                    put("duration", attackDuration).
                    put("frameWidth", width).
                    put("frameHeight", height).
                    put("fpr", 5));
            hammerAutoHB.getTransformComponent().setAnchoredPosition(new Point2D(
                entity.getX() - ((double) width / 2) + entity.getWidth() / 2 + offsetX,
                entity.getY() - ((double) height / 2) + entity.getHeight() / 2 + offsetY));
            hammerAutoHB.setZIndex(5);
            if (entity.getScaleX() == 1) {
                hammerAutoHB.setScaleX(1);
            } else {
                hammerAutoHB.translateX(width - 2 * offsetX + 20);
                hammerAutoHB.setScaleX(-1);
            }
        } else {
            int width = 16;
            int height = 16;
            int handOffset = 15;
            Entity magicBossAutoHB = spawn("weapon",
                new SpawnData(
                    getEntity().getX(), getEntity().getY()).
                    put("weaponFile", "fireCharge_16x16").
                    put("duration", attackDuration).
                    put("frameWidth", width).
                    put("frameHeight", height).
                    put("fpr", 15));
            magicBossAutoHB.getTransformComponent().setAnchoredPosition(
                new Point2D(entity.getX() - ((double) width / 2) + entity.getWidth() / 2
                    + handOffset,
                    entity.getY() - ((double) height / 2) + entity.getHeight() / 2 + 15));
            magicBossAutoHB.setZIndex(5);
            magicBossAutoHB.setScaleX(1.5);
            magicBossAutoHB.setScaleY(1.5);
            if (entity.getScaleX() == 1) {
                magicBossAutoHB.setScaleX(1.5);
            } else {
                magicBossAutoHB.translateX(width - 2 * handOffset);
                magicBossAutoHB.setScaleX(-1.5);
            }
        }
    }

    private void hammerAttack() {
        int hitBoxWidth = 160;
        int hitBoxHeight = 160;
        double swordOffset = 55;

        Entity hammerHitBox = spawn("meleeEnemyPunch",
            new SpawnData(getEntity().getX(), getEntity().getY()).
                put("widthBox", hitBoxWidth).put("heightBox", hitBoxHeight));
        hammerHitBox.getTransformComponent().setAnchoredPosition(
            new Point2D(
                getEntity().getX() - ((double) hitBoxWidth / 2) + getEntity().getWidth() / 2
                    + (getEntity().getScaleX() > 0 ? swordOffset : -swordOffset),
                getEntity().getY() - ((double) hitBoxHeight / 2) + getEntity().getHeight() / 2));

        EntityCleanup.removeNextFrame(hammerHitBox);
    }

    private void hammerUltimatePrepAttack() {
        int width = 215;
        int height = 175;
        int offsetX = 16;
        int offsetY = -27;
        FXGL.play("skills/charge_hammer.wav");
        Entity hammerUltimateHB = spawn("weapon",
            new SpawnData(
                getEntity().getX(), getEntity().getY()).
                put("weaponFile", "bossUltSpriteSheet_215x175").
                put("duration", ULTIMATE_DURATION).
                put("frameWidth", width).
                put("frameHeight", height).
                put("fpr", 10));
        hammerUltimateHB.getTransformComponent().setAnchoredPosition(
            new Point2D(entity.getX() - ((double) width / 2) + entity.getWidth() / 2 + offsetX,
                entity.getY() - ((double) height / 2) + entity.getHeight() / 2 + offsetY));
        hammerUltimateHB.setZIndex(5);
        if (entity.getScaleX() == 1) {
            hammerUltimateHB.setScaleX(1);
        } else {
            hammerUltimateHB.translateX(width - 2 * offsetX + 20);
            hammerUltimateHB.setScaleX(-1);
        }
    }

    private void hammerUltimateSmash() {
        int hitBoxWidth = 350;
        int hitBoxHeight = 350;
        double swordOffset = 0;

        Entity hammerHitBox = spawn("meleeEnemyPunch",
            new SpawnData(getEntity().getX(), getEntity().getY()).
                put("widthBox", hitBoxWidth).put("heightBox", hitBoxHeight));
        hammerHitBox.addComponent(new GroundSmashComponent());
        hammerHitBox.getTransformComponent().setAnchoredPosition(
            new Point2D(
                getEntity().getX() - ((double) hitBoxWidth / 2) + getEntity().getWidth() / 2
                    + (getEntity().getScaleX() > 0 ? swordOffset : -swordOffset),
                getEntity().getY() - ((double) hitBoxHeight / 2) + getEntity().getHeight() / 2));
        hammerHitBox.setType(RoyalType.SMASHEDGROUND);
        Entity sg = spawn("weapon",
            new SpawnData(
                getEntity().getX(), getEntity().getY()).
                put("weaponFile", "smashedGround").
                put("duration", 4000).
                put("frameWidth", hitBoxWidth).
                put("frameHeight", hitBoxHeight).
                put("fpr", 1));
        sg.getTransformComponent().setAnchoredPosition(
            new Point2D(
                getEntity().getX() - ((double) hitBoxWidth / 2) + getEntity().getWidth() / 2
                    + (getEntity().getScaleX() > 0 ? swordOffset : -swordOffset),
                getEntity().getY() - ((double) hitBoxHeight / 2) + getEntity().getHeight() / 2));
    }

    private void magicUltimate360PrepAttack() {
        int width = 100;
        int height = 100;
        FXGL.play("skills/charge_boss.wav");

        Entity magic360AuraHB = spawn("weapon",
            new SpawnData(
                enemyX - ((double) width / 2) + 4, enemyY - ((double) height / 2) + 10).
                put("weaponFile", "fire360_100x100").
                put("duration", ULTIMATE_DURATION).
                put("frameWidth", width).
                put("frameHeight", height).
                put("fpr", 60));
        magic360AuraHB.setZIndex(5);
    }

    private void magicUltimate360Fire() {
        int topBottomOffset = 20;
        int leftOffset = 30;
        int rightOffset = 20;
        int frameWidth = 64;
        int frameHeight = 64;

        double centerX = ((double) (leftOffset + (frameWidth - rightOffset)) / 2);
        double centerY = ((double) (topBottomOffset + (frameHeight - topBottomOffset)) / 2);

        int speed = 200; // Уменьшил скорость снарядов для лучшего баланса
        int amountOfFireballs = 35;
        Vec2[] angles = new Vec2[amountOfFireballs];
        double angleIncrementer = 2 * Math.PI / amountOfFireballs;
        double angle = 0;
        for (int i = 0; i < angles.length; i++) {
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            angles[i] = new Vec2(new Point2D(x, y));
            angle += angleIncrementer;
        }
        for (Vec2 vec : angles) {
            Entity rangedHitBox = spawn("rangedMagicHitBox",
                new SpawnData(
                    enemyX, enemyY).
                    put("dir", vec.toPoint2D()).
                    put("speed", speed).
                    put("weapon", !type.equals("finalboss")
                        ? "fireball_enemy_64x64" : "fireball_boss_64x64").
                    put("duration", 500).
                    put("fpr", 60).
                    put("ultimateActive", false).
                    put("topBotOffset", topBottomOffset).
                    put("leftOffset", leftOffset).
                    put("rightOffset", rightOffset).
                    put("frameWidth", frameWidth).
                    put("frameHeight", frameHeight).
                    put("isArrow", false).
                    put("isMagic", true).
                    put("damage", 1.0).
                    put("royalType", RoyalType.ENEMYATTACK));
            rangedHitBox.setLocalAnchor(new Point2D(centerX, centerY));
            rangedHitBox.setAnchoredPosition(enemyX, enemyY);
            rangedHitBox.getTransformComponent().setRotationOrigin(
                new Point2D(centerX, ((double) (frameHeight)) / 2));
            rangedHitBox.setZIndex(5);
        }
    }

    private void magicUltimateRicochetPrepAttack() {
        int width = 32;
        int height = 32;
        int handOffset = 22;
        Entity magicUltimateRicoHB = spawn("weapon",
            new SpawnData(
                getEntity().getX(), getEntity().getY()).
                put("weaponFile", "orangeNovaBall_32x32").
                put("duration", attackDuration).
                put("frameWidth", width).
                put("frameHeight", height).
                put("fpr", 32));
        magicUltimateRicoHB.getTransformComponent().setAnchoredPosition(
            new Point2D(entity.getX() - ((double) width / 2) + entity.getWidth() / 2
                + handOffset,
                entity.getY() - ((double) height / 2) + entity.getHeight() / 2 + 15));
        magicUltimateRicoHB.setZIndex(5);
        if (entity.getScaleX() == 1) {
            magicUltimateRicoHB.setScaleX(1);
        } else {
            magicUltimateRicoHB.translateX(width - 2 * handOffset);
            magicUltimateRicoHB.setScaleX(-1);
        }
    }

    private void magicUltimateRicochetFire() {
        double adjacent = playerX - enemyX;
        double opposite = playerY - enemyY;
        double angle = Math.atan2(opposite, adjacent);
        angle = Math.toDegrees(angle);
        Vec2 dir = Vec2.fromAngle(angle);

        int topBottomOffset = 40;
        int leftOffset = 50;
        int rightOffset = 25;
        int frameWidth = 100;
        int frameHeight = 100;

        double centerX = ((double) (leftOffset + (frameWidth - rightOffset)) / 2);
        double centerY = ((double) (topBottomOffset + (frameHeight - topBottomOffset)) / 2);

        int speed = 200; // Уменьшил скорость снарядов для лучшего баланса

        entity.setScaleX(playerX - enemyX > 0 ? 1 : -1);
        Entity rangedHitBox = spawn("rangedMagicHitBox",
            new SpawnData(
                enemyX, enemyY).
                put("dir", dir.toPoint2D()).
                put("speed", speed).
                put("weapon", "ricochetBall_100x100").
                put("duration", 500).
                put("fpr", 60).
                put("ultimateActive", true).
                put("topBotOffset", topBottomOffset).
                put("leftOffset", leftOffset).
                put("rightOffset", rightOffset).
                put("frameWidth", frameWidth).
                put("frameHeight", frameHeight).
                put("isArrow", false).
                put("isMagic", true).
                put("damage", 1.0).
                put("royalType", RoyalType.ENEMYATTACK));
        rangedHitBox.addComponent(new RicochetComponent());
        rangedHitBox.setLocalAnchor(new Point2D(centerX, centerY));
        rangedHitBox.setAnchoredPosition(enemyX, enemyY);
        rangedHitBox.getTransformComponent().setRotationOrigin(
            new Point2D(centerX, ((double) (frameHeight)) / 2));
        rangedHitBox.setZIndex(5);
    }

    private void enlarge() {
        entity.setScaleX(scaler * Math.signum(entity.getScaleX()));
        entity.setScaleY(scaler * Math.signum(entity.getScaleY()));
        if (scaler < 1.2) {
            scaler += 0.005;
        }
    }

    private void shrink() {
        entity.setScaleX(scaler * Math.signum(entity.getScaleX()));
        entity.setScaleY(scaler * Math.signum(entity.getScaleY()));
        if (scaler > 1) {
            scaler -= 0.01;
        } else {
            scaler = 1;
            startShrink = false;
        }
    }

    @Override
    public void die() {
        super.die();
        if (!MainApp.isIsTesting()) {
            // Get IDComponent BEFORE removing from world
            int entityId = -1;
            if (getEntity().hasComponent(IDComponent.class)) {
                IDComponent idComponent = getEntity().getComponent(IDComponent.class);
                entityId = idComponent.getId();
            }

            getEntity().removeFromWorld();
            if (dizzyEffect != null) {
                dizzyEffect.removeFromWorld();
            }

            // Update room data if we have a valid ID
            if (entityId != -1) {
                Room curRoom = FXGL.geto("curRoom");
                curRoom.setEntityData(entityId, "isAlive", 0);
            }

            try {
                int experience = type.equals("finalboss") ? 100 :
                                 type.equals("miniboss") ? 50 : 25;
                
                int damage = fighterClass.equals("melee") ? 20 : 15;
                if (type.equals("miniboss")) damage = 35;
                if (type.equals("finalboss")) damage = 50;
                
                GameIntegration.onEnemyDefeated(
                    damage,
                    experience,
                    new javafx.geometry.Point2D(enemyX, enemyY),
                    false
                );
            } catch (Exception e) {
            }

            // Check for challenge room completion
            Room curRoom = FXGL.geto("curRoom");
            if (curRoom.getRoomType().equals("challengeRoom") && curRoom.enemiesCleared()) {
                Entity tempChest = FXGL.spawn("chest", 480, 387);
                tempChest.addComponent(new IDComponent("", 999));

                try {
                    uwu.openjfx.components.PlayerComponent.onChestOpened();
                } catch (Exception e) {
                }
            }
        }
    }

    public String getFighterClass() {
        return fighterClass;
    }

    public String getType() {
        return type;
    }

    public String transformBoss(String assetName, int width, int height, int frames,
                              String fighterClass) {
        this.assetName = assetName;
        this.width = width;
        this.height = height;
        this.fighterClass = fighterClass;

        if (!MainApp.isIsTesting()) {
            animIdle = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds(0.5), 0, frames / 2 - 1);
            animWalk = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds(0.5), frames / 2, frames - 1);
            animMeleeAttack = new AnimationChannel(FXGL.image(assetName), frames,
                width, height, Duration.seconds((double) attackDuration / 1000),
                frames / 2, frames / 2);
            getEntity().getBoundingBoxComponent().clearHitBoxes();
            getEntity().getBoundingBoxComponent().addHitBox(new HitBox(
                new Point2D(
                    5,
                    5),
                BoundingShape.box(width - 5, height - 5)));
            entity.getTransformComponent().setScaleOrigin(
                new Point2D(width / 2.0, height / 2.0));
            texture.set(new AnimatedTexture(animIdle));
            texture.loop();
            speed = 40; // Уменьшил с 80 до 40
            attackDuration = 1100;
            attackBreaktime = 2000;
        } else {
            return "transformed";
        }
        return null;
    }

    public boolean getPlayerLeavesRadius() {
        return playerLeavesRadius;
    }

    public void setCollidingWithPlayer(boolean collidingWithPlayer) {
        this.collidingWithPlayer = collidingWithPlayer;
    }

    public boolean getMassEffect() {
        return massEffect;
    }

    public void turnSpriteRed() {
        if (texture != null) {
            javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
            colorAdjust.setBrightness(0.5);
            texture.setEffect(colorAdjust);
            com.almasb.fxgl.dsl.FXGL.getGameTimer().runOnceAfter(() -> {
                texture.setEffect(null);
            }, javafx.util.Duration.seconds(0.1));
        }
    }
    public void move(double dx, double dy) {
        physics.setVelocityX(dx);
        physics.setVelocityY(dy);
    }

    public void setPrepAttack(boolean prepAttack) {
        this.prepAttack = prepAttack;
    }

    public void left(double distance) {
        physics.setVelocityX(-distance);
    }

    public void up(double distance) {
        physics.setVelocityY(-distance);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}