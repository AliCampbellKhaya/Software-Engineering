package edu.wpi.cs3733.c22.teamD.controllers.data;

import com.jfoenix.controls.JFXButton;
import java.awt.*;
import javafx.fxml.FXML;

public interface DataPage {
  @FXML JFXButton addDB = new JFXButton();
  @FXML JFXButton modifyDB = new JFXButton();
  @FXML JFXButton deleteDB = new JFXButton();
}
