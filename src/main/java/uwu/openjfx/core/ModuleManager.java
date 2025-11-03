package uwu.openjfx.core;

import com.almasb.fxgl.dsl.FXGL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Менеджер модулей - управляет загрузкой, инициализацией и завершением работы всех модулей
 */
public class ModuleManager {
    private static ModuleManager instance;

    private final Map<String, GameModule> modules = new ConcurrentHashMap<>();
    private final List<String> initializationOrder = new CopyOnWriteArrayList<>();
    private final Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>();
    private boolean initialized = false;

    private ModuleManager() {}

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    /**
     * Зарегистрировать модуль
     */
    public void registerModule(GameModule module) {
        if (module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        }

        String moduleName = module.getName();
        if (modules.containsKey(moduleName)) {
            GameLogger.warning("ModuleManager", "Module already registered: " + moduleName);
            return;
        }

        modules.put(moduleName, module);
        dependencyGraph.put(moduleName, new HashSet<>(Arrays.asList(module.getDependencies())));

        GameLogger.system("Registered module: " + moduleName + " v" + module.getVersion());
    }

    /**
     * Инициализировать все модули
     */
    public synchronized void initializeAll() throws GameModule.ModuleException {
        if (initialized) {
            GameLogger.warning("ModuleManager", "Modules already initialized");
            return;
        }

        GameLogger.system("Initializing all modules...");

        // Определяем порядок инициализации на основе зависимостей
        List<String> orderedModules = resolveInitializationOrder();

        // Инициализируем модули в правильном порядке
        for (String moduleName : orderedModules) {
            GameModule module = modules.get(moduleName);
            if (module != null) {
                initializeModule(module);
            }
        }

        initialized = true;
        GameLogger.system("All modules initialized successfully. Count: " + orderedModules.size());
    }

    /**
     * Инициализировать конкретный модуль
     */
    private void initializeModule(GameModule module) throws GameModule.ModuleException {
        String moduleName = module.getName();

        try {
            module.setState(GameModule.ModuleState.INITIALIZING);
            GameLogger.info("ModuleManager", "Initializing module: " + moduleName);

            module.initialize();

            module.setState(GameModule.ModuleState.ACTIVE);
            module.onInitialized();
            initializationOrder.add(moduleName);

            GameLogger.info("ModuleManager", "Module initialized: " + moduleName);

        } catch (Exception e) {
            module.setState(GameModule.ModuleState.ERROR);
            module.onError(e);
            throw new GameModule.ModuleException("Failed to initialize module: " + moduleName, e);
        }
    }

    /**
     * Разрешить порядок инициализации на основе зависимостей
     */
    private List<String> resolveInitializationOrder() {
        List<String> allModules = new ArrayList<>(modules.keySet());
        List<String> orderedModules = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        // Топологическая сортировка
        boolean progress = true;
        while (progress && processed.size() < allModules.size()) {
            progress = false;

            for (String moduleName : allModules) {
                if (processed.contains(moduleName)) {
                    continue;
                }

                // Проверяем, что все зависимости обработаны
                Set<String> dependencies = dependencyGraph.getOrDefault(moduleName, new HashSet<>());
                boolean allDepsProcessed = true;

                for (String dep : dependencies) {
                    if (!processed.contains(dep)) {
                        allDepsProcessed = false;
                        break;
                    }
                }

                // Если все зависимости обработаны, добавляем модуль
                if (allDepsProcessed) {
                    orderedModules.add(moduleName);
                    processed.add(moduleName);
                    progress = true;
                }
            }
        }

        // Если остались необработанные модули, это циклическая зависимость
        if (processed.size() < allModules.size()) {
            Set<String> circular = allModules.stream()
                .filter(m -> !processed.contains(m))
                .collect(Collectors.toSet());

            GameLogger.error("ModuleManager", "Circular dependency detected in modules: " + circular);
            throw new IllegalStateException("Circular module dependencies detected: " + circular);
        }

        // Сортируем по приоритету (модули с более низким приоритетом инициализируются раньше)
        return orderedModules.stream()
            .sorted((m1, m2) -> {
                int p1 = modules.get(m1).getPriority();
                int p2 = modules.get(m2).getPriority();
                return Integer.compare(p1, p2);
            })
            .collect(Collectors.toList());
    }

    /**
     * Завершить работу всех модулей
     */
    public synchronized void shutdownAll() {
        if (!initialized) {
            return;
        }

        GameLogger.system("Shutting down all modules...");

        // Завершаем модули в обратном порядке
        List<String> reverseOrder = new ArrayList<>(initializationOrder);
        Collections.reverse(reverseOrder);

        for (String moduleName : reverseOrder) {
            GameModule module = modules.get(moduleName);
            if (module != null) {
                try {
                    module.setState(GameModule.ModuleState.SHUTTING_DOWN);
                    module.shutdown();
                    module.setState(GameModule.ModuleState.UNLOADED);
                    module.onShutdown();
                } catch (Exception e) {
                    GameLogger.error("ModuleManager", "Error shutting down module " + moduleName, e);
                }
            }
        }

        modules.clear();
        dependencyGraph.clear();
        initializationOrder.clear();
        initialized = false;

        GameLogger.system("All modules shut down");
    }

    /**
     * Получить модуль по имени
     */
    public GameModule getModule(String name) {
        return modules.get(name);
    }

    /**
     * Проверить, инициализированы ли все модули
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Получить количество зарегистрированных модулей
     */
    public int getModuleCount() {
        return modules.size();
    }

    /**
     * Получить список всех модулей
     */
    public Collection<GameModule> getAllModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    /**
     * Получить список активных модулей
     */
    public List<GameModule> getActiveModules() {
        return modules.values().stream()
            .filter(m -> m.isInitialized())
            .collect(Collectors.toList());
    }

    /**
     * Проверить наличие модуля
     */
    public boolean hasModule(String name) {
        return modules.containsKey(name);
    }

    /**
     * Обновить все модули
     */
    public void updateAll(double tpf) {
        if (!initialized) {
            return;
        }

        for (String moduleName : initializationOrder) {
            GameModule module = modules.get(moduleName);
            if (module != null && module instanceof Updateable) {
                try {
                    ((Updateable) module).update(tpf);
                } catch (Exception e) {
                    GameLogger.error("ModuleManager", "Error updating module " + moduleName, e);
                }
            }
        }
    }

    /**
     * Интерфейс для модулей, которые нужно обновлять каждый кадр
     */
    interface Updateable {
        void update(double tpf);
    }
}
