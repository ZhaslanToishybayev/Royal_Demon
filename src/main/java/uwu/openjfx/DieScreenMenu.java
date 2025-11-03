package uwu.openjfx;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.ui.LocalizedText;
import uwu.openjfx.ui.LocalizedButton;



public class DieScreenMenu extends FXGLMenu {

    private Rectangle masker = new Rectangle(
            FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0.0, 0.0, 0.0, 0.25));
    private VBox content;

    private Animation<?> animation;

    public DieScreenMenu(MenuType type) {
        super(type);
        content = createContentPane(type);
        content.getChildren().add(createContent());

        content.setTranslateX(FXGL.getAppWidth() / 4.0);
        content.setTranslateY(FXGL.getAppHeight() / 15.0);

        getContentRoot().getChildren().setAll(masker, content);

        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.LINEAR.EASE_IN())
                .translate(content)
                .from(new Point2D(FXGL.getAppWidth() / 4.0, -400.0))
                .to(new Point2D(FXGL.getAppWidth() / 4.0, FXGL.getAppHeight() / 15.0))
                .build();
    }

    @Override
    public void onCreate() {
        animation.start();
    }

    protected void onUpdate(double tpf) {
        animation.onUpdate(tpf);
    }

    private VBox createContentPane(MenuType type) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // Добавляем локализованный заголовок
        LocalizedText titleText = LocalizedText.createTitle("game.game_over");
        titleText.setFill(Color.RED);
        titleText.setFont(Font.font("Arial Bold", 48.0));
        titleText.setStroke(Color.WHITE);
        titleText.setStrokeWidth(2.0);

        vbox.getChildren().add(titleText);
        return vbox;
    }

    private Parent createContent() {
        // Создаем локализованные кнопки
        LocalizedButton btnResume = LocalizedButton.createPrimary("menu.main_menu");
        btnResume.setOnAction(e -> MainMenu.resetToMainMenu());

        LocalizedButton btnExit = LocalizedButton.createDanger("menu.exit");
        btnExit.setOnAction(e -> {
            fireExit();
        });

        HBox buttonBox = new HBox(15.0, btnResume, btnExit);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-background-radius: 10px; -fx-padding: 15px;");

        // Добавляем локализованный текст с сообщением
        LocalizedText messageText = LocalizedText.createDescription("game.defeat_message");
        messageText.setFill(Color.WHITE);
        messageText.setFont(Font.font("Arial", 24.0));
        messageText.setStroke(Color.RED);
        messageText.setStrokeWidth(1.0);

        VBox fullContent = new VBox(20.0, messageText, buttonBox);
        fullContent.setAlignment(Pos.CENTER);
        fullContent.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 15px; -fx-padding: 30px; -fx-border-color: rgba(255,0,0,0.3); -fx-border-width: 2px;");

        return fullContent;
    }
}
