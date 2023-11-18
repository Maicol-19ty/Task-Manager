package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ForgotController implements Initializable {

    @FXML
    private PasswordField confpasswordTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label passwordMatchLabel;
    @FXML
    private Button forgotButton;
    @FXML
    private Label forgotMessageLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ImageView forbrandingImageView;
    private List<TextField> textFields;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Pestaña de recuerdo inicializada.");
        File forbrandingFile = new File("src/main/resources/com/infinitehorizons/taskmanager/images/task-logo-2.png");
        if (forbrandingFile.exists()) {
            Image forbrandingImage = new Image(forbrandingFile.toURI().toString());
            forbrandingImageView.setImage(forbrandingImage);
        } else {
            System.out.println("No se pudo encontrar la imagen en la ubicación especificada.");
        }

        BooleanBinding passwordMatch = Bindings.createBooleanBinding(() ->
                        passwordTextField.getText().equals(confpasswordTextField.getText()) &&
                                passwordTextField.getText().length() >= 8,
                passwordTextField.textProperty(), confpasswordTextField.textProperty());

        passwordMatch.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordMatchLabel.setText("✅ You are set");
            } else {
                passwordMatchLabel.setText("❌ Passwords do not match or are too short");
            }
        });
        forgotButton.disableProperty().bind(passwordMatch.not());

        textFields = Arrays.asList(emailTextField, usernameTextField, passwordTextField, confpasswordTextField);
    }

    public void forgotPasswordOnAction (ActionEvent event) throws IOException{

        forgotMessageLabel.setText("Update error, please try again");

        if (!usernameTextField.getText().isBlank() && !emailTextField.getText().isBlank() && !passwordTextField.getText().isBlank() && !confpasswordTextField.getText().isBlank()) {
            confirmPass();
            forgot();
        } else {
            forgotMessageLabel.setText("Please fill all fields");
        }
    }

    public void forgot () {

        String checkData = "SELECT * FROM users_accounts WHERE username = '" + usernameTextField.getText() + "' AND email = '" + emailTextField.getText() +"'";

        Connect connectNow = new Connect();
        Connection connectDB = connectNow.getConnection();

        try {

            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(checkData);

            if (!resultSet.next()) {
                forgotMessageLabel.setText("Username or email cannot be found");
            } else {

                String updateData = "UPDATE users_accounts SET password = ?, update_date = ?" +
                        "WHERE username = '" + usernameTextField.getText() + "' AND email = '" + emailTextField.getText() +"'";

                try {

                    PreparedStatement preparedStatement = connectDB.prepareStatement(updateData);

                    preparedStatement.setString(1, passwordTextField.getText());

                    java.util.Date date = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    preparedStatement.setString(2, String.valueOf(sqlDate));

                    preparedStatement.executeUpdate();

                    forgotMessageLabel.setText("Password updated successfully");

                    forgotClearFix();
                    redirectToLoginPage();
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getCause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextTextField(Node source) throws IOException {
        int currentIndex = textFields.indexOf(source);

        if (currentIndex < textFields.size() - 1) {
            TextField nextField = textFields.get(currentIndex + 1);

            nextField.requestFocus();
        }
    }

    @FXML
    private void handleEnterKey(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            Node source = (Node) event.getSource();
            nextTextField(source);
        }
    }

    @FXML
    private void passwordKeyRelease(KeyEvent event) {
        if (passwordTextField.getText().equals(confpasswordTextField.getText()) && passwordTextField.getText().length() >= 8 && confpasswordTextField.getText().length() >= 8) {
            passwordMatchLabel.setText("✅ You are set");
        } else if (passwordTextField.getText().equals(confpasswordTextField.getText()) && passwordTextField.getText().length() < 8 && confpasswordTextField.getText().length() < 8){
            passwordMatchLabel.setText("❌ Password too short");
        } else {
            passwordMatchLabel.setText("❌ Do not match");
        }
    }

    public void confirmPass() {
        if (passwordTextField.getText().equals(confpasswordTextField.getText()) && passwordTextField.getText().length() >= 8 && confpasswordTextField.getText().length() >= 8) {
            passwordMatchLabel.setText("✅ You are set");
        } else if (passwordTextField.getText().equals(confpasswordTextField.getText()) && passwordTextField.getText().length() < 8 && confpasswordTextField.getText().length() < 8){
            passwordMatchLabel.setText("❌ Password too short");
        } else {
            passwordMatchLabel.setText("❌ Do not match");
        }
    }

    public void cancelButtonOnAction (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/login.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1520, 790);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
        stage.setTitle("Task Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public void forgotClearFix () {
        emailTextField.setText("");
        usernameTextField.setText("");
        passwordTextField.setText("");
        confpasswordTextField.setText("");
        forgotMessageLabel.setText("");
    }

    public void redirectToLoginPage() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/login.fxml")));
                Stage stage = (Stage) forgotButton.getScene().getWindow();
                Scene scene = new Scene(root, 1520, 790);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
                stage.setTitle("Task Manager - Login");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }
}
