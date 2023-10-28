package com.infinitehorizons.taskmanager.Controllers;

import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    private Stage stage;
    private Scene scene;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordShow;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Hyperlink forgotPass;
    @FXML
    private CheckBox selectShowPass;

    public void registerButtonOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/register.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1280, 720);
        stage.setTitle("Task Manager - Register");
        stage.setScene(scene);
        stage.show();
    }

    public void loginButtonOnAction(ActionEvent event) throws IOException {
        loginMessageLabel.setText("Failed to log in, incorrect username or password");

        if (!usernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter username and password");
        }

    }

    public void forgotPassOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/infinitehorizons/taskmanager/forgot.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1280, 720);
        stage.setTitle("Task Manager - Forgot Password");
        stage.setScene(scene);
        stage.show();
    }

    public void showPassOnAction() {
        if (selectShowPass.isSelected()) {
            passwordShow.setText(passwordTextField.getText());
            passwordShow.setVisible(true);
            passwordTextField.setVisible(false);
        } else {
            passwordTextField.setText(passwordShow.getText());
            passwordShow.setVisible(false);
            passwordTextField.setVisible(true);
        }
    }

    public void initialize (URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("src//main//resources//com//infinitehorizons//taskmanager//images//task-logo.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);
    }


    public void validateLogin () {
        Connect connectNow = new Connect();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM user_accounts WHERE username = '" + usernameTextField.getText() + "' AND password = '" + passwordTextField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(verifyLogin);

            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    loginMessageLabel.setText("Congratulations!");
                } else {
                    loginMessageLabel.setText("Invalid login. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }


}