package uwu.openjfx.audio;

import com.almasb.fxgl.audio.Sound;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Утилита для загрузки звуков из различных источников
 */
public class SoundLoader {

    /**
     * Загружает звук из файла ресурсов
     */
    public static CompletableFuture<Sound> loadSound(String resourcePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = SoundLoader.class.getResourceAsStream("/" + resourcePath);
                if (inputStream == null) {
                    System.err.println("Sound resource not found: " + resourcePath);
                    return null;
                }
                // Note: Using FXGL to load sound
                // return com.almasb.fxgl.dsl.FXGL.getAudioPlayer().loadSound(resourcePath);
                System.err.println("Sound loading not implemented yet: " + resourcePath);
                return null;
            } catch (Exception e) {
                System.err.println("Error loading sound: " + resourcePath + " - " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Загружает звук с указанной задержкой (для создания эха)
     */
    public static CompletableFuture<Sound> loadSoundWithDelay(String resourcePath, long delayMs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(delayMs);
                return loadSound(resourcePath).get();
            } catch (Exception e) {
                System.err.println("Error loading sound with delay: " + resourcePath + " - " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Создает пустой звук (заглушку)
     */
    public static Sound createEmptySound() {
        // Возвращаем заглушку - можно реализовать генерацию тишины
        return null;
    }
}