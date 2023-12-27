package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class ConfirmExitController {
  @FXML private JFXButton exitApplication = new JFXButton();
  @FXML private JFXButton cancelApp = new JFXButton();
  @Setter private ConfirmExitController controller;
  @FXML private ParentController parent = new ParentController();
  @Setter private PopOver popOver;

  @FXML
  public void initialize() {
    exitApplication.setOnAction(e -> confirmExit());
    cancelApp.setOnAction(e -> closeConfirm());
  }

  @FXML
  public void confirmExit() {
    parent.closeApplication();
  }

  @FXML
  public void closeConfirm() {
    this.popOver.hide();
    // parent.goToHome();
  }
}
