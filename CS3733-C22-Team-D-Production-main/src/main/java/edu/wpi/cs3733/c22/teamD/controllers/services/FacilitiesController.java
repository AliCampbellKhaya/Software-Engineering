package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.entity.FacilitiesMaintenanceRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javafx.fxml.FXML;

public class FacilitiesController implements IServiceRequest {
  @FXML private JFXButton submit;
  @FXML private JFXButton clear;
  @FXML private JFXComboBox assigneeField;
  @FXML private JFXComboBox locationField;
  @FXML private JFXComboBox urgencyMed;
  @FXML private JFXTextArea extraField;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  // DB stuff --Jacob
  private String[][] locationDisplayList;
  private LocationImpl locationImpl;
  private EmployeeImpl employeeDBController;

  private String[][] location_display_list;
  private ArrayList<Employee> employee_display_list;
  private LocationImpl locationDBController;
  private ServiceRequestImpl serviceRequestDBController;

  @FXML
  public void initialize() { // populate db --Jacob
    submit.setDisable(true);
    extraField.setPromptText("Enter details..");
    locationImpl = LocationImpl.getInstance();

    locationDBController = LocationImpl.getInstance();
    employeeDBController = EmployeeImpl.getInstance();

    //    locationDisplayList = LocationImpl.getInstance().getLocationPkAndName();
    //    for (int i = 0; i < locationDisplayList.length; i++) {
    //      locationField.getItems().add(locationDisplayList[i][1]);
    //    }
    //    employee_display_list = employeeDBController.getAll(); // array list of employees
    //    for (int i = 0; i < employee_display_list.size(); i++) {
    //      assigneeField.getItems().add(employee_display_list.get(i).getFirstName());
    //    }

    // populates urgency
    this.urgencyMed.getItems().add("HIGH");
    this.urgencyMed.getItems().add("LOW");
    this.urgencyMed.getItems().add("MED");

    location_display_list = locationDBController.getLocationPkAndName();
    for (int i = 0; i < location_display_list.length; i++) {
      locationField.getItems().add(location_display_list[i][1]);
    }
    employee_display_list = employeeDBController.getAll(); // array list of employees
    for (int i = 0; i < employee_display_list.size(); i++) {
      assigneeField.getItems().add(employee_display_list.get(i).getFirstName());
    }
    serviceRequestDBController = ServiceRequestImpl.getInstance();
  }

  @FXML
  public void onSubmit() throws SQLException {
    int locationIndex = locationField.getSelectionModel().getSelectedIndex();
    String locationKey = location_display_list[locationIndex][0];
    int assignIndex = assigneeField.getSelectionModel().getSelectedIndex();
    int assigneeKey = employee_display_list.get(assignIndex).getEmployeeID();
    String urgency = (String) urgencyMed.getSelectionModel().getSelectedItem();
    String extraDetails = extraField.getText();

    // DB entity
    Random rand = new Random(); // creates a new uuid
    int rand1 = rand.nextInt(1000000);

    FacilitiesMaintenanceRequest newSubmission =
        new FacilitiesMaintenanceRequest(
            "FacilitiesMaintenanceRequest",
            assigneeKey,
            extraDetails,
            "INCOMPLETE",
            urgency,
            "0",
            locationKey);

    serviceRequestDBController.addNewEntity(newSubmission);
    System.out.println("Creating a request (facilities)");
    onClear();
    confirmationUtil.loadPop();
  }

  @FXML
  public void enableSubmitButton() { // Enables the submit button if all fields are full
    boolean case2 = (String) locationField.getSelectionModel().getSelectedItem() != null;
    boolean case3 = (String) assigneeField.getSelectionModel().getSelectedItem() != null;
    boolean case4 = (String) urgencyMed.getSelectionModel().getSelectedItem() != null;

    if (case2 && case3 && case4) {
      this.submit.setDisable(false);
    } else {
      this.submit.setDisable(true);
    }
  }

  public void onClear() { // clears all fields
    assigneeField.getSelectionModel().clearSelection();
    locationField.getSelectionModel().clearSelection();
    urgencyMed.getSelectionModel().clearSelection();
    extraField.clear();

    submit.setDisable(true); // necessary, in case button was enabled before clearing
  }

  public boolean isFilledOption() { // only checks fields that are required
    return true;
  }
}
