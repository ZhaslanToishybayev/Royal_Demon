package uwu.openjfx.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

/**
 * Utility helpers for deferred entity cleanup to avoid concurrent modification during update loops.
 */
public final class EntityCleanup {

    private EntityCleanup() {
    }

    public static void removeNextFrame(Entity entity) {
        if (entity == null) {
            return;
        }

        FXGL.getGameTimer().runOnceAfter(() -> {
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
        }, Duration.ZERO);
    }
}
