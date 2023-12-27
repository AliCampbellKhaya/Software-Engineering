package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LocationNodeInfoController {

  @FXML private Label id;
  @FXML private Label xCoord;
  @FXML private Label yCoord;
  @FXML private Label longName;
  @FXML private Label shortName;

  public void setId(String id) {
    this.id.setText(id);
  }

  public void setXCoord(String coord) {
    this.xCoord.setText(coord);
  }

  public void setYCoord(String coord) {
    this.yCoord.setText(coord);
  }

  public void setLongName(String name) {
    this.longName.setText(name);
  }

  public void setShortName(String name) {
    this.shortName.setText(name);
  }
}
