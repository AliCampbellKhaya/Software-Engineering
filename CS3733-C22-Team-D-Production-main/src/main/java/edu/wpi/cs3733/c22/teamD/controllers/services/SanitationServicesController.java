package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.database.entity.SanitationRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.*;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javafx.fxml.FXML;

public class SanitationServicesController implements IServiceRequest {
  @FXML private JFXButton submit;
  @FXML private JFXButton clear;

  @FXML private JFXComboBox type;
  @FXML private JFXComboBox employeeField;
  @FXML private JFXComboBox locationField;
  @FXML private JFXComboBox urgencyField;
  @FXML private JFXTextArea descriptionArea;

  // DB stuff
  private String[][] locationDisplayList;
  // private String[][] employee_display_list;
  private String[][] medEquipList;
  private LocationImpl locationImpl;
  private MedicalEquipmentImpl medO;
  private SanitationRequestImpl saniO;
  private EmployeeImpl employeeDBController;
  private ServiceRequestImpl serviceRequest;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  private MedicalEquipmentImpl medicalEquipmentDBController;
  private ArrayList<MedicalEquipment> medical_object_list;
  private ArrayList<Employee> employee_display_list;

  @FXML
  public void initialize() { // populate db
    submit.setDisable(true);
    descriptionArea.setPromptText("Enter details..");
    //
    //    type.getItems().add("Bed Sheets");
    //    type.getItems().add("IVs");
    //    type.getItems().add("Infusion Pump");
    //    type.getItems().add("Recliner");

    locationImpl = LocationImpl.getInstance();
    medO = MedicalEquipmentImpl.getInstance();
    saniO = SanitationRequestImpl.getInstance();
    this.serviceRequest = ServiceRequestImpl.getInstance();
    medicalEquipmentDBController = MedicalEquipmentImpl.getInstance();

    employeeDBController = EmployeeImpl.getInstance();
    saniO = SanitationRequestImpl.getInstance();

    locationDisplayList = LocationImpl.getInstance().getLocationPkAndName();
    for (int i = 0; i < locationDisplayList.length; i++) {
      locationField.getItems().add(locationDisplayList[i][1]);
    }

    employee_display_list = employeeDBController.getAll(); // array list of employees
    for (int i = 0; i < employee_display_list.size(); i++) {
      employeeField.getItems().add(employee_display_list.get(i).getFirstName());
    }

    medical_object_list = medicalEquipmentDBController.getAll();
    for (int i = 0; i < medical_object_list.size(); i++) {
      MedicalEquipment tmp = medical_object_list.get(i);
      if (tmp.getStatus().equals("AVAILABLE")) {
        type.getItems().add(tmp.getEquipmentType());
      }
    }
    //    medEquipList = MedicalEquipmentO.getInstance().getMedEquipPkAndName();
    //    for (int i = 0; i < medEquipList.length; i++) {
    //      type.getItems().add(medEquipList[i][1]);
    //    }

    // populates urgency
    this.urgencyField.getItems().add("LOW");
    this.urgencyField.getItems().add("MED");
    this.urgencyField.getItems().add("HIGH");
  }

  @FXML
  public void onSubmit() throws SQLException {
    String typeS = (String) type.getSelectionModel().getSelectedItem();
    int assignIndex = employeeField.getSelectionModel().getSelectedIndex();
    int assignTo = employee_display_list.get(assignIndex).getEmployeeID();
    int locationIndex = locationField.getSelectionModel().getSelectedIndex();
    String locationKey = locationDisplayList[locationIndex][0];
    String urgencyS = (String) urgencyField.getSelectionModel().getSelectedItem();
    String descriptionS = (String) descriptionArea.getText();
    // DB entity
    Random rand = new Random(); // creates a new uuid
    int rand1 = rand.nextInt(1000000);

    SanitationRequest newRequestSanitationServicesController =
        new SanitationRequest(
            "SanitationRequest", assignTo, descriptionS, "IN_PROGRESS", urgencyS, "0", locationKey);
    serviceRequest.addNewEntity(newRequestSanitationServicesController);
    System.out.println("Creating a request (sanitation service)");
    onClear();
    confirmationUtil.loadPop();
  }

  @FXML
  public void enableSubmitButton() { // Enables the submit button if all fields are full
    boolean case1 = (String) type.getSelectionModel().getSelectedItem() != null;
    boolean case2 = (String) locationField.getSelectionModel().getSelectedItem() != null;
    boolean case3 = (String) urgencyField.getSelectionModel().getSelectedItem() != null;

    if (case1 && case2 && case3) {
      this.submit.setDisable(false);
    } else {
      this.submit.setDisable(true);
    }
  }

  public void onClear() { // clears all fields
    type.getSelectionModel().clearSelection();
    employeeField.getSelectionModel().clearSelection();
    locationField.getSelectionModel().clearSelection();
    urgencyField.getSelectionModel().clearSelection();
    descriptionArea.clear();

    submit.setDisable(true); // necessary, in case button was enabled before clearing
  }

  public boolean isFilledOption() { // only checks fields that are required
    return true;
  }
}
