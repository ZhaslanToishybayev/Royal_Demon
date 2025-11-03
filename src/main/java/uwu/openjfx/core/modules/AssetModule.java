package uwu.openjfx.core.modules;

import uwu.openjfx.core.*;
import java.util.List;

/**
 * Модуль управления ресурсами
 */
public class AssetModule implements GameModule {
    private ResourceManager resourceManager;
    private ModuleState state = ModuleState.UNLOADED;
    private List<String> inventoryWeapons;
    private List<String> normalMinions;
    private List<String> forestMinions;
    private List<String> miniBosses;
    private List<String> roomTypes;

    @Override
    public String getName() {
        return "Assets";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Resource and asset management system";
    }

    @Override
    public void initialize() throws ModuleException {
        state = ModuleState.INITIALIZING;

        try {
            GameLogger.info("AssetModule", "Initializing Asset Module...");

            resourceManager = ResourceManager.getInstance();

            // Загружаем списки ресурсов
            loadAssetLists();

            GameLogger.info("AssetModule", "Asset Module initialized");
            GameLogger.info("AssetModule", "Loaded " + (inventoryWeapons != null ? inventoryWeapons.size() : 0) + " weapons");
            GameLogger.info("AssetModule", "Loaded " + (normalMinions != null ? normalMinions.size() : 0) + " normal minions");
            GameLogger.info("AssetModule", "Loaded " + (forestMinions != null ? forestMinions.size() : 0) + " forest minions");

            state = ModuleState.ACTIVE;

        } catch (Exception e) {
            state = ModuleState.ERROR;
            throw new ModuleException("Failed to initialize Asset Module", e);
        }
    }

    /**
     * Загрузить списки ресурсов из classpath
     */
    private void loadAssetLists() {
        // Загружаем оружие из инвентаря
        inventoryWeapons = resourceManager.listResourcesByExtension(
            "assets/textures/ui/inventory", "png");

        // Загружаем обычных миньонов
        normalMinions = resourceManager.listResourcesByExtension(
            "assets/textures/creatures/minions/normal", "png");

        // Загружаем лесных миньонов
        forestMinions = resourceManager.listResourcesByExtension(
            "assets/textures/creatures/minions/forest", "png");

        // Загружаем мини-боссов
        miniBosses = resourceManager.listResourcesByExtension(
            "assets/textures/creatures/miniBoss", "png");

        // Загружаем типы комнат
        roomTypes = resourceManager.listResourcesByExtension(
            "assets/levels/tmx", "tmx");

        // Фильтруем специальные комнаты
        if (roomTypes != null) {
            roomTypes.removeIf(room -> room.equals("initialRoom.tmx") ||
                room.equals("bossRoom.tmx") ||
                room.equals("challengeRoom.tmx") ||
                room.equals("finalWinRoom.tmx") ||
                room.equals("weaponsShowcaseRoom.tmx"));

            // Убираем расширения
            roomTypes.replaceAll(room -> room.replace(".tmx", ""));
        }
    }

    @Override
    public void shutdown() throws ModuleException {
        state = ModuleState.SHUTTING_DOWN;

        // Очищаем списки
        if (inventoryWeapons != null) inventoryWeapons.clear();
        if (normalMinions != null) normalMinions.clear();
        if (forestMinions != null) forestMinions.clear();
        if (miniBosses != null) miniBosses.clear();
        if (roomTypes != null) roomTypes.clear();

        state = ModuleState.UNLOADED;
    }

    @Override
    public boolean isInitialized() {
        return state == ModuleState.ACTIVE;
    }

    @Override
    public int getPriority() {
        return 10; // Загружается после Core Module
    }

    @Override
    public ModuleState getState() {
        return state;
    }

    @Override
    public void setState(ModuleState newState) {
        state = newState;
    }

    // Getters для списков ресурсов
    public List<String> getInventoryWeapons() {
        return inventoryWeapons;
    }

    public List<String> getNormalMinions() {
        return normalMinions;
    }

    public List<String> getForestMinions() {
        return forestMinions;
    }

    public List<String> getMiniBosses() {
        return miniBosses;
    }

    public List<String> getRoomTypes() {
        return roomTypes;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
