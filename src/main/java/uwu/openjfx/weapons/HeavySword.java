package uwu.openjfx.weapons;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import uwu.openjfx.core.GameEnvironment;
import uwu.openjfx.utils.EntityCleanup;

import static com.almasb.fxgl.dsl.FXGL.spawn;

public class HeavySword implements Weapon {
    private boolean ultimateActivated;
    private static final double ATTACK_DAMAGE = GameEnvironment.get().getWeaponBalanceService().getAttackDamage("heavy_sword", 80);

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HeavySword;
    }

    @Override
    public void prepAttack(Entity player) {
        int width = 69;
        int height = 26;
        double swordOffset = 22;
        String chosenAttack = "gold_knife_stab_69x26";

        Entity gs = spawn("weapon",
            new SpawnData(
                player.getX(), player.getY()).
                put("weaponFile", chosenAttack).
                put("duration", getDuration(ultimateActivated)).
                put("frameWidth", width).
                put("frameHeight", height).
                put("fpr", 3));
        gs.getTransformComponent().setAnchoredPosition(
            new Point2D(
                player.getX() - ((double) width / 2) + player.getWidth() / 2
                    + (player.getScaleX() > 0 ? swordOffset : -swordOffset),
                player.getY() - ((double) height / 2) + player.getHeight() / 2));
        gs.setZIndex(5);
        if (player.getScaleX() == 1) {
            gs.setScaleX(1);
        } else {
            gs.translateX(width);
            gs.setScaleX(-1);
        }
    }

    @Override
    public void attack(Entity player, double mouseCurrX, double mouseCurrY) {
        int hitBoxWidth = 80;
        int hitBoxHeight = 90;
        double swordOffset = 25;
        String attSound = "skills/sword_basic.wav";
        FXGL.play(attSound);

        Entity meleeHitBox = spawn("meleeSwordHitBox",
            new SpawnData(player.getX(), player.getY()).
                put("width", hitBoxWidth).put("height", hitBoxHeight).
                put("damage", ATTACK_DAMAGE));
        meleeHitBox.getTransformComponent().setAnchoredPosition(
            new Point2D(
                player.getX() - ((double) hitBoxWidth / 2) + player.getWidth() / 2
                    + (player.getScaleX() > 0 ? swordOffset : -swordOffset),
                player.getY() - ((double) hitBoxHeight / 2) + player.getHeight() / 2));
        EntityCleanup.removeNextFrame(meleeHitBox);
    }

    @Override
    public int getDuration(boolean ultimateActivated) {
        int attackDuration = 800;
        this.ultimateActivated = ultimateActivated;
        return attackDuration;
    }

    @Override
    public Image getWeaponSprite() {
        return new Image("assets/textures/ui/inventory/sword2.png");
    }

    @Override
    public String getWeaponIconPath() {
        return "ui/inventory/sword2.png";
    }

    @Override
    public String getName() {
        return "Heavy Sword";
    }

    @Override
    public String getDescription() {
        return "A heavy sword that deals a lot of damage, but is slow.";
    }

    @Override
    public boolean isMeleeAttack() {
        return true;
    }

    @Override
    public int getUltimateCD() {
        return 0;
    }
}
