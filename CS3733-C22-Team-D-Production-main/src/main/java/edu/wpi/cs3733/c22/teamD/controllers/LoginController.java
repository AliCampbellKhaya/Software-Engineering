package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.login.PBKDF2Hasher;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginController {

  @FXML private ImageView image;
  @FXML private ImageView logo;
  @FXML private JFXButton login = new JFXButton();
  @FXML private TextField username = new TextField();
  @FXML private PasswordField password = new PasswordField();

  @FXML private Label passwordDeniedLabel = new Label();
  @FXML private VBox whiteBox = new VBox();

  private EmployeeImpl employeeDBController;
  private ArrayList<Employee> employeeList = new ArrayList<>();

  private PBKDF2Hasher hasher = new PBKDF2Hasher();

  @FXML
  public void initialize() throws IOException {

    this.image.setPreserveRatio(true);
    this.image.isResizable();

    this.logo.setTranslateX(-32);
    this.logo.setTranslateY(16);

    this.logo.setFitWidth(456);
    this.logo.setFitHeight(532);

    this.whiteBox.setTranslateX(-32);
    whiteBox.setVisible(false);

    loginBoxAnimation(); // does the animation

    login.setOnAction(
        e -> {
          try {
            onLogin();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        });

    employeeDBController = EmployeeImpl.getInstance();
    employeeList = employeeDBController.getAll();
  }

  private void loginBoxAnimation() {
    ScaleTransition scaleLogo = new ScaleTransition();
    TranslateTransition moveLogo = new TranslateTransition();

    FadeTransition fadeBox = new FadeTransition();

    scaleLogo.setDuration(Duration.millis(1500));
    scaleLogo.setDelay(Duration.millis(1000));
    scaleLogo.setByX(-.5);
    scaleLogo.setByY(-.5);

    moveLogo.setDuration(Duration.millis(1500));
    moveLogo.setDelay(Duration.millis(1000));
    moveLogo.setToY(-128);

    fadeBox.setDuration(Duration.millis(2000));
    fadeBox.setDelay(Duration.millis(2500));
    fadeBox.setFromValue(0);
    fadeBox.setToValue(10);

    scaleLogo.setNode(logo);
    moveLogo.setNode(logo);

    fadeBox.setNode(whiteBox);

    scaleLogo.play();

    moveLogo.setOnFinished(
        e -> {
          whiteBox.setVisible(true);
        });
    moveLogo.play();
    fadeBox.play();
  }

  public void onLogin() throws IOException {
    // this is where we check password
    int employeeID;
    Employee sampleEmployee = new Employee();
    try {
      employeeID = Integer.parseInt(username.getText());
    } catch (NumberFormatException e) {
      // passwordDeniedLabel.setVisible(true);
      username.clear();
      password.clear();
      return;
    }

    boolean success = false;
    for (Employee employee : employeeList) {
      if (employee.getEmployeeID() == employeeID) {
        sampleEmployee = employee;
        success = true;
      }
    }

    if (!success) {
      sampleEmployee = null;
    }

    //    if (sampleEmployee.getToken() == null) {
    //      sampleEmployee.setToken(employeeDBController.addPassword(password.getText()));
    //    }
    //
    //    if (hasher.checkPassword(password.getText().toCharArray(), sampleEmployee.getToken())) {
    //      Navigation.navigate(Screen.PARENT);
    //    } else {
    //      passwordDeniedLabel.setVisible(true);
    //    }

    if (sampleEmployee != null) {
      if (sampleEmployee.getToken().equals(password.getText())) {
        Navigation.navigate(Screen.PARENT);
      } else {
        // passwordDeniedLabel.setVisible(true);
        username.clear();
        password.clear();
        return;
      }
    } else {
      // passwordDeniedLabel.setVisible(true);
      username.clear();
      password.clear();
      return;
    }

    // Navigation.navigate(Screen.PARENT);
  }
}
