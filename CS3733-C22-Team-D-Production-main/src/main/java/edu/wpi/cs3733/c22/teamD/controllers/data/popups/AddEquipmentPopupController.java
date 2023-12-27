package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class AddEquipmentPopupController implements PopupController {
  public PopupController controller;
  @Setter PopOver popOver = new PopOver();

  @FXML
  public void initialize() {
    controller = new AddEquipmentPopupController();
    submit.setOnAction(
        e -> {
          try {
            addEquipment();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        });
  }

  @FXML private TextField idEquip;
  @FXML private TextField loc;
  @FXML private TextField equipment;
  @FXML private TextField statusField;

  @FXML
  public void addEquipment() throws SQLException {
    if (this.idEquip.getText().isEmpty())
      throw new SQLException("Empty primary key"); // todo create custom exception
    MedicalEquipmentImpl.getInstance()
        .addNewEntity(
            new MedicalEquipment(
                Integer.parseInt(this.idEquip.getText()),
                this.loc.getText(),
                this.equipment.getText(),
                this.statusField.getText()));
  }
}
