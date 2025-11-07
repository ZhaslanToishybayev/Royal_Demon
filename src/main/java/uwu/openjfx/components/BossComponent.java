package uwu.openjfx.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uwu.openjfx.MainApp;
import uwu.openjfx.RoyalType;
import uwu.openjfx.UI;
import uwu.openjfx.behaviors.DoNothing;
import uwu.openjfx.hud.ModernGameHUD;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.loopBGM;

// Todo: add more boss behavior here
public class BossComponent extends EnemyComponent {
    private static final IntegerProperty bossHealthProperty = new SimpleIntegerProperty();

    public BossComponent(int healthPoints, String assetName, int width, int height, int frames,
                         String fighterClass) {
        super(healthPoints, assetName, width, height, frames, "finalboss", fighterClass);
        // setDieBehavior(new WinWhenDie());
        setDieBehavior(new DoNothing());
        bossHealthProperty.set(healthPoints);
    }

    @Override
    public void die() {
        if (!(this instanceof DummyBossComponent)) {
            super.die();
            if (!MainApp.isIsTesting()) {
                Entity finalDoor = FXGL.getGameWorld().
                    getEntitiesByType(RoyalType.FINALDOOR).get(0);
                finalDoor.getViewComponent().clearChildren();
                finalDoor.getViewComponent().addChild(FXGL.texture("woodenDoorOpened.png"));
                getAudioPlayer().stopMusic(
                    FXGL.getAssetLoader().loadMusic("boss/boss_battle_ 2.mp3"));
                loopBGM("end/Training Is Over.mp3");
                FXGL.getGameScene().clearUINodes();
                // Исправлено: используем только GameHUD вместо UI.init()
                // UI.init() конфликтовал с GameHUD, создавая дублирующие системы HP
                Entity playerEntity = FXGL.geto("player");
                if (playerEntity != null) {
                    ModernGameHUD.init(playerEntity);
                }
            }
        }
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "UI bindings require direct access to boss health property.")
    public static IntegerProperty getBossHealthProperty() {
        return bossHealthProperty;
    }

    public static void setBossHealthProperty(int bossHealthProperty) {
        BossComponent.bossHealthProperty.set(bossHealthProperty);
    }
}
