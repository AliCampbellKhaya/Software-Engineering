package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class EditEmployeePopupController implements PopupController {
  public PopupController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML private TextField nodeID;
  @FXML private TextField firstName;
  @FXML private TextField lastName;
  @FXML private TextField phone;
  @FXML private TextField job;

  @FXML
  public void initialize() {
    controller = new EditEmployeePopupController();
    submit.setOnAction(
        e -> {
          updateFields();
        });
  }

  @FXML
  public void updateFields() {
    final EmployeeImpl locale = EmployeeImpl.getInstance();
    final int ID = Integer.parseInt(this.nodeID.getText());
    try {
      locale.modifyEntity(ID, "firstName", this.firstName.getText());
      locale.modifyEntity(ID, "lastName", this.lastName.getText());
      locale.modifyEntity(ID, "phoneNumber", this.phone.getText());
      locale.modifyEntity(ID, "job", this.job.getText());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.popOver.hide();
  }
}
