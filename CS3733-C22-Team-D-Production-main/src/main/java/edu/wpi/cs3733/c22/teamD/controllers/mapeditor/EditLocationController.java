package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class EditLocationController {

  @FXML private TextField nodeID;
  @FXML private TextField xCoord;
  @FXML private TextField yCoord;
  @FXML private TextField floor;
  @FXML private TextField building;
  @FXML private TextField nodeType;
  @FXML private TextField longName;
  @FXML private TextField shortName;

  @Setter private MapEditorController controller;
  @Setter private PopOver popOver;

  /**
   * The Location Object of the node.
   *
   * @param locale The Location Object of a node.
   */
  public void setFields(Location locale) {
    this.nodeID.setText(locale.getNodeID());
    this.xCoord.setText(String.valueOf(locale.getXCoord()));
    this.yCoord.setText(String.valueOf(locale.getYCoord()));
    this.floor.setText(locale.getFloor());
    this.building.setText(locale.getBuilding());
    this.nodeType.setText(locale.getNodeType());
    this.longName.setText(locale.getLongName());
    this.shortName.setText(locale.getShortName());
  }

  /** Update the fields from editing a node. */
  @FXML
  public void updateFields() {
    final LocationImpl locale = LocationImpl.getInstance();
    final String ID = this.nodeID.getText();
    try {
      locale.modifyEntity(ID, "xCoord", this.xCoord.getText());
      locale.modifyEntity(ID, "yCoord", this.yCoord.getText());
      locale.modifyEntity(ID, "floor", this.floor.getText());
      locale.modifyEntity(ID, "building", this.building.getText());
      locale.modifyEntity(ID, "nodeType", this.nodeType.getText());
      locale.modifyEntity(ID, "longName", this.longName.getText());
      locale.modifyEntity(ID, "shortName", this.shortName.getText());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.popOver.hide();
    this.controller.loadIcons();
  }
}

// todo update only necessary types
