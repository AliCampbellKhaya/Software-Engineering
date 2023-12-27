package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class DeleteEquipmentPopupController implements PopupController {

  public PopupController controller;
  @Setter public PopOver popOver = new PopOver();

  @FXML private TextField id;

  @FXML
  public void initialize() {
    controller = new DeleteEquipmentPopupController();
    submit.setOnAction(
        e -> {
          try {
            deleteEquipment();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        });
  }

  @FXML
  public void deleteEquipment() throws SQLException {
    if (this.id.getText().isEmpty())
      throw new SQLException("Empty primary key"); // todo create custom exception
    EmployeeImpl.getInstance().deleteTuple(Integer.parseInt(id.getText()));
  }
}
