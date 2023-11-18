package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.animation.FadeTransition;
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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
    private List<TextField> textFields;

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
        registerButton.disableProperty().bind(passwordMatch.not());

        textFields = Arrays.asList(emailTextField, firstnameTextField, lastnameTextField, usernameTextField, passwordTextField, confpasswordTextField);
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
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
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

    public void register() {
        String checkData = "SELECT * FROM users_accounts WHERE username = ? OR email = ?";

        Connect connectNow = new Connect();
        Connection connectDB = connectNow.getConnection();

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(checkData);
            preparedStatement.setString(1, usernameTextField.getText());
            preparedStatement.setString(2, emailTextField.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String existingUsername = resultSet.getString("username");
                String existingEmail = resultSet.getString("email");

                if (existingUsername.equals(usernameTextField.getText())) {
                    registerMessageLabel.setText(usernameTextField.getText() + " is already in use");
                    stopRegisteringAnimation();
                } else if (existingEmail.equals(emailTextField.getText())) {
                    sendReminderEmail(emailTextField.getText(), existingUsername);
                    registerMessageLabel.setText("Check your email, there is already a \n\nregistered user with this email");
                }
            } else {
                registerMessageLabel.setText("Registering...");

                String insertData = "INSERT INTO users_accounts (email, firstname, lastname, username, password, date) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connectDB.prepareStatement(insertData);

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
        passwordMatchLabel.setText("");
        registerMessageLabel.setText("");
    }

    public void redirectToMainPage() {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/home.fxml")));
                Stage stage = (Stage) registerButton.getScene().getWindow();
                Scene scene = new Scene(root, 1520, 790);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/Style/Style.css")).toExternalForm());
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

        FadeTransition fade = new FadeTransition(Duration.seconds(1), registerMessageLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    public void stopRegisteringAnimation() {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), registerMessageLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(event -> {
            registerMessageLabel.setVisible(false);
            registerMessageLabel.setText("");
        });
        fade.play();
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

    private void sendReminderEmail(String userEmail, String username) {
        final String usernameEmail = "team.taskmgr@gmail.com";
        final String password = "wtah przp lkzg gror";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usernameEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usernameEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Task Manager Accounts Reminder");

            // HTML y CSS para el cuerpo del correo
            String htmlBody = "<div style=\"background-color:#415055; padding:20px;\">" +
                    "<div style=\"text-align: center;\">" +
                    "<h1 style=\"color:#ffffff; font-size:24px;\">Task Manager</h1>" +
                    "</div>" +
                    "<div style=\"background-color:#ffffff; padding:20px; text-align: left; font-size:16px;\">" +
                    "<p style=\"color:#555555;\">Hello user,</p>" +
                    "<p style=\"color:#555555;\">You are receiving this email because it has been detected that you are trying to create a <br/>new account with this email. We remind you of your username so you can log in.</p>" +
                    "<p style=\"color:#555555;\">Username: <strong>" + username + "</strong></p>" +
                    "<p style=\"color:#555555;\">Regards<br/>Task Manager</p>" +
                    "</div>" +
                    "</div>";

            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
