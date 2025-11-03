package uwu.openjfx;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.ui.LocalizedText;
import uwu.openjfx.ui.LocalizedButton;

public class GameOverMenu extends FXGLMenu {

    public GameOverMenu(@NotNull MenuType type) {
        super(type);

        // Создаем локализованные компоненты
        LocalizedText title = LocalizedText.createTitle("menu.game_over");
        title.setFill(Color.RED);
        title.setFont(Font.font("Arial Bold", 72.0));

        LocalizedText message = LocalizedText.createDescription("menu.game_over_message");
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Arial", 24.0));

        LocalizedButton btnRestart = LocalizedButton.createPrimary("menu.restart");
        btnRestart.setOnAction(event -> MainMenu.resetToMainMenu());

        // Настраиваем стили контейнера
        VBox vbox = new VBox(50, title, message, btnRestart);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-background-radius: 15px; -fx-padding: 40px;");

        // Позиционирование
        vbox.setTranslateX(FXGL.getAppWidth() / 2 - 200);
        vbox.setTranslateY(FXGL.getAppHeight() / 2 - 150);

        getContentRoot().getChildren().add(vbox);
    }
}
