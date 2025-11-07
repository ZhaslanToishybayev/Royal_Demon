package uwu.openjfx.input;

import com.almasb.fxgl.input.UserAction;
import uwu.openjfx.hud.ModernGameHUD;

public class UseHealthPot extends UserAction {
    public UseHealthPot(String useHealthPot) {
        super(useHealthPot);
    }

    @Override
    protected void onActionBegin() {
        ModernGameHUD.useHealthPot();
    }
}
