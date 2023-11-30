package com.infinitehorizons.taskmanager.Controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;


public class HomeController implements Initializable {

    private final String userName;
    @FXML
    private Label usernameHome;
    @FXML
    private Label helloLabel;
    @FXML
    private Label dateLabelCal;
    @FXML
    private Label horaCalendar;
    @FXML
    private Label horaCalendar1;
    @FXML
    private Label horaCalendar2;
    @FXML
    private Label horaCalendar3;
    @FXML
    private Line timeIndicator;
    @FXML
    private Line timeIndicator1;
    @FXML
    private Line timeIndicator2;
    @FXML
    private Line timeIndicator3;
    @FXML
    private Line timeIndicator4;
    @FXML
    private ImageView menuTask;
    @FXML
    private Button taskButton;
    @FXML
    private Label goToTaskPage;
    @FXML
    private Tooltip newTaskTool;
    @FXML
    private VBox boxTaskC;
    @FXML
    private ImageView logoutEvent;

    public HomeController(String userName) {
        this.userName = userName;
    }

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

        usernameHome.setText(userName);

        String greeting = generateGreeting(userName);
        helloLabel.setText(greeting);

        dateLabelCal.setText(createCalendar());

        new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 60000000) {
                    updateHour();
                    lastUpdate = now;
                }
            }
        }.start();

        Timeline timeline = new Timeline();
        timeline.setCycleCount (Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame (Duration.seconds (1), new EventHandler<ActionEvent> () {
            @Override
            public void handle (ActionEvent event) {
                LocalTime now = LocalTime.now ();

                int minute = now.getMinute ();

                timeIndicator.setVisible (minute >= 1 && minute <= 12);
                timeIndicator1.setVisible (minute >= 13 && minute <= 24);
                timeIndicator2.setVisible (minute >= 25 && minute <= 36);
                timeIndicator3.setVisible (minute >= 37 && minute <= 48);
                timeIndicator4.setVisible (minute >= 49);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        ContextMenu contextMenu = new ContextMenu();

        contextMenu.getStyleClass().add("context-menu");


        MenuItem menuItem1 = new MenuItem("Go to task");
        MenuItem menuItem2 = new MenuItem("Create new task");

        menuItem1.getStyleClass().add("taskMenuImage");
        menuItem1.getStyleClass().add("menu-item");
        menuItem2.getStyleClass().add("taskMenuImage");
        menuItem2.getStyleClass().add("menu-item");

        contextMenu.getItems().addAll(menuItem1, menuItem2);

        menuTask.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {

                double menuX = menuTask.localToScreen(-80, -80).getX();
                double menuY = menuTask.localToScreen(0, 0).getY() + menuTask.getBoundsInParent().getHeight();

                contextMenu.show(menuTask, menuX, menuY);
            }
        });

        goToTaskPage.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                redirectToTaskPage(userName);
            }
        });

        boxTaskC.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                redirectToTaskPage(userName);
            }
        });

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

    public static String generateGreeting(String userName) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "¡Good Morning, " + userName + "!";
        } else if (hour < 16) {
            return "¡Good Afternoon, " + userName + "!";
        } else {
            return "¡Good Evening, " + userName + "!";
        }
    }

    private String createCalendar() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);

        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        String formattedDate = daysOfWeek[dayOfWeek - 1] + " " + dayOfMonth + " of ";

        switch (month) {
            case Calendar.JANUARY:
                formattedDate += "january";
                break;
            case Calendar.FEBRUARY:
                formattedDate += "february";
                break;
            case Calendar.MARCH:
                formattedDate += "march";
                break;
            case Calendar.APRIL:
                formattedDate += "april";
                break;
            case Calendar.MAY:
                formattedDate += "may";
                break;
            case Calendar.JUNE:
                formattedDate += "june";
                break;
            case Calendar.JULY:
                formattedDate += "july";
                break;
            case Calendar.AUGUST:
                formattedDate += "august";
                break;
            case Calendar.SEPTEMBER:
                formattedDate += "september";
                break;
            case Calendar.OCTOBER:
                formattedDate += "october";
                break;
            case Calendar.NOVEMBER:
                formattedDate += "november";
                break;
            case Calendar.DECEMBER:
                formattedDate += "december";
                break;
        }
        return formattedDate;
    }

    public static int getCurrentHour() {
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        String formattedTime = currentTime.format(formatter);

        return Integer.parseInt(formattedTime);
    }

    private void updateHour() {
        int currentHour = getCurrentHour();

        Platform.runLater(() -> {
            updateLabelHour(horaCalendar, currentHour);
            updateLabelHour(horaCalendar1, currentHour + 1);
            updateLabelHour(horaCalendar2, currentHour + 2);
            updateLabelHour(horaCalendar3, currentHour + 3);
        });
    }

    private String getTextHour(int hora) {
        return hora % 12 + " " + ((hora / 12) == 0 ? "AM" : "PM");
    }

    private void updateLabelHour(Label labelHour, int hour) {
        String formattedHour = getTextHour(hour);
        labelHour.setText(formattedHour);
    }

    public void redirectToTaskPage(ActionEvent event) throws IOException {
        redirectToTaskPage(userName);
    }

    public void redirectToTaskPage(String userName) {
        Platform.runLater(() -> {
            try {
                TasksController controller = new TasksController(userName);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/infinitehorizons/taskmanager/tasks.fxml"));
                loader.setController(controller);
                Parent root = loader.load();
                Stage stage = (Stage) taskButton.getScene().getWindow();
                Scene scene = new Scene(root, 1520, 790);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
                stage.setTitle("Task Manager - Tasks");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void logout() throws IOException {
        System.exit(0);
    }
}
