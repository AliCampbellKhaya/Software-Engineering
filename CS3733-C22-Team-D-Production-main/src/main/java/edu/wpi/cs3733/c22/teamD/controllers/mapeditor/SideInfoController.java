package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SideInfoController {

  @FXML private Label dirtyLabel;

  @FXML private Label cleanLabel;

  @FXML private Label floorLabel;

  @FXML
  public void initialize() {}

  public void setDirtyLabel(int count) {
    this.dirtyLabel.setText(Integer.toString(count));
  }

  public void setCleanLabel(int count) {
    this.cleanLabel.setText(Integer.toString(count));
  }

  public void setFloorLabel(String floor) {
    this.floorLabel.setText(floor);
  }
}
