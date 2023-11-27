package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.Core.Task;
import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
            mostrarTareasEnListView();
        });

        mostrarTareasEnListView();
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
        String title = titleTaskArea.getText();
        String description = descriptionTaskArea.getText();
        int userId = getUserId(userName);

        if (title.equals(PROVISIONAL_TEXT)) {
            title = "";
            titleTaskArea.clear();
        }

        if (description.equals(PROVISIONAL_DESCRIPTION)) {
            description = "";
            descriptionTaskArea.clear();
        }

        if (title.isEmpty() || description.isEmpty()) {
            System.out.println("Por favor, completa tanto el título como la descripción.");
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
                int taskId = obtenerIdDeLaTareaDesdeElNodo(tareaBox);
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

    private int obtenerIdDeLaTareaDesdeElNodo(Object nodo) {
        if (nodo instanceof VBox tareaBox) {
            Object idObject = tareaBox.getUserData();

            if (idObject instanceof Integer) {
                return (int) idObject;
            }
        }
        return -1;
    }

    public void mostrarTareasEnListView() {
        System.out.println("Mostrando tareas en ListView...");

        List<Task> tasks = obtenerTareasDesdeLaBaseDeDatos();

        tasksListView.getItems().clear();

        tasks.forEach(task -> {
            VBox tareaBox = crearTareaBox(task);
            tasksListView.getItems().add(tareaBox);
        });
    }

    private VBox crearTareaBox(Task task) {
        VBox tareaBox = new VBox();
        int idTask = task.getId();
        tareaBox.setUserData(idTask);

        Label tituloLabel = new Label(task.getTitulo());
        tituloLabel.getStyleClass().add("label-title");

        String descripcion = task.getDescripcion();
        String primeras10Palabras = obtenerPrimerasPalabras(descripcion, 10);
        Label descripcionLabel = new Label(primeras10Palabras);
        descripcionLabel.getStyleClass().add("label-description");

        tareaBox.getChildren().addAll(tituloLabel, descripcionLabel);

        tareaBox.setOnMouseClicked(event -> {
            handleTaskClick(event);
            int taskId = obtenerIdDeLaTareaDesdeElNodo(tareaBox);
            handleEditTask(taskId);
            loadTaskData(taskId);
        });
        return tareaBox;
    }

    private List<Task> obtenerTareasDesdeLaBaseDeDatos() {
        List<Task> listaDeTareas = new ArrayList<>();

        try (PreparedStatement statement = connectDB.prepareStatement("SELECT id_task, title_task, description_task FROM tasks WHERE username = ?")) {
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Task tarea = new Task();
                    tarea.setId(resultSet.getInt("id_task"));
                    tarea.setTitulo(resultSet.getString("title_task"));
                    tarea.setDescripcion(resultSet.getString("description_task"));
                    listaDeTareas.add(tarea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaDeTareas;
    }

    private String obtenerPrimerasPalabras(String texto, int n) {
        String[] palabras = texto.split("\\s+");
        int longitud = Math.min(n, palabras.length);
        return String.join(" ", Arrays.copyOfRange(palabras, 0, longitud));
    }

    @FXML
    private void handleDeleteTask() {
        int selectedTaskId = getSelectedTaskId();

        if (selectedTaskId != -1) {
            deleteTask(selectedTaskId);

            mostrarTareasEnListView();
        } else {
            System.out.println("Por favor, selecciona una tarea para eliminar.");
        }
    }

    private void deleteTask(int taskId) {
        try (PreparedStatement statement = connectDB.prepareStatement("DELETE FROM tasks WHERE id_task = ?")) {
            statement.setInt(1, taskId);
            statement.executeUpdate();
            System.out.println("Tarea eliminada con éxito.");
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

}
