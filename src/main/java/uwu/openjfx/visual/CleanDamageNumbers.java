package uwu.openjfx.visual;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Чистая система отображения чисел урона и других визуальных эффектов
 */
public class CleanDamageNumbers {

    /**
     * Показать число урона с улучшенными эффектами
     *
     * @param damage количество урона
     * @param position позиция для отображения
     * @param critical критический ли урон
     */
    public static void showDamageNumber(int damage, Point2D position, boolean critical) {
        Text damageText = createDamageText(damage, critical);
        applyVisualEffects(damageText, critical);

        // Создание анимации
        animateDamageNumber(damageText, position, critical);
    }

    /**
     * Показать число исцеления
     *
     * @param healAmount количество исцеления
     * @param position позиция для отображения
     */
    public static void showHealNumber(int healAmount, Point2D position) {
        Text healText = new Text("+" + healAmount);

        // Настройка внешнего вида для исцеления
        healText.setFill(Color.LIGHTGREEN);
        healText.setFont(Font.font("Arial Bold", 24));
        healText.setStyle("-fx-font-weight: bold;");

        // Эффект свечения
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GREEN);
        glow.setRadius(10);
        healText.setEffect(glow);

        // Анимация исцеления (движение вверх)
        animateHealNumber(healText, position);
    }

    /**
     * Показать надпись о получении предмета
     *
     * @param itemName название предмета
     * @param position позиция для отображения
     */
    public static void showPickupText(String itemName, Point2D position) {
        Text pickupText = new Text("+ " + itemName);

        // Настройка внешнего вида
        pickupText.setFill(Color.GOLD);
        pickupText.setFont(Font.font("Arial", 16));
        pickupText.setStyle("-fx-font-weight: bold;");

        // Анимация появления
        animatePickupText(pickupText, position);
    }

    private static Text createDamageText(int damage, boolean critical) {
        Text damageText = new Text(String.valueOf(damage));

        // Настройка внешнего вида
        if (critical) {
            damageText.setFill(Color.CRIMSON);
            damageText.setFont(Font.font("Arial Bold", 32));
            damageText.setStyle("-fx-font-weight: bold; -fx-stroke: black; -fx-stroke-width: 1px;");
        } else {
            damageText.setFill(Color.ORANGE);
            damageText.setFont(Font.font("Arial Bold", 24));
            damageText.setStyle("-fx-font-weight: bold; -fx-stroke: black; -fx-stroke-width: 0.5px;");
        }

        return damageText;
    }

    private static void applyVisualEffects(Text text, boolean critical) {
        // Добавляем эффект свечения
        DropShadow glow = new DropShadow();
        if (critical) {
            glow.setColor(Color.RED);
            glow.setRadius(15);
            glow.setSpread(0.3);
        } else {
            glow.setColor(Color.ORANGE);
            glow.setRadius(8);
            glow.setSpread(0.2);
        }
        text.setEffect(glow);
    }

    private static void animateDamageNumber(Text damageText, Point2D position, boolean critical) {
        // Позиция
        damageText.setX(position.getX());
        damageText.setY(position.getY());

        // Добавить на экран
        FXGL.getGameScene().addUINode(damageText);

        // Анимация движения вверх с рандомизацией для критических ударов
        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1.5), damageText);
        float offset = critical ? -80 : -60;
        double horizontalOffset = critical ? (Math.random() - 0.5) * 30 : 0;
        moveUp.setToY(offset);
        moveUp.setToX(horizontalOffset);

        // Анимация затухания
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), damageText);
        fadeOut.setToValue(0);

        // Анимация масштаба для критических ударов
        if (critical) {
            ScaleTransition scalePulse = new ScaleTransition(Duration.seconds(0.2), damageText);
            scalePulse.setToX(1.3);
            scalePulse.setToY(1.3);
            scalePulse.setAutoReverse(true);
            scalePulse.setCycleCount(2);
            scalePulse.play();
        }

        // Параллельная анимация
        ParallelTransition parallelTransition = new ParallelTransition(moveUp, fadeOut);
        parallelTransition.play();

        // Удаление после завершения анимации
        parallelTransition.setOnFinished(event -> {
            FXGL.getGameScene().removeUINode(damageText);
        });
    }

    private static void animateHealNumber(Text healText, Point2D position) {
        healText.setX(position.getX());
        healText.setY(position.getY());

        FXGL.getGameScene().addUINode(healText);

        // Анимация движения вверх (более плавная для исцеления)
        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(2.0), healText);
        moveUp.setToY(-70);

        // Анимация затухания
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2.0), healText);
        fadeOut.setToValue(0);

        // Анимация масштаба (появление)
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), healText);
        scaleIn.setToX(1.2);
        scaleIn.setToY(1.2);
        scaleIn.setAutoReverse(true);
        scaleIn.setCycleCount(2);

        SequentialTransition sequential = new SequentialTransition(scaleIn,
            new ParallelTransition(moveUp, fadeOut));

        sequential.play();
        sequential.setOnFinished(event -> {
            FXGL.getGameScene().removeUINode(healText);
        });
    }

    private static void animatePickupText(Text pickupText, Point2D position) {
        pickupText.setX(position.getX());
        pickupText.setY(position.getY());

        // Начинаем с прозрачности 0
        pickupText.setOpacity(0);

        FXGL.getGameScene().addUINode(pickupText);

        // Анимация появления
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), pickupText);
        fadeIn.setToValue(1.0);

        // Пауза
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));

        // Анимация затухания
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), pickupText);
        fadeOut.setToValue(0);

        SequentialTransition sequential = new SequentialTransition(fadeIn, pause, fadeOut);
        sequential.play();
        sequential.setOnFinished(event -> {
            FXGL.getGameScene().removeUINode(pickupText);
        });
    }
}