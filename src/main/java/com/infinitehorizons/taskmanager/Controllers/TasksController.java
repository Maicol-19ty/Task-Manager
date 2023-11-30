package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.Core.Task;
import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class TasksController implements Initializable {

    private final String userName;
    @FXML
    private Label usernameHome;
    @FXML
    private TextArea titleTaskArea;
    @FXML
    private TextArea descriptionTaskArea;
    @FXML
    private Button createNewTask;
    @FXML
    private Button saveTask;
    @FXML
    private ListView<VBox> tasksListView;
    @FXML
    private Button homeButton;
    @FXML
    private Label taskLabel;
    @FXML
    private ImageView logoutEvent;

    private static final String PROVISIONAL_TEXT = "Write the title...";
    private static final String PROVISIONAL_DESCRIPTION = "Write the description...";
    private int selectedTaskId = -1;

    private static final Connect connectNow = new Connect();
    private static final Connection connectDB = connectNow.getConnection();

    public TasksController(String userName) {
        this.userName = userName;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        usernameHome.setText(userName);

        titleTaskArea.setText(PROVISIONAL_TEXT);
        descriptionTaskArea.setText(PROVISIONAL_DESCRIPTION);

        descriptionTaskArea.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (descriptionTaskArea.getText().equals(PROVISIONAL_DESCRIPTION)) {
                descriptionTaskArea.setText("");
            }
        });

        titleTaskArea.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (titleTaskArea.getText().equals(PROVISIONAL_TEXT)) {
                titleTaskArea.setText("");
            }
        });

        createNewTask.setOnAction(event -> {
            selectedTaskId = -1;
            titleTaskArea.clear();
            titleTaskArea.setText(PROVISIONAL_TEXT);
            descriptionTaskArea.clear();
            descriptionTaskArea.setText(PROVISIONAL_DESCRIPTION);
        });

        saveTask.setOnAction(event -> {
            handleCreateNewTask();
        });

        showTasksInListView();

        List<Task> tasks = obtenerTareasDesdeLaBaseDeDatos();
        if (!tasks.isEmpty()) {
            int firstTaskId = tasks.get(0).getId();
            handleEditTask(firstTaskId);
            loadTaskData(firstTaskId);
        }

        logoutEvent.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    logout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void redirectToHomePage() {
        Platform.runLater(() -> {
            try {
                HomeController controller = new HomeController(userName);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/infinitehorizons/taskmanager/home.fxml"));
                loader.setController(controller);
                Parent root = loader.load();
                Stage stage = (Stage) homeButton.getScene().getWindow();
                Scene scene = new Scene(root, 1520, 790);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
                stage.setTitle("Task Manager - Home");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void saveTask(int taskId, int userId, String username, String titleTask, String descriptionTask) {
        try {
            if (taskId == 0) {
                try (PreparedStatement statement = connectDB.prepareStatement("INSERT INTO tasks (account_id, username, title_task, description_task, date_creation) VALUES (?, ?, ?, ?, CURDATE())")) {
                    statement.setInt(1, userId);
                    statement.setString(2, username);
                    statement.setString(3, titleTask);
                    statement.setString(4, descriptionTask);
                    statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = connectDB.prepareStatement("UPDATE tasks SET title_task = ?, description_task = ? WHERE id_task = ?")) {
                    statement.setString(1, titleTask);
                    statement.setString(2, descriptionTask);
                    statement.setInt(3, taskId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleCreateNewTask() {
        String title = titleTaskArea.getText().trim();
        String description = descriptionTaskArea.getText().trim();
        int userId = getUserId(userName);

        if (title.isEmpty() || title.equals(PROVISIONAL_TEXT)) {
            taskLabel.setText("Please complete the title.");
            cleanLabel();
            return;
        }

        if (description.isEmpty() || description.equals(PROVISIONAL_DESCRIPTION)) {
            taskLabel.setText("Please complete the description.");
            cleanLabel();
            return;
        }

        if (selectedTaskId == -1) {
            saveTask(0, userId, userName, title, description);
        } else {
            saveTask(selectedTaskId, userId, userName, title, description);
            selectedTaskId = -1;
        }

        if (titleTaskArea.getText().isEmpty()) {
            titleTaskArea.setText(PROVISIONAL_TEXT);
        }

        if (descriptionTaskArea.getText().isEmpty()) {
            descriptionTaskArea.setText(PROVISIONAL_DESCRIPTION);
        }

        showTasksInListView();
    }


    private int getUserId(String username) {
        int userId = -1;

        try (PreparedStatement statement = connectDB.prepareStatement("SELECT account_id FROM users_accounts WHERE username = ?")) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("account_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    public void handleEditTask(int taskId) {
        selectedTaskId = taskId;
        loadTaskData(taskId);
    }

    public void handleTaskClick(MouseEvent event) {
        System.out.println("Task clicked!");
        Object source = event.getSource();

        if (source instanceof Label) {
            Parent parent = ((Label) source).getParent();

            if (parent instanceof VBox tareaBox) {
                int taskId = getTaskIdFromNode(tareaBox);
                handleEditTask(taskId);
                loadTaskData(taskId);
            }
        }
    }

    private void loadTaskData(int taskId) {
        try (PreparedStatement statement = connectDB.prepareStatement("SELECT * FROM tasks WHERE id_task = ?")) {
            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title_task");
                    String description = resultSet.getString("description_task");

                    titleTaskArea.setText(title);
                    descriptionTaskArea.setText(description);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getTaskIdFromNode(Object nodo) {
        if (nodo instanceof VBox tareaBox) {
            Object idObject = tareaBox.getUserData();

            if (idObject instanceof Integer) {
                return (int) idObject;
            }
        }
        return -1;
    }

    public void showTasksInListView() {
        taskLabel.setText("Loading tasks in the list...");

        List<Task> tasks = obtenerTareasDesdeLaBaseDeDatos();

        tasksListView.getItems().clear();

        tasks.forEach(task -> {
            VBox taskBox = createTaskBox(task);
            tasksListView.getItems().add(taskBox);
        });

        cleanLabel();
    }

    private VBox createTaskBox(Task task) {
        VBox taskBox = new VBox();
        int idTask = task.getId();
        taskBox.setUserData(idTask);

        Label titleLabel = new Label(task.getTitulo());
        titleLabel.getStyleClass().add("label-title");

        String description = task.getDescripcion();
        String first10words = getFirstWords(description);
        Label descriptionLabel = new Label(first10words);
        descriptionLabel.getStyleClass().add("label-description");

        taskBox.getChildren().addAll(titleLabel, descriptionLabel);

        taskBox.setOnMouseClicked(event -> {
            handleTaskClick(event);
            int taskId = getTaskIdFromNode(taskBox);
            handleEditTask(taskId);
            loadTaskData(taskId);
        });
        return taskBox;
    }

    private List<Task> obtenerTareasDesdeLaBaseDeDatos() {
        List<Task> taskOfList = new ArrayList<>();

        try (PreparedStatement statement = connectDB.prepareStatement("SELECT id_task, title_task, description_task FROM tasks WHERE username = ?")) {
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task();
                    task.setId(resultSet.getInt("id_task"));
                    task.setTitulo(resultSet.getString("title_task"));
                    task.setDescripcion(resultSet.getString("description_task"));
                    taskOfList.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taskOfList;
    }

    private String getFirstWords(String texto) {
        String[] words = texto.split("\\s+");
        int length = Math.min(10, words.length);
        return String.join(" ", Arrays.copyOfRange(words, 0, length));
    }

    @FXML
    private void handleDeleteTask() {
        int selectedTaskId = getSelectedTaskId();

        if (selectedTaskId != -1) {
            deleteTask(selectedTaskId);

            showTasksInListView();
        } else {
            taskLabel.setText("Please select a task to delete.");
            cleanLabel();
        }
    }

    private void deleteTask(int taskId) {
        try (PreparedStatement statement = connectDB.prepareStatement("DELETE FROM tasks WHERE id_task = ?")) {
            statement.setInt(1, taskId);
            statement.executeUpdate();
            taskLabel.setText("Task deleted successfully.");
            cleanLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedTaskId() {
        VBox selectedTaskBox = tasksListView.getSelectionModel().getSelectedItem();

        if (selectedTaskBox != null) {
            Object idObject = selectedTaskBox.getUserData();

            if (idObject instanceof Integer) {
                return (int) idObject;
            }
        }
        return -1;
    }

    private void cleanLabel() {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(5));
        pauseTransition.setOnFinished(event -> {
            taskLabel.setText("");
        });
        pauseTransition.play();
    }

    private void logout() throws IOException {
        System.exit(0);
    }

}
