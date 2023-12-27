package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class NodeFormController {

  @FXML private TextField nodeID;
  @FXML private TextField longName;
  @FXML private TextField shortName;
  @FXML private TextField type;
  @FXML private TextField building;

  @Setter private String floor;
  @Setter private MapEditorController controller;
  @Setter private PopOver popOver;

  private double xCoord;
  private double yCoord;

  /**
   * Set the x and y coordinates of a Location.
   *
   * @param xCoord X coordinate.
   * @param yCoord Y coordinate.
   */
  public void setCoordinates(double xCoord, double yCoord) {
    this.xCoord = xCoord;
    this.yCoord = yCoord;
  }

  /**
   * Creates a new Location object in the database
   *
   * @throws SQLException
   */
  @FXML
  public void createLocation() throws SQLException {
    if (this.nodeID.getText().isEmpty())
      throw new SQLException("Empty primary key"); // todo create custom exception
    LocationImpl.getInstance()
        .addNewEntity(
            new Location(
                this.nodeID.getText(),
                (int) this.xCoord,
                (int) this.yCoord,
                this.floor,
                this.building.getText(),
                this.type.getText(),
                this.longName.getText(),
                this.shortName.getText()));
    this.popOver.hide();
    this.controller.loadIcons();
  }
}
