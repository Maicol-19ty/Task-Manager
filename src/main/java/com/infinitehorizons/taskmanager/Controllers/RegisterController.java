package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private ImageView regbrandingImageView;
    @FXML
    private Label registerMessageLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField confpasswordTextField;
    @FXML
    private Button registerButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label passwordMatchLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("Pestaña de registro inicializada.");
        File regbrandingFile = new File("src/main/resources/com/infinitehorizons/taskmanager/images/task-logo-2.png");
        if (regbrandingFile.exists()) {
            Image regbrandingImage = new Image(regbrandingFile.toURI().toString());
            regbrandingImageView.setImage(regbrandingImage);
        } else {
            System.out.println("No se pudo encontrar la imagen en la ubicación especificada.");
        }

        BooleanBinding passwordMatch = Bindings.createBooleanBinding(() ->
                        passwordTextField.getText().equals(confpasswordTextField.getText()),
                passwordTextField.textProperty(), confpasswordTextField.textProperty());

        passwordMatch.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    passwordMatchLabel.setText("✅ You are set");
                } else {
                    passwordMatchLabel.setText("❌ Do not match");
                }
            }
        });
        registerButton.disableProperty().bind(passwordMatch.not());
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

    public void loginButtonOnAction (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/login.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1520, 790);
        stage.setTitle("Task Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public void registerButtonOnAction (ActionEvent event) throws IOException {
        registerMessageLabel.setText("Failed to register, please try again");

        if (!emailTextField.getText().isBlank() && !firstnameTextField.getText().isBlank() && !lastnameTextField.getText().isBlank() && !usernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank() && !confpasswordTextField.getText().isBlank()) {
            confirmPass();
            startRegisteringAnimation();
            register();
        } else {
            registerMessageLabel.setText("Please enter all fields");
        }
    }

    public void register () {

        String checkUsername = "SELECT * FROM user_accounts WHERE username = '" + usernameTextField.getText() + "'";

        Connect connectNow = new Connect();
        Connection connectDB = connectNow.getConnection();

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(checkUsername);

            if (resultSet.next()) {
                registerMessageLabel.setText(usernameTextField.getText() + " is already in use");
                stopRegisteringAnimation();
            } else {

                registerMessageLabel.setText("Registering...");

                String insertData = "INSERT INTO user_accounts (email, firstname, lastname, username, password, date) VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connectDB.prepareStatement(insertData);

                preparedStatement.setString(1, emailTextField.getText());
                preparedStatement.setString(2, firstnameTextField.getText());
                preparedStatement.setString(3, lastnameTextField.getText());
                preparedStatement.setString(4, usernameTextField.getText());
                preparedStatement.setString(5, passwordTextField.getText());

                Date date = new Date();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                preparedStatement.setString(6, String.valueOf(sqlDate));

                preparedStatement.executeUpdate();

                registerMessageLabel.setText("User registered successfully");

                registerClearFields();

                redirectToMainPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void registerClearFields () {
        emailTextField.setText("");
        firstnameTextField.setText("");
        lastnameTextField.setText("");
        usernameTextField.setText("");
        passwordTextField.setText("");
        confpasswordTextField.setText("");
        confpasswordTextField.setText("");
    }

    public void redirectToMainPage() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/home.fxml")));
                Stage stage = (Stage) registerButton.getScene().getWindow();
                Scene scene = new Scene(root, 1520, 790);
                stage.setTitle("Task Manager - Home");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }

    public void startRegisteringAnimation() {
        registerMessageLabel.setText("Registering...");
        registerMessageLabel.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(3), registerMessageLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    public void stopRegisteringAnimation() {
        FadeTransition fade = new FadeTransition(Duration.seconds(3), registerMessageLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(event -> {
            registerMessageLabel.setVisible(false);
        });
        fade.play();
    }

}
