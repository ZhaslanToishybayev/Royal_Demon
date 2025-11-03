package uwu.openjfx.visual;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 * Менеджер визуальных эффектов для улучшения геймплея
 */
public class VisualEffectsManager {
    private static VisualEffectsManager instance;

    private VisualEffectsManager() {
    }

    public static VisualEffectsManager getInstance() {
        if (instance == null) {
            instance = new VisualEffectsManager();
        }
        return instance;
    }

    /**
     * Создает эффект взрыва
     *
     * @param position позиция взрыва
     * @param size размер эффекта
     */
    public void createExplosionEffect(Point2D position, int size) {
        // Ограничиваем количество частиц для производительности (максимум 15)
        int actualSize = Math.min(size, 15);

        // Создаем несколько частиц для взрыва
        for (int i = 0; i < actualSize; i++) {
            Circle particle = new Circle(3 + Math.random() * 4);
            particle.setFill(Color.ORANGE);
            particle.setCenterX(position.getX());
            particle.setCenterY(position.getY());

            // Эффект свечения
            Glow glow = new Glow(0.8);
            particle.setEffect(glow);

            // Добавляем на сцену
            FXGL.getGameScene().addUINode(particle);

            // Анимация разлета частиц
            double angle = (Math.PI * 2 * i) / actualSize;
            double velocity = 50 + Math.random() * 100;

            // Движение
            TranslateTransition move = new TranslateTransition(Duration.seconds(1.0), particle);
            move.setByX(Math.cos(angle) * velocity);
            move.setByY(Math.sin(angle) * velocity);

            // Масштабирование
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), particle);
            scale.setToX(0.1);
            scale.setToY(0.1);

            // Затухание
            FadeTransition fade = new FadeTransition(Duration.seconds(1.0), particle);
            fade.setToValue(0);

            // Параллельная анимация
            ParallelTransition animation = new ParallelTransition(move, scale, fade);
            animation.play();

            // Удаление после анимации
            animation.setOnFinished(event -> {
                FXGL.getGameScene().removeUINode(particle);
            });
        }
    }

    /**
     * Создает эффект исцеления
     *
     * @param position позиция эффекта
     */
    public void createHealEffect(Point2D position) {
        // Создаем кольцевые частицы (уменьшено с 8 до 6 для производительности)
        for (int i = 0; i < 6; i++) {
            Circle healParticle = new Circle(2);
            healParticle.setFill(Color.LIGHTGREEN);
            healParticle.setCenterX(position.getX());
            healParticle.setCenterY(position.getY());

            // Эффект свечения
            Glow glow = new Glow(0.6);
            healParticle.setEffect(glow);

            FXGL.getGameScene().addUINode(healParticle);

            // Анимация движения вверх
            double angle = (Math.PI * 2 * i) / 6;
            TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), healParticle);
            move.setByY(-50);
            move.setByX(Math.cos(angle) * 20);

            // Анимация затухания
            FadeTransition fade = new FadeTransition(Duration.seconds(1.5), healParticle);
            fade.setToValue(0);

            ParallelTransition animation = new ParallelTransition(move, fade);
            animation.play();
            animation.setOnFinished(event -> {
                FXGL.getGameScene().removeUINode(healParticle);
            });
        }
    }

    /**
     * Создает эффект подбора предмета
     *
     * @param position позиция эффекта
     * @param color цвет эффекта
     */
    public void createPickupEffect(Point2D position, Color color) {
        // Создаем пульсирующее кольцо
        Circle ring = new Circle(20);
        ring.setStroke(color);
        ring.setFill(Color.TRANSPARENT);
        ring.setStrokeWidth(3);
        ring.setCenterX(position.getX());
        ring.setCenterY(position.getY());

        FXGL.getGameScene().addUINode(ring);

        // Пульсация
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), ring);
        pulse.setToX(1.5);
        pulse.setToY(1.5);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(3);

        // Расширение и затухание
        ScaleTransition expand = new ScaleTransition(Duration.seconds(1.5), ring);
        expand.setToX(3);
        expand.setToY(3);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), ring);
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(expand, fade);
        animation.setOnFinished(event -> {
            FXGL.getGameScene().removeUINode(ring);
        });

        pulse.play();
        animation.play();
    }

    /**
     * Создает эффект критического удара
     *
     * @param position позиция эффекта
     */
    public void createCriticalHitEffect(Point2D position) {
        // Создаем несколько вспышек (уменьшено с 3 до 2 для производительности)
        for (int i = 0; i < 2; i++) {
            Rectangle flash = new Rectangle(40, 40);
            flash.setFill(Color.RED);
            flash.setOpacity(0.6);
            flash.setX(position.getX() - 20);
            flash.setY(position.getY() - 20);

            // Добавляем небольшую задержку для каждой вспышки
            PauseTransition pause = new PauseTransition(Duration.millis(i * 100));

            FadeTransition fade = new FadeTransition(Duration.seconds(0.3), flash);
            fade.setToValue(0);

            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), flash);
            scale.setToX(2);
            scale.setToY(2);

            SequentialTransition sequence = new SequentialTransition(pause,
                new ParallelTransition(fade, scale));

            sequence.setOnFinished(event -> {
                FXGL.getGameScene().removeUINode(flash);
            });

            FXGL.getGameScene().addUINode(flash);
            sequence.play();
        }
    }

    /**
     * Создает текстовый эффект с улучшенной анимацией
     *
     * @param text текст для отображения
     * @param position позиция эффекта
     * @param color цвет текста
     * @param font шрифт текста
     * @param upward движение вверх
     */
    public void createFloatingText(String text, Point2D position, Color color,
                                    Font font, boolean upward) {
        Text textNode = new Text(text);
        textNode.setFill(color);
        textNode.setFont(font);

        // Добавляем тень для читаемости
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(2);
        shadow.setOffsetX(1);
        shadow.setOffsetY(1);
        textNode.setEffect(shadow);

        FXGL.getGameScene().addUINode(textNode);
        textNode.setX(position.getX());
        textNode.setY(position.getY());

        // Анимация
        double direction = upward ? -1 : 1;
        TranslateTransition move = new TranslateTransition(Duration.seconds(2.0), textNode);
        move.setByY(50 * direction);

        FadeTransition fade = new FadeTransition(Duration.seconds(2.0), textNode);
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(move, fade);
        animation.setOnFinished(event -> {
            FXGL.getGameScene().removeUINode(textNode);
        });

        animation.play();
    }
}