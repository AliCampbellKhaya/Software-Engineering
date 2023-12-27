package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class EditEquipmentPopupController implements PopupController {
  public PopupController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML private TextField idEquip;
  @FXML private TextField loc;
  @FXML private TextField equipmentName;
  @FXML private TextField sterilized;

  @FXML
  public void initialize() {
    controller = new EditEquipmentPopupController();
    submit.setOnAction(
        e -> {
          updateFields();
        });
  }

  @FXML
  public void updateFields() {
    final MedicalEquipmentImpl locale = MedicalEquipmentImpl.getInstance();
    final int ID = Integer.parseInt(this.idEquip.getText());
    try {
      locale.modifyEntity(ID, "location", this.loc.getText());
      locale.modifyEntity(ID, "equipment", this.equipmentName.getText());
      locale.modifyEntity(ID, "status", this.sterilized.getText());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.popOver.hide();
  }
}
