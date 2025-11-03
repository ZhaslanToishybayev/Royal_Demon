package uwu.openjfx.config;

/**
 * Основная конфигурация игры
 */
public class GameConfig {
    // Настройки графики
    private int screenWidth = 960;
    private int screenHeight = 640;
    private boolean fullscreen = false;
    private int maxFPS = 60;
    private boolean vsync = true;

    // Настройки звука
    private float masterVolume = 1.0f;
    private float musicVolume = 0.7f;
    private float sfxVolume = 0.8f;

    // Настройки геймплея
    private String difficulty = "normal";
    private String language = "ru";
    private boolean showDamageNumbers = true;
    private boolean showTooltips = true;
    private boolean autoSave = true;

    // Настройки управления
    private boolean invertY = false;
    private float mouseSensitivity = 1.0f;
    private boolean enableDebugMode = false;

    // Конструктор по умолчанию
    public GameConfig() {}

    // Getters и Setters
    public int getScreenWidth() { return screenWidth; }
    public void setScreenWidth(int screenWidth) { this.screenWidth = screenWidth; }

    public int getScreenHeight() { return screenHeight; }
    public void setScreenHeight(int screenHeight) { this.screenHeight = screenHeight; }

    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

    public int getMaxFPS() { return maxFPS; }
    public void setMaxFPS(int maxFPS) { this.maxFPS = maxFPS; }

    public boolean isVsync() { return vsync; }
    public void setVsync(boolean vsync) { this.vsync = vsync; }

    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isShowDamageNumbers() { return showDamageNumbers; }
    public void setShowDamageNumbers(boolean showDamageNumbers) { this.showDamageNumbers = showDamageNumbers; }

    public boolean isShowTooltips() { return showTooltips; }
    public void setShowTooltips(boolean showTooltips) { this.showTooltips = showTooltips; }

    public boolean isAutoSave() { return autoSave; }
    public void setAutoSave(boolean autoSave) { this.autoSave = autoSave; }

    public boolean isInvertY() { return invertY; }
    public void setInvertY(boolean invertY) { this.invertY = invertY; }

    public float getMouseSensitivity() { return mouseSensitivity; }
    public void setMouseSensitivity(float mouseSensitivity) { this.mouseSensitivity = mouseSensitivity; }

    public boolean isEnableDebugMode() { return enableDebugMode; }
    public void setEnableDebugMode(boolean enableDebugMode) { this.enableDebugMode = enableDebugMode; }

    @Override
    public String toString() {
        return "GameConfig{" +
            "screenWidth=" + screenWidth +
            ", screenHeight=" + screenHeight +
            ", fullscreen=" + fullscreen +
            ", maxFPS=" + maxFPS +
            ", vsync=" + vsync +
            ", masterVolume=" + masterVolume +
            ", musicVolume=" + musicVolume +
            ", sfxVolume=" + sfxVolume +
            ", difficulty='" + difficulty + '\'' +
            ", language='" + language + '\'' +
            ", showDamageNumbers=" + showDamageNumbers +
            ", showTooltips=" + showTooltips +
            ", autoSave=" + autoSave +
            ", invertY=" + invertY +
            ", mouseSensitivity=" + mouseSensitivity +
            ", enableDebugMode=" + enableDebugMode +
            '}';
    }
}
