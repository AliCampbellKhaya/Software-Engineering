package edu.wpi.cs3733.c22.teamD.controllers.data.popups;

import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class AddEmployeePopupController implements PopupController {
  public PopupController controller;
  @Setter PopOver popOver = new PopOver();

  @FXML
  public void initialize() {
    controller = new AddEmployeePopupController();
    submit.setOnAction(
        e -> {
          try {
            addEmployee();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
        });
  }

  @FXML private TextField nodeID;
  @FXML private TextField firstName;
  @FXML private TextField lastName;
  @FXML private TextField phone;
  @FXML private TextField job;
  @FXML private TextField token;

  @FXML
  public void addEmployee() throws SQLException {
    if (this.nodeID.getText().isEmpty())
      throw new SQLException("Empty primary key"); // todo create custom exception
    EmployeeImpl.getInstance()
        .addNewEntity(
            new Employee(
                Integer.parseInt(this.nodeID.getText()),
                this.firstName.getText(),
                this.lastName.getText(),
                this.phone.getText(),
                this.job.getText(),
                this.token.getText()));
  }
}
