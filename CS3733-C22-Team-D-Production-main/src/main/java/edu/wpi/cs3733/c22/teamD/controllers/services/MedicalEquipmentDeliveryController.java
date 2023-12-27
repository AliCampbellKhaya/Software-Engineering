package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipmentRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.*;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MedicalEquipmentDeliveryController implements IServiceRequest {
  @FXML private TextField quantityField = new TextField();
  @FXML private JFXComboBox locationField;
  @FXML private JFXComboBox assigneeField;
  @FXML private JFXComboBox medicalEquipmentField;
  @FXML private JFXComboBox urgencyField = new JFXComboBox();
  @FXML private JFXTextArea extraField;
  private MedicalEquipmentRequestImpl meserviceDBController;
  private LocationImpl locationDBController;
  private EmployeeImpl employeeDBController;
  private String[][] location_display_list;
  private ArrayList<Employee> employee_display_list;
  private ServiceRequestImpl serviceRequest;
  private MedicalEquipmentImpl medicalEquipmentDBController;
  private ArrayList<MedicalEquipment> medical_object_list;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  @FXML private JFXButton submit = new JFXButton();
  @FXML private JFXButton clear = new JFXButton();

  @FXML
  public void initialize() {
    this.serviceRequest = ServiceRequestImpl.getInstance();

    submit.setDisable(true);
    // ToDo Base this on a list of equipment (not in here)

    urgencyField.getItems().add("LOW");
    urgencyField.getItems().add("MED");
    urgencyField.getItems().add("HIGH");
    extraField.setPromptText("Enter details..");

    meserviceDBController = MedicalEquipmentRequestImpl.getInstance();
    locationDBController = LocationImpl.getInstance();
    employeeDBController = EmployeeImpl.getInstance();
    medicalEquipmentDBController = MedicalEquipmentImpl.getInstance();

    location_display_list = locationDBController.getLocationPkAndName();
    for (int i = 0; i < location_display_list.length; i++) {
      locationField.getItems().add(location_display_list[i][1]);
    }

    medical_object_list = medicalEquipmentDBController.getAll();
    for (int i = 0; i < medical_object_list.size(); i++) {
      MedicalEquipment tmp = medical_object_list.get(i);
      if (tmp.getStatus().equals("AVAILABLE")) {
        medicalEquipmentField.getItems().add(tmp.getEquipmentType());
      }
    }

    employee_display_list = employeeDBController.getAll(); // array list of employees
    for (int i = 0; i < employee_display_list.size(); i++) {
      assigneeField
          .getItems()
          .add(
              employee_display_list.get(i).getFirstName()
                  + " "
                  + employee_display_list.get(i).getLastName());
    }
  }

  @FXML
  public void onSubmit() throws SQLException {

    String quantity = quantityField.getText();
    int locationIndex = locationField.getSelectionModel().getSelectedIndex();
    String locationKey = location_display_list[locationIndex][0];
    int assignIndex = assigneeField.getSelectionModel().getSelectedIndex();
    int assignTo = employee_display_list.get(assignIndex).getEmployeeID();
    int medicalEquipmentIndex = medicalEquipmentField.getSelectionModel().getSelectedIndex();
    int medicalEquipmentKey = medical_object_list.get(medicalEquipmentIndex).getEquipmentID();
    String urgency = (String) urgencyField.getSelectionModel().getSelectedItem();
    String extraDetails = extraField.getText();

    Random rand = new Random();
    int rand_int1 = rand.nextInt(1000000);

    MedicalEquipmentRequest newMedicalEquipmentRequest =
        new MedicalEquipmentRequest(
            "MedicalEquipmentRequest",
            assignTo,
            extraDetails,
            "IN_PROGRESS",
            urgency,
            "0",
            locationKey,
            medicalEquipmentKey);
    serviceRequest.addNewEntity(newMedicalEquipmentRequest);

    System.out.println("Creating a request (medicalEquipment)");
    onClear();
    confirmationUtil.loadPop();
  }

  @FXML
  public void enableSubmitButton() {

    boolean enabled = isFilledOption(); // helper
    this.submit.setDisable(!enabled);
  }

  @FXML
  public void onClear() {
    quantityField.clear();
    extraField.clear();
    locationField.getSelectionModel().clearSelection();
    assigneeField.getSelectionModel().clearSelection();
    medicalEquipmentField.getSelectionModel().clearSelection();
    urgencyField.getSelectionModel().clearSelection();
    enableSubmitButton();
  }

  @FXML
  public boolean isFilledOption() { // only checks fields that are required
    boolean pass1 = ((String) this.locationField.getSelectionModel().getSelectedItem() != null);
    boolean pass2 = (this.quantityField.getText() != null);
    boolean pass3 = ((String) this.assigneeField.getSelectionModel().getSelectedItem() != null);
    boolean pass4 = ((String) this.urgencyField.getSelectionModel().getSelectedItem() != null);
    boolean pass5 =
        ((String) this.medicalEquipmentField.getSelectionModel().getSelectedItem() != null);

    return pass1 && pass2 && pass3 && pass4 && pass5;
  }

  private void updateTextBox(ActionEvent e) {}

  public void navigateToAnother() {
    Navigation.navigate(Screen.MEDICAL_EQUIPMENT_DELIVERY);
  }
}
