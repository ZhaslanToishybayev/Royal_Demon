package uwu.openjfx.leaderboard;

import com.almasb.fxgl.dsl.FXGL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import uwu.openjfx.utils.GameLogger;

import java.util.List;
import java.util.Map;
import javafx.scene.input.KeyCode;

/**
 * UI компонент для отображения таблицы лидеров в виде диалогового окна
 */
public class LeaderboardUI {
    private VBox dialogContainer;
    private TableView<LeaderboardEntry> tableView;
    private Label titleLabel;
    private Label statisticsLabel;
    private Button refreshButton;
    private Button closeButton;
    private Stage dialogStage;
    private boolean isVisible = false; // Флаг для отслеживания состояния окна

    public LeaderboardUI() {
        // Не инициализируем UI здесь - будем делать это при show()
    }

    /**
     * Инициализация UI компонентов диалога
     */
    private void initializeUI() {
        // Главный контейнер диалога с красивым фоном
        dialogContainer = new VBox(20);
        dialogContainer.setAlignment(Pos.CENTER);
        dialogContainer.setPadding(new Insets(40));
        dialogContainer.setStyle(
            "-fx-background-color: #667eea;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 3px;" +
            "-fx-border-color: #FFD700;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 12, 0, 0, 0);"
        );

        // Красивый заголовок с тенью
        titleLabel = new Label("🏆 ТАБЛИЦА ЛИДЕРОВ");
        titleLabel.setFont(Font.font("Arial Black", 42));
        titleLabel.setTextFill(Color.web("#FFD700"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle(
            "-fx-effect: dropshadow(gaussian, #000000, 3, 0.5, 2, 2);" +
            "-fx-padding: 10 0 10 0;"
        );

        // Создаём таблицу с улучшенным дизайном
        createLeaderboardTable();

        // Стильная кнопка обновления
        refreshButton = createStyledButton("🔄 Обновить", "#3498db", "#2980b9");
        refreshButton.setOnAction(e -> {
            System.out.println("🔄 Обновляем таблицу лидеров...");
            refreshLeaderboard();
        });
        // Горячая клавиша F5 для обновления
        refreshButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F5) {
                System.out.println("🔄 Обновляем таблицу лидеров (F5)...");
                refreshLeaderboard();
            }
        });

        // Стильная кнопка закрытия
        closeButton = createStyledButton("❌ Закрыть", "#e74c3c", "#c0392b");
        closeButton.setOnAction(e -> {
            System.out.println("❌ Нажата кнопка Закрыть");
            closeDialog();
        });
        // Горячая клавиша ESC для закрытия
        closeButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.out.println("❌ Закрываем по ESC");
                closeDialog();
            }
        });

        // Панель с кнопками
        HBox buttonPanel = new HBox(20, refreshButton, closeButton);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(20, 0, 0, 0));

        // Красивая статистика
        statisticsLabel = new Label();
        statisticsLabel.setFont(Font.font("Arial", 16));
        statisticsLabel.setTextFill(Color.web("#FFFFFF"));
        statisticsLabel.setAlignment(Pos.CENTER);
        statisticsLabel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 20;" +
            "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );

        // Добавляем всё в главный контейнер
        dialogContainer.getChildren().addAll(
            titleLabel,
            tableView,
            statisticsLabel,
            buttonPanel
        );

        // Загружаем данные
        loadLeaderboardData();
    }

    /**
     * Создание стилизованной кнопки с анимациями
     */
    private Button createStyledButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle(
            "-fx-background-color: " + baseColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12 30;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);" +
            "-fx-cursor: hand;"
        );

        // Анимация при наведении
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 30;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;" +
                "-fx-cursor: hand;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 30;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;" +
                "-fx-cursor: hand;"
            );
        });

        return button;
    }

    /**
     * Создание таблицы лидеров с красивым дизайном
     */
    private void createLeaderboardTable() {
        tableView = new TableView<>();

        // Красивая настройка таблицы
        tableView.setPrefSize(820, 420);
        tableView.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 0);" +
            "-fx-selection-bar: #667eea;" +
            "-fx-selection-bar-non-focused: #a8b2f5;"
        );

        // Красивые заголовки колонок
        String headerStyle = "-fx-background-color: #667eea;" +
                             "-fx-text-fill: white;" +
                             "-fx-font-weight: bold;" +
                             "-fx-font-size: 14px;" +
                             "-fx-alignment: center;";

        // Колонка позиции с медалями
        TableColumn<LeaderboardEntry, Integer> positionColumn = new TableColumn<>("#");
        positionColumn.setPrefWidth(60);
        positionColumn.setStyle(headerStyle);
        positionColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, Integer>, TableCell<LeaderboardEntry, Integer>>() {
            @Override
            public TableCell<LeaderboardEntry, Integer> call(TableColumn<LeaderboardEntry, Integer> param) {
                return new TableCell<LeaderboardEntry, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setText(null);
                            setStyle(null);
                        } else {
                            int pos = getIndex() + 1;
                            String medal = "";
                            String color = "#FFFFFF";
                            String bgColor = "transparent";

                            if (pos == 1) {
                                medal = "🥇 ";
                                color = "#FFD700";
                                bgColor = "rgba(255, 215, 0, 0.2)";
                            } else if (pos == 2) {
                                medal = "🥈 ";
                                color = "#C0C0C0";
                                bgColor = "rgba(192, 192, 192, 0.2)";
                            } else if (pos == 3) {
                                medal = "🥉 ";
                                color = "#CD7F32";
                                bgColor = "rgba(205, 127, 50, 0.2)";
                            }

                            setText(medal + pos);
                            setTextFill(Color.web(color));
                            setStyle("-fx-background-color: " + bgColor + "; -fx-font-weight: bold; -fx-alignment: center;");
                        }
                    }
                };
            }
        });

        // Колонка имени игрока
        TableColumn<LeaderboardEntry, String> nameColumn = new TableColumn<>("🎮 Игрок");
        nameColumn.setPrefWidth(200);
        nameColumn.setStyle(headerStyle);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        nameColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, String>, TableCell<LeaderboardEntry, String>>() {
            @Override
            public TableCell<LeaderboardEntry, String> call(TableColumn<LeaderboardEntry, String> param) {
                return new TableCell<LeaderboardEntry, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 0 0 0 10;");
                        }
                    }
                };
            }
        });

        // Колонка монет
        TableColumn<LeaderboardEntry, Integer> goldColumn = new TableColumn<>("💰 Монеты");
        goldColumn.setPrefWidth(150);
        goldColumn.setStyle(headerStyle);
        goldColumn.setCellValueFactory(new PropertyValueFactory<>("maxGold"));
        goldColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, Integer>, TableCell<LeaderboardEntry, Integer>>() {
            @Override
            public TableCell<LeaderboardEntry, Integer> call(TableColumn<LeaderboardEntry, Integer> param) {
                return new TableCell<LeaderboardEntry, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText("🪙 " + String.format("%,d", item));
                            setTextFill(Color.web("#27ae60"));
                            setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center;");
                        }
                    }
                };
            }
        });

        // Колонка уровня
        TableColumn<LeaderboardEntry, Integer> levelColumn = new TableColumn<>("⬆️ Уровень");
        levelColumn.setPrefWidth(100);
        levelColumn.setStyle(headerStyle);
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("playerLevel"));
        levelColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, Integer>, TableCell<LeaderboardEntry, Integer>>() {
            @Override
            public TableCell<LeaderboardEntry, Integer> call(TableColumn<LeaderboardEntry, Integer> param) {
                return new TableCell<LeaderboardEntry, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText("⚡ " + item);
                            setTextFill(Color.web("#8e44ad"));
                            setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center;");
                        }
                    }
                };
            }
        });

        // Колонка времени
        TableColumn<LeaderboardEntry, Long> timeColumn = new TableColumn<>("⏱️ Время");
        timeColumn.setPrefWidth(150);
        timeColumn.setStyle(headerStyle);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("playTimeSeconds"));
        timeColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, Long>, TableCell<LeaderboardEntry, Long>>() {
            @Override
            public TableCell<LeaderboardEntry, Long> call(TableColumn<LeaderboardEntry, Long> param) {
                return new TableCell<LeaderboardEntry, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            long hours = item / 3600;
                            long minutes = (item % 3600) / 60;
                            long seconds = item % 60;
                            setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                            setTextFill(Color.web("#34495e"));
                            setStyle("-fx-font-family: monospace; -fx-font-size: 13px; -fx-alignment: center;");
                        }
                    }
                };
            }
        });

        // Колонка даты
        TableColumn<LeaderboardEntry, String> dateColumn = new TableColumn<>("📅 Дата");
        dateColumn.setPrefWidth(150);
        dateColumn.setStyle(headerStyle);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("achievedAt"));
        dateColumn.setCellFactory(new Callback<TableColumn<LeaderboardEntry, String>, TableCell<LeaderboardEntry, String>>() {
            @Override
            public TableCell<LeaderboardEntry, String> call(TableColumn<LeaderboardEntry, String> param) {
                return new TableCell<LeaderboardEntry, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            setTextFill(Color.web("#7f8c8d"));
                            setStyle("-fx-font-size: 12px; -fx-alignment: center;");
                        }
                    }
                };
            }
        });

        // Добавляем колонки в таблицу
        tableView.getColumns().addAll(
            positionColumn,
            nameColumn,
            goldColumn,
            levelColumn,
            timeColumn,
            dateColumn
        );
    }

    /**
     * Загрузка данных в таблицу
     */
    private void loadLeaderboardData() {
        try {
            List<LeaderboardEntry> topEntries = LeaderboardManager.getInstance().getTopEntries(20);
            ObservableList<LeaderboardEntry> data = FXCollections.observableArrayList(topEntries);
            tableView.setItems(data);

            // Обновляем статистику
            updateStatistics();

            GameLogger.info("Загружено " + topEntries.size() + " записей в таблицу лидеров");
        } catch (Exception e) {
            GameLogger.error("Ошибка загрузки таблицы лидеров: " + e.getMessage());
        }
    }

    /**
     * Обновление статистики
     */
    private void updateStatistics() {
        try {
            Map<String, Object> stats = LeaderboardManager.getInstance().getStatistics();
            int totalEntries = (int) stats.get("totalEntries");
            int topGold = (int) stats.get("topGold");
            String topPlayer = (String) stats.get("topPlayer");
            double averageGold = (double) stats.get("averageGold");

            statisticsLabel.setText(String.format(
                "📊 Всего игроков: %d | 🏆 Рекорд: %s (%d монет) | 💰 Средний результат: %.0f",
                totalEntries, topPlayer, topGold, averageGold
            ));
        } catch (Exception e) {
            GameLogger.warn("Не удалось обновить статистику: " + e.getMessage());
        }
    }

    /**
     * Обновление таблицы лидеров
     */
    private void refreshLeaderboard() {
        loadLeaderboardData();
    }

    /**
     * Закрыть диалоговое окно
     */
    private void closeDialog() {
        System.out.println("❌ Закрываем таблицу лидеров");

        // Закрываем диалоговое окно
        try {
            if (dialogStage != null) {
                dialogStage.close();
                isVisible = false;
                System.out.println("✅ Диалог таблицы лидеров закрыт");
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка при закрытии диалога: " + e.getMessage());
            GameLogger.error("Не удалось закрыть таблицу лидеров: " + e.getMessage());
        }
    }

    /**
     * Получить контейнер диалога (если нужен)
     */
    public VBox getDialogContainer() {
        return dialogContainer;
    }

    /**
     * Показать таблицу лидеров в диалоговом окне
     */
    public void show() {
        // Если окно уже отображается, не создаем его заново
        if (isVisible && dialogStage != null && dialogStage.isShowing()) {
            System.out.println("⚠️ Таблица лидеров уже отображается!");
            dialogStage.toFront(); // Переносим на передний план
            return;
        }

        System.out.println("🏆 Открываем таблицу лидеров");

        // Инициализируем UI если еще не сделано
        if (dialogContainer == null) {
            initializeUI();
        }

        try {
            // Создаем диалоговое окно
            dialogStage = new Stage();
            dialogStage.setTitle("🏆 Таблица лидеров Royal Demons");
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Блокирует другие окна
            dialogStage.initStyle(StageStyle.DECORATED);

            // Создаем новую сцену с нашим контейнером
            Scene scene = new Scene(dialogContainer);
            dialogStage.setScene(scene);

            // Обработка горячих клавиш на уровне сцены
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    System.out.println("❌ Закрываем по ESC (из сцены)");
                    closeDialog();
                } else if (e.getCode() == KeyCode.F5) {
                    System.out.println("🔄 Обновляем по F5 (из сцены)");
                    refreshLeaderboard();
                }
            });

            // Устанавливаем размер окна
            dialogStage.setWidth(900);
            dialogStage.setHeight(650);

            // Показываем окно асинхронно (не блокируем выполнение)
            dialogStage.show();

            // Фокусируем кнопку закрытия для немедленного ответа на ESC
            closeButton.requestFocus();

            isVisible = true; // Устанавливаем флаг
            System.out.println("✅ Диалог таблицы лидеров открыт");

        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании диалога: " + e.getMessage());
            e.printStackTrace();
            GameLogger.error("Не удалось отобразить таблицу лидеров: " + e.getMessage());
        }
    }
}
