package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class AddLocationPopupController implements PopupController {

  @FXML private TextField idNode;
  @FXML private TextField xCord;
  @FXML private TextField yCord;
  @FXML private TextField floorN;
  @FXML private TextField buildingN;
  @FXML private TextField nodeTypeN;
  @FXML private TextField nameLong;
  @FXML private TextField nameShort;

  public PopupController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML
  public void initialize() {
    controller = new AddLocationPopupController();
    submit.setOnAction(
        e -> {
          try {
            createLocation();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        });
  }

  @FXML
  public void createLocation() throws SQLException {
    if (this.idNode.getText().isEmpty())
      throw new SQLException("Empty primary key"); // todo create custom exception
    LocationImpl.getInstance()
        .addNewEntity(
            new Location(
                this.idNode.getText(),
                Integer.parseInt(this.xCord.getText()),
                Integer.parseInt(this.yCord.getText()),
                this.floorN.getText(),
                this.buildingN.getText(),
                this.nodeTypeN.getText(),
                this.nameLong.getText(),
                this.nameShort.getText()));
  }
}
