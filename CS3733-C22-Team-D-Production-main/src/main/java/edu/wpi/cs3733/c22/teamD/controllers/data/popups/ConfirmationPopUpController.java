package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import javafx.fxml.FXML;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class ConfirmationPopUpController {

  public ConfirmationPopUpController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML
  public void initialize() {
    controller = new ConfirmationPopUpController();
  }
}
