package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class EditMedicalEquipment {

  @FXML private JFXComboBox<String> locationCB;
  private LocationImpl locationImpl = null;

  @Setter private String locationID = null;
  @Setter private int equipmentID;
  @Setter private PopOver popOver = null;

  @Setter private MapEditorController controller;

  @FXML
  public void initialize() {
    this.locationImpl = LocationImpl.getInstance();

    System.out.println(this.locationImpl.getAll().size());

    this.locationImpl
        .getAll()
        .forEach(
            e -> {
              this.locationCB.getItems().add(e.getNodeID());
            });

    System.out.println(locationID);
  }

  public void setLocation(String locationID) {
    this.locationCB.getSelectionModel().select(locationID);
  }

  @FXML
  public void onSubmit() {
    final MedicalEquipmentImpl locale = MedicalEquipmentImpl.getInstance();
    try {
      locale.modifyEntity(equipmentID, "location", locationCB.getValue());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.popOver.hide();
    this.controller.loadIcons();
  }
}
