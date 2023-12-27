package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import org.controlsfx.control.PopOver;

public interface PopupController {

  @FXML JFXButton submit = new JFXButton();

  void setPopOver(PopOver popOver);
}
