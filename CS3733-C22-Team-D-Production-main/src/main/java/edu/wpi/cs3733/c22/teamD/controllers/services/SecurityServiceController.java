package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.SecurityService;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.SecurityServiceImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SecurityServiceController {
  @FXML private JFXComboBox assigneeField;
  @FXML private JFXComboBox locationField;
  @FXML private JFXComboBox urgencyField = new JFXComboBox();
  @FXML private JFXTextArea extraField;

  private SecurityServiceImpl securityServiceDBController;
  private ServiceRequestImpl serviceRequestDBController;
  private LocationImpl locationDBController;
  private EmployeeImpl employeeDBController;
  private String[][] location_display_list;
  private String[][] employee_display_list;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  @FXML private JFXButton submit = new JFXButton();
  @FXML private JFXButton clear = new JFXButton();

  @FXML
  public void initialize() {

    //
    //    @FXML private JFXComboBox assigneeField;
    //    @FXML private JFXComboBox locationField;
    //    @FXML private JFXComboBox urgencyField = new JFXComboBox();

    this.submit.setDisable(true);
    this.urgencyField.getItems().add("LOW");
    this.urgencyField.getItems().add("MED");
    this.urgencyField.getItems().add("HIGH");
    extraField.setPromptText("Enter details..");

    securityServiceDBController = SecurityServiceImpl.getInstance();
    locationDBController = LocationImpl.getInstance();
    employeeDBController = EmployeeImpl.getInstance();
    serviceRequestDBController = ServiceRequestImpl.getInstance();

    location_display_list = locationDBController.getLocationPkAndName();
    for (int i = 0; i < location_display_list.length; i++) {
      locationField.getItems().add(location_display_list[i][1]);
    }

    employee_display_list = employeeDBController.getEmployeePkAndName();

    for (int i = 0; i < employee_display_list.length; i++) {
      assigneeField.getItems().add(employee_display_list[i][1]);
    }
  }

  @FXML
  public void onSubmit() throws SQLException {
    int locationIndex = locationField.getSelectionModel().getSelectedIndex();
    String locationKey = location_display_list[locationIndex][0];
    int assignIndex = assigneeField.getSelectionModel().getSelectedIndex();
    int assignTo = Integer.parseInt(employee_display_list[assignIndex][0]);
    // String status = (String) urgencyField.getSelectionModel().getSelectedItem();
    String extraDetails = extraField.getText();
    String urgency = urgencyField.getSelectionModel().getSelectedItem().toString();

    SecurityService newSecurityService =
        new SecurityService(
            "SecurityService", assignTo, extraDetails, "IN_PROGRESS", urgency, "0", locationKey);
    serviceRequestDBController.addNewEntity(newSecurityService);
    System.out.println("Creating a request (securityService");
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
    extraField.clear();
    locationField.getSelectionModel().clearSelection();
    assigneeField.getSelectionModel().clearSelection();
    urgencyField.getSelectionModel().clearSelection();
    enableSubmitButton();
  }

  @FXML
  public boolean isFilledOption() { // only checks fields that are required
    boolean pass1 = ((String) this.locationField.getSelectionModel().getSelectedItem() != null);
    boolean pass3 = ((String) this.assigneeField.getSelectionModel().getSelectedItem() != null);
    boolean pass4 = ((String) this.urgencyField.getSelectionModel().getSelectedItem() != null);

    return pass1 && pass3 && pass4;
  }

  private void updateTextBox(ActionEvent e) {}

  public void navigateToAnother() {
    Navigation.navigate(Screen.MEDICAL_EQUIPMENT_DELIVERY);
  }
}
