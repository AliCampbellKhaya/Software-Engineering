package edu.wpi.cs3733.c22.teamD.controllers.data;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeeDataController extends DataPageController {

  @FXML private JFXButton addEmployee;
  @FXML private JFXButton deleteEmployee;
  @FXML private JFXButton editEmployee;
  @FXML private TableColumn<ServiceRequest, Integer> serviceID;
  @FXML private TableColumn<ServiceRequest, String> status;
  @FXML private TableColumn<ServiceRequest, String> locationID;
  @FXML private Label idLabel;
  @FXML private Label firstLabel;
  @FXML private Label lastLabel;
  @FXML private Label phoneLabel;
  @FXML private Label jobLabel;
  @FXML private TableView<Employee> table;
  @FXML private TableView<ServiceRequest> srTable;

  @FXML private TableColumn<Employee, String> employeeID;
  @FXML private TableColumn<Employee, String> firstName;
  @FXML private TableColumn<Employee, String> lastName;
  @FXML private TableColumn<Employee, String> phoneNumber;
  @FXML private TableColumn<Employee, String> jobTitle;

  @FXML
  public void initialize() {
    final IDatabaseAPI<Employee, Integer> empInstance = EmployeeImpl.getInstance();
    final IDatabaseAPI<ServiceRequest, Integer> srInstance = ServiceRequestImpl.getInstance();

    this.employeeID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
    this.firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    this.lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    this.phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    this.jobTitle.setCellValueFactory(new PropertyValueFactory<>("job"));

    final ObservableList<Employee> data = FXCollections.observableArrayList();
    data.addAll(empInstance.getAll());

    this.table.setItems(data);

    this.table.setOnMouseClicked(
        event -> {
          if (table.getSelectionModel().getSelectedItem() != null) {
            final Employee e = table.getSelectionModel().getSelectedItem();
            this.idLabel.setText(Integer.toString(e.getEmployeeID()));
            this.firstLabel.setText(e.getFirstName());
            this.lastLabel.setText(e.getLastName());
            this.phoneLabel.setText(e.getPhoneNumber());
            this.jobLabel.setText(e.getJob());

            this.serviceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
            this.status.setCellValueFactory(new PropertyValueFactory<>("serviceStatus"));
            this.locationID.setCellValueFactory(new PropertyValueFactory<>("location"));

            final ObservableList<ServiceRequest> srData = FXCollections.observableArrayList();

            // todo change to assigned to
            var filtered =
                srInstance.getAll().stream()
                    .filter(sr -> sr.getCreator_employee() == e.getEmployeeID())
                    .collect(Collectors.toList());

            srData.addAll(filtered);

            this.srTable.setItems(srData);
          }
        });

    this.addEmployee.setOnAction(e -> this.loadPop("views/data/AddEmployee.fxml"));
    this.editEmployee.setOnAction(e -> this.loadPop("views/data/EditEmployee.fxml"));
    this.deleteEmployee.setOnAction(e -> this.loadPop("views/data/DeleteEmployeeEntity.fxml"));
  }
}
