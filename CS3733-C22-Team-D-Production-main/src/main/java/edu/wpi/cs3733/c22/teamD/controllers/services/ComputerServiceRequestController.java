package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.ComputerRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ITEquipmentImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import javafx.fxml.FXML;

public class ComputerServiceRequestController implements IServiceRequest {
  @FXML private JFXButton submit;
  @FXML private JFXButton clear;
  @FXML private JFXComboBox cvEquipmentField;
  @FXML private JFXComboBox cvEmployeeField;
  @FXML private JFXComboBox cvLocationField;
  @FXML private JFXComboBox cvUrgency;
  @FXML private JFXTextArea CSExtraField;

  // DB stuff --Jacob
  private String[][] locationDisplayList;

  private String[][] ITEquipment_list;
  private String[][] employee_display_list;
  private LocationImpl locationImpl;
  private EmployeeImpl employeeDBController;
  private ITEquipmentImpl ITEquipmentController;
  private ServiceRequestImpl serviceRequest;

  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  @FXML
  public void initialize() { // populate db --Jacob
    submit.setDisable(true);
    CSExtraField.setPromptText("Enter details..");
    locationImpl = LocationImpl.getInstance();
    ITEquipmentController = ITEquipmentImpl.getInstance();
    employeeDBController = EmployeeImpl.getInstance();
    serviceRequest = ServiceRequestImpl.getInstance();

    cvEquipmentField.setOnAction(
        e -> {
          enableSubmitButton();
        });
    cvEmployeeField.setOnAction(
        e -> {
          enableSubmitButton();
        });
    cvLocationField.setOnAction(
        e -> {
          enableSubmitButton();
        });
    cvUrgency.setOnAction(
        e -> {
          enableSubmitButton();
        });

    locationDisplayList = LocationImpl.getInstance().getLocationPkAndName();
    for (int i = 0; i < locationDisplayList.length; i++) {
      cvLocationField.getItems().add(locationDisplayList[i][1]);
    }
    employee_display_list = employeeDBController.getEmployeePkAndName(); // array list of employees
    for (int i = 0; i < employee_display_list.length; i++) {
      cvEmployeeField.getItems().add(employee_display_list[i][1]);
    }
    ITEquipment_list = ITEquipmentController.getLocationPkAndName();
    for (int i = 0; i < ITEquipment_list.length; i++) {
      cvEquipmentField.getItems().add(ITEquipment_list[i][1]);
    }

    // populates urgency
    this.cvUrgency.getItems().add("HIGH");
    this.cvUrgency.getItems().add("MED");
    this.cvUrgency.getItems().add("LOW");
  }

  @FXML
  public void onSubmit() throws SQLException {
    int assignIndex = cvEmployeeField.getSelectionModel().getSelectedIndex();
    int assignTo = Integer.parseInt(employee_display_list[assignIndex][0]);
    int locationIndex = cvLocationField.getSelectionModel().getSelectedIndex();
    int itIndex = cvEquipmentField.getSelectionModel().getSelectedIndex();
    int itID = Integer.parseInt(ITEquipment_list[itIndex][0]);
    String locationKey = locationDisplayList[locationIndex][0];
    String cvUrgencyS = (String) cvUrgency.getSelectionModel().getSelectedItem();
    String CSExtraFieldS = CSExtraField.getText();

    ComputerRequest newComputerRequest =
        new ComputerRequest(
            "ComputerRequest",
            assignTo,
            CSExtraFieldS,
            "INCOMPLETE",
            cvUrgencyS,
            "initiatedTime",
            locationKey,
            itID);

    serviceRequest.addNewEntity(newComputerRequest);
    System.out.println("Creating a request (computer request)");
    onClear();
    confirmationUtil.loadPop();
  }

  @FXML
  public void enableSubmitButton() { // Enables the submit button if all fields are full

    boolean enabled = isFilledOption(); // helper
    this.submit.setDisable(!enabled);
  }

  public void onClear() { // clears all fields
    cvEquipmentField.getSelectionModel().clearSelection();
    cvEmployeeField.getSelectionModel().clearSelection();
    cvLocationField.getSelectionModel().clearSelection();
    cvUrgency.getSelectionModel().clearSelection();
    CSExtraField.clear();

    submit.setDisable(true); // necessary, in case button was enabled before clearing
  }

  public boolean isFilledOption() { // only checks fields that are required
    boolean case1 = ((String) cvEquipmentField.getSelectionModel().getSelectedItem() != null);
    boolean case2 = ((String) cvLocationField.getSelectionModel().getSelectedItem() != null);
    boolean case3 = ((String) cvEmployeeField.getSelectionModel().getSelectedItem() != null);
    boolean case4 = ((String) cvUrgency.getSelectionModel().getSelectedItem() != null);

    return case1 && case2 && case3 && case4;
  }
}
