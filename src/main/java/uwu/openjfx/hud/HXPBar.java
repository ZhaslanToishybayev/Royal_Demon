package uwu.openjfx.hud;

import javafx.scene.control.ProgressBar;
import javafx.scene.effect.*;

/**
 * Улучшенная ProgressBar для здоровья с кастомными эффектами
 */
public class HXPBar extends ProgressBar {

    public HXPBar() {
        super();
        setupCustomStyling();
    }

    public HXPBar(double progress) {
        super(progress);
        setupCustomStyling();
    }

    private void setupCustomStyling() {
        // Добавляем внутреннюю тень для глубины
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3));
        innerShadow.setBlurType(BlurType.GAUSSIAN);
        innerShadow.setRadius(5);
        innerShadow.setChoke(0.5);
        setEffect(innerShadow);

        // Устанавливаем начальные стили
        setStyle("-fx-background-radius: 5px;");
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // Можно добавить кастомную логику отрисовки если нужно
    }
}