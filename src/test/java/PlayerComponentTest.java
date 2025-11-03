import com.almasb.fxgl.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uwu.openjfx.MainApp;
import uwu.openjfx.components.PlayerComponent;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerComponentTest {

    @BeforeEach
    public void init() {
        MainApp.setIsTesting(true);
    }

    @Test
    void testPlayerDies() {
        var player = new Entity();
        var playerComponent = new PlayerComponent(1);
        player.addComponent(playerComponent);
        playerComponent.deductHealth(Double.MAX_VALUE, 1, 0, 1, 0);
        assertTrue(playerComponent.dead());
    }
}