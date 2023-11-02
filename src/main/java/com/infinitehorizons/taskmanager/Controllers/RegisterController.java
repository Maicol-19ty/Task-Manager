package com.infinitehorizons.taskmanager.Controllers;


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

import javax.swing.*;
import javafx.beans.value.ChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    private Stage stage;
    private Scene scene;
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
    private Button loginButton;
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
        if (passwordTextField.getText().equals(confpasswordTextField.getText())) {
            passwordMatchLabel.setText("✅ You are set");
        } else {
            passwordMatchLabel.setText("❌ Do not match");
        }
    }

    public void confirmPass() {
        if (passwordTextField.getText().equals(confpasswordTextField.getText())) {
            passwordMatchLabel.setText("✅ You are set");
        } else {
            passwordMatchLabel.setText("❌ Do not match");
        }
    }

    public void loginButtonOnAction (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/login.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1520, 790);
        stage.setTitle("Task Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public void registerButtonOnAction (ActionEvent event) throws IOException {
        registerMessageLabel.setText("Failed to register, please try again");
        confirmPass();

        if (!emailTextField.getText().isBlank() && !firstnameTextField.getText().isBlank() && !lastnameTextField.getText().isBlank() && !usernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank() && !confpasswordTextField.getText().isBlank()) {

        } else {
            registerMessageLabel.setText("Please enter all fields");
        }

    }






}
