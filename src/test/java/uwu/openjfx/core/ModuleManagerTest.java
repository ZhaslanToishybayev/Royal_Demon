package uwu.openjfx.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ModuleManager
 */
class ModuleManagerTest {

    @BeforeEach
    void setUp() {
        // Очищаем состояние перед каждым тестом
        // В реальной реализации нужно добавить метод reset()
    }

    @Test
    void testModuleRegistration() {
        ModuleManager manager = ModuleManager.getInstance();
        TestModule module = new TestModule("TestModule", "1.0.0");

        manager.registerModule(module);

        assertTrue(manager.hasModule("TestModule"));
        assertNotNull(manager.getModule("TestModule"));
        assertEquals(1, manager.getModuleCount());
    }

    @Test
    void testMultipleModules() {
        ModuleManager manager = ModuleManager.getInstance();
        manager.registerModule(new TestModule("Module1", "1.0.0"));
        manager.registerModule(new TestModule("Module2", "1.0.0"));
        manager.registerModule(new TestModule("Module3", "1.0.0"));

        assertEquals(3, manager.getModuleCount());
        assertTrue(manager.hasModule("Module1"));
        assertTrue(manager.hasModule("Module2"));
        assertTrue(manager.hasModule("Module3"));
    }

    @Test
    void testModuleInitialization() throws GameModule.ModuleException {
        ModuleManager manager = ModuleManager.getInstance();
        TestModule module = new TestModule("InitTest", "1.0.0");

        manager.registerModule(module);
        manager.initializeAll();

        assertTrue(module.isInitialized());
        assertEquals(GameModule.ModuleState.ACTIVE, module.getState());
    }

    @Test
    void testModuleShutdown() throws GameModule.ModuleException {
        ModuleManager manager = ModuleManager.getInstance();
        TestModule module = new TestModule("ShutdownTest", "1.0.0");

        manager.registerModule(module);
        manager.initializeAll();
        assertTrue(module.isInitialized());

        manager.shutdownAll();
        assertFalse(module.isInitialized());
    }

    @Test
    void testPriorityOrdering() throws GameModule.ModuleException {
        ModuleManager manager = ModuleManager.getInstance();

        TestModule lowPriority = new TestModule("LowPriority", "1.0.0");
        lowPriority.setPriority(100);

        TestModule highPriority = new TestModule("HighPriority", "1.0.0");
        highPriority.setPriority(0);

        TestModule mediumPriority = new TestModule("MediumPriority", "1.0.0");
        mediumPriority.setPriority(50);

        manager.registerModule(lowPriority);
        manager.registerModule(highPriority);
        manager.registerModule(mediumPriority);

        manager.initializeAll();

        // Высокий приоритет должен быть инициализирован первым
        assertTrue(highPriority.getInitializationOrderIndex()
                < mediumPriority.getInitializationOrderIndex());
        assertTrue(mediumPriority.getInitializationOrderIndex()
                < lowPriority.getInitializationOrderIndex());
    }

    /**
     * Тестовый модуль для проверки функциональности
     */
    private static class TestModule implements GameModule {
        private String name;
        private String version;
        private boolean isInitialized = false;
        private int priority = 100;
        private int initializationOrderIndex = -1;

        private ModuleState state = ModuleState.UNLOADED;

        public TestModule(String name, String version) {
            this.name = name;
            this.version = version;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public String getDescription() {
            return "Test module: " + name;
        }

        @Override
        public void initialize() throws ModuleException {
            setInitialized(true);
            state = ModuleState.ACTIVE;
        }

        // Accessor methods
        public boolean isInitialized() {
            return isInitialized;
        }

        public void setInitialized(boolean initialized) {
            this.isInitialized = initialized;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getInitializationOrderIndex() {
            return initializationOrderIndex;
        }

        public void setInitializationOrderIndex(int index) {
            this.initializationOrderIndex = index;
        }

        @Override
        public void shutdown() throws ModuleException {
            setInitialized(false);
            state = ModuleState.UNLOADED;
        }

        @Override
        public ModuleState getState() {
            return state;
        }
    }
}
