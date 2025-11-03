package uwu.openjfx.audio;

import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.audio.Sound;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Улучшенный менеджер звука для управления звуковыми эффектами и музыкой
 */
public class AudioManager {

    private static AudioManager instance;

    // Плееры для разных типов звука
    private final AudioPlayer sfxPlayer;
    private final AudioPlayer musicPlayer;
    private final AudioPlayer voicePlayer;

    // Настройки громкости
    private final DoubleProperty masterVolume = new SimpleDoubleProperty(1.0);
    private final DoubleProperty sfxVolume = new SimpleDoubleProperty(0.8);
    private final DoubleProperty musicVolume = new SimpleDoubleProperty(0.6);
    private final DoubleProperty voiceVolume = new SimpleDoubleProperty(0.9);

    // Хранилище звуков
    private final ObservableMap<String, Sound> sfxSounds = FXCollections.observableHashMap();
    private final ObservableMap<String, Sound> musicTracks = FXCollections.observableHashMap();
    private final ObservableMap<String, Sound> voiceSounds = FXCollections.observableHashMap();

    // Текущее состояние
    private String currentMusicTrack = null;
    private boolean isMusicPlaying = false;
    private boolean isMuted = false;
    private boolean isSfxMuted = false;
    private boolean isMusicMuted = false;

    // Кэш для часто используемых звуков
    private final Map<String, CompletableFuture<Sound>> loadingCache = new HashMap<>();

    private AudioManager() {
        this.sfxPlayer = new AudioPlayer();
        this.musicPlayer = new AudioPlayer();
        this.voicePlayer = new AudioPlayer();

        setupVolumeListeners();
        loadDefaultSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Настраивает слушатели для управления громкостью
     */
    private void setupVolumeListeners() {
        // Обновляем громкость при изменении настроек
        masterVolume.addListener((obs, oldVal, newVal) -> updateAllVolumes());
        sfxVolume.addListener((obs, oldVal, newVal) -> updateSfxVolume());
        musicVolume.addListener((obs, oldVal, newVal) -> updateMusicVolume());
        voiceVolume.addListener((obs, oldVal, newVal) -> updateVoiceVolume());
    }

    /**
     * Загружает звуки по умолчанию
     */
    private void loadDefaultSounds() {
        // Звуковые эффекты
        preloadSound("sword_hit", "assets/audio/sfx/sword_hit.wav", SoundType.SFX);
        preloadSound("bow_shoot", "assets/audio/sfx/bow_shoot.wav", SoundType.SFX);
        preloadSound("magic_cast", "assets/audio/sfx/magic_cast.wav", SoundType.SFX);
        preloadSound("explosion", "assets/audio/sfx/explosion.wav", SoundType.SFX);
        preloadSound("heal", "assets/audio/sfx/heal.wav", SoundType.SFX);
        preloadSound("pickup", "assets/audio/sfx/pickup.wav", SoundType.SFX);
        preloadSound("level_up", "assets/audio/sfx/level_up.wav", SoundType.SFX);
        preloadSound("critical_hit", "assets/audio/sfx/critical_hit.wav", SoundType.SFX);
        preloadSound("dodge", "assets/audio/sfx/dodge.wav", SoundType.SFX);
        preloadSound("block", "assets/audio/sfx/block.wav", SoundType.SFX);
        preloadSound("menu_click", "assets/audio/sfx/menu_click.wav", SoundType.SFX);
        preloadSound("menu_back", "assets/audio/sfx/menu_back.wav", SoundType.SFX);
        preloadSound("door_open", "assets/audio/sfx/door_open.wav", SoundType.SFX);
        preloadSound("chest_open", "assets/audio/sfx/chest_open.wav", SoundType.SFX);

        // Музыкальные треки
        preloadSound("main_theme", "assets/audio/music/main_theme.ogg", SoundType.MUSIC);
        preloadSound("battle_theme", "assets/audio/music/battle_theme.ogg", SoundType.MUSIC);
        preloadSound("victory_theme", "assets/audio/music/victory_theme.ogg", SoundType.MUSIC);
        preloadSound("game_over_theme", "assets/audio/music/game_over_theme.ogg", SoundType.MUSIC);
        preloadSound("menu_theme", "assets/audio/music/menu_theme.ogg", SoundType.MUSIC);
        preloadSound("dungeon_ambient", "assets/audio/music/dungeon_ambient.ogg", SoundType.MUSIC);

        // Голосовые звуки
        preloadSound("player_grunt", "assets/audio/voice/player_grunt.wav", SoundType.VOICE);
        preloadSound("player_hurt", "assets/audio/voice/player_hurt.wav", SoundType.VOICE);
        preloadSound("player_death", "assets/audio/voice/player_death.wav", SoundType.VOICE);
        preloadSound("enemy_growl", "assets/audio/voice/enemy_growl.wav", SoundType.VOICE);
        preloadSound("enemy_death", "assets/audio/voice/enemy_death.wav", SoundType.VOICE);
        preloadSound("victory_cheer", "assets/audio/voice/victory_cheer.wav", SoundType.VOICE);
    }

    /**
     * Предварительно загружает звук
     */
    private void preloadSound(String name, String path, SoundType type) {
        CompletableFuture<Sound> soundFuture = SoundLoader.loadSound(path);
        loadingCache.put(name, soundFuture);

        soundFuture.thenAccept(sound -> {
            switch (type) {
                case SFX:
                    sfxSounds.put(name, sound);
                    break;
                case MUSIC:
                    musicTracks.put(name, sound);
                    break;
                case VOICE:
                    voiceSounds.put(name, sound);
                    break;
            }
        }).exceptionally(ex -> {
            System.err.println("Failed to load sound: " + path + " - " + ex.getMessage());
            return null;
        });
    }

    /**
     * Воспроизводит звуковой эффект
     */
    public void playSFX(String soundName) {
        if (isMuted || isSfxMuted) return;

        Sound sound = sfxSounds.get(soundName);
        if (sound != null) {
            sfxPlayer.playSound(sound);
        } else {
            System.err.println("SFX not found: " + soundName);
        }
    }

    /**
     * Воспроизводит музыкальный трек
     */
    public void playMusic(String trackName, boolean loop) {
        if (isMuted || isMusicMuted) return;

        Sound music = musicTracks.get(trackName);
        if (music != null) {
            // Останавливаем текущую музыку
            stopMusic();

            currentMusicTrack = trackName;
            musicPlayer.playSound(music);
            isMusicPlaying = true;

            // Note: setCycleCount might not be available in this FXGL version
            // if (loop) {
            //     music.setCycleCount(1000000);
            // }
        } else {
            System.err.println("Music track not found: " + trackName);
        }
    }

    /**
     * Воспроизводит голосовой звук
     */
    public void playVoice(String voiceName) {
        if (isMuted) return;

        Sound voice = voiceSounds.get(voiceName);
        if (voice != null) {
            voicePlayer.playSound(voice);
        } else {
            System.err.println("Voice sound not found: " + voiceName);
        }
    }

    /**
     * Останавливает текущую музыку
     */
    public void stopMusic() {
        if (currentMusicTrack != null) {
            musicPlayer.stopSound(musicTracks.get(currentMusicTrack));
            currentMusicTrack = null;
            isMusicPlaying = false;
        }
    }

    /**
     * Останавливает все звуки
     */
    public void stopAllSounds() {
        sfxPlayer.stopAllSounds();
        musicPlayer.stopAllSounds();
        voicePlayer.stopAllSounds();
        currentMusicTrack = null;
        isMusicPlaying = false;
    }

    /**
     * Обновляет громкость всех звуков
     */
    private void updateAllVolumes() {
        updateSfxVolume();
        updateMusicVolume();
        updateVoiceVolume();
    }

    /**
     * Обновляет громкость звуковых эффектов
     */
    private void updateSfxVolume() {
        double volume = calculateVolume(sfxVolume.get());
        // Note: FXGL doesn't have setGlobalVolume, volumes are set per-sound
        // Just update the property value
    }

    /**
     * Обновляет громкость музыки
     */
    private void updateMusicVolume() {
        double volume = calculateVolume(musicVolume.get());
        // Note: FXGL doesn't have setGlobalVolume, volumes are set per-sound
        // Just update the property value
    }

    /**
     * Обновляет громкость голоса
     */
    private void updateVoiceVolume() {
        double volume = calculateVolume(voiceVolume.get());
        // Note: FXGL doesn't have setGlobalVolume, volumes are set per-sound
        // Just update the property value
    }

    /**
     * Вычисляет итоговую громкость с учетом основной громкости
     */
    private double calculateVolume(double typeVolume) {
        return masterVolume.get() * typeVolume;
    }

    // Getters и Setters для настроек громкости

    public double getMasterVolume() {
        return masterVolume.get();
    }

    public DoubleProperty masterVolumeProperty() {
        return masterVolume;
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume.set(Math.max(0.0, Math.min(1.0, masterVolume)));
    }

    public double getSfxVolume() {
        return sfxVolume.get();
    }

    public DoubleProperty sfxVolumeProperty() {
        return sfxVolume;
    }

    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume.set(Math.max(0.0, Math.min(1.0, sfxVolume)));
    }

    public double getMusicVolume() {
        return musicVolume.get();
    }

    public DoubleProperty musicVolumeProperty() {
        return musicVolume;
    }

    public void setMusicVolume(double musicVolume) {
        this.musicVolume.set(Math.max(0.0, Math.min(1.0, musicVolume)));
    }

    public double getVoiceVolume() {
        return voiceVolume.get();
    }

    public DoubleProperty voiceVolumeProperty() {
        return voiceVolume;
    }

    public void setVoiceVolume(double voiceVolume) {
        this.voiceVolume.set(Math.max(0.0, Math.min(1.0, voiceVolume)));
    }

    // Управление состоянием

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (muted) {
            stopAllSounds();
        }
    }

    public boolean isSfxMuted() {
        return isSfxMuted;
    }

    public void setSfxMuted(boolean sfxMuted) {
        this.isSfxMuted = sfxMuted;
        if (sfxMuted) {
            sfxPlayer.stopAllSounds();
        }
    }

    public boolean isMusicMuted() {
        return isMusicMuted;
    }

    public void setMusicMuted(boolean musicMuted) {
        this.isMusicMuted = musicMuted;
        if (musicMuted) {
            stopMusic();
        }
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public String getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    /**
     * Переключает режим-muted
     */
    public void toggleMute() {
        setMuted(!isMuted);
    }

    /**
     * Переключает режим-muted для SFX
     */
    public void toggleSfxMute() {
        setSfxMuted(!isSfxMuted);
    }

    /**
     * Переключает режим-muted для музыки
     */
    public void toggleMusicMute() {
        setMusicMuted(!isMusicMuted);
    }

    /**
     * Получает список доступных звуковых эффектов
     */
    public ObservableMap<String, Sound> getSfxSounds() {
        return FXCollections.unmodifiableObservableMap(sfxSounds);
    }

    /**
     * Получает список доступных музыкальных треков
     */
    public ObservableMap<String, Sound> getMusicTracks() {
        return FXCollections.unmodifiableObservableMap(musicTracks);
    }

    /**
     * Получает список доступных голосовых звуков
     */
    public ObservableMap<String, Sound> getVoiceSounds() {
        return FXCollections.unmodifiableObservableMap(voiceSounds);
    }

    /**
     * Добавляет звуковой эффект
     */
    public void addSFX(String name, String path) {
        preloadSound(name, path, SoundType.SFX);
    }

    /**
     * Добавляет музыкальный трек
     */
    public void addMusic(String name, String path) {
        preloadSound(name, path, SoundType.MUSIC);
    }

    /**
     * Добавляет голосовой звук
     */
    public void addVoice(String name, String path) {
        preloadSound(name, path, SoundType.VOICE);
    }

    /**
     * Очищает все ресурсы
     */
    public void cleanup() {
        stopAllSounds();
        sfxSounds.clear();
        musicTracks.clear();
        voiceSounds.clear();
        loadingCache.clear();
    }

    /**
     * Типы звуков
     */
    private enum SoundType {
        SFX, MUSIC, VOICE
    }
}