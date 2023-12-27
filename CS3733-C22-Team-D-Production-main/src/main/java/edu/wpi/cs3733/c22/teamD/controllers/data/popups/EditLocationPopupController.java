package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class EditLocationPopupController implements PopupController {

  public PopupController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML
  public void initialize() {
    controller = new EditLocationPopupController();
    submit.setOnAction(
        e -> {
          updateLocation();
        });
  }

  @FXML private TextField idNode;
  @FXML private TextField xCord;
  @FXML private TextField yCord;
  @FXML private TextField floorN;
  @FXML private TextField buildingN;
  @FXML private TextField nodeTypeN;
  @FXML private TextField nameLong;
  @FXML private TextField nameShort;

  @FXML
  public void updateLocation() {
    final LocationImpl locale = LocationImpl.getInstance();
    final String ID = this.idNode.getText();
    try {
      locale.modifyEntity(ID, "xCoord", this.xCord.getText());
      locale.modifyEntity(ID, "yCoord", this.yCord.getText());
      locale.modifyEntity(ID, "floor", this.floorN.getText());
      locale.modifyEntity(ID, "building", this.buildingN.getText());
      locale.modifyEntity(ID, "nodeType", this.nodeTypeN.getText());
      locale.modifyEntity(ID, "longName", this.nameLong.getText());
      locale.modifyEntity(ID, "shortName", this.nameShort.getText());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.popOver.hide();
  }
}
