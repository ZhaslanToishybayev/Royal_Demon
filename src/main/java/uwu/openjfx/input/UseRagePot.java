package uwu.openjfx.input;

import com.almasb.fxgl.input.UserAction;
import uwu.openjfx.hud.ModernGameHUD;

public class UseRagePot extends UserAction {
    public UseRagePot(String useRagePot) {
        super(useRagePot);
    }

    @Override
    protected void onActionBegin() {
        ModernGameHUD.useRagePot();
    }
}
