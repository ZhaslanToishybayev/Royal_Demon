package uwu.openjfx.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class MinionNPCComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animIdle;

    public MinionNPCComponent() {
        animIdle = new AnimationChannel(FXGL.image("creatures/ally/MINION_168x105.png"), 12,
                168, 105, Duration.seconds(0.8), 0, 5);
        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(84, 52));
        entity.getViewComponent().addChild(texture);
    }
}
