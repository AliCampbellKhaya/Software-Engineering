package edu.wpi.cs3733.c22.teamD.controllers.services;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.c22.teamD.database.entity.InternalTransportationRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.*;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InternalTransportationController implements IServiceRequest {
  @FXML private JFXComboBox patientField;
  @FXML private JFXComboBox roomField;
  @FXML private TextField timeField;
  @FXML private JFXComboBox locationField;
  @FXML private DatePicker dateField;
  @FXML private JFXCheckBox yesRoundTrip;
  @FXML private JFXCheckBox noRoundTrip;
  @FXML private TextField roundTripPickUp;
  @FXML private JFXComboBox employeeField;
  @FXML private JFXComboBox statusField;
  @FXML private JFXComboBox urgencyField;
  @FXML private JFXButton submit = new JFXButton();
  @FXML private JFXButton clear;
  @FXML private TextField extraField;
  @FXML private JFXButton submitButton;
  @FXML private Label pickUpLabel;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  private InternalTransportationRequestImpl internalTransportationController;
  private ServiceRequestImpl serviceRequest;
  private LocationImpl locationDBController;
  private String[][] location_display_list;
  private PatientImpl patientDBController = null;
  private String[][] patient_display_list;
  private EmployeeImpl employeeDBController;
  private String[][] employee_display_list;

  @FXML
  public void initialize() throws IOException {
    // this.foo();

    this.pickUpLabel.setVisible(false);
    this.roundTripPickUp.setVisible(false);
    // this.roundTripPickUp.setDisable(true);

    // set statusField

    statusField.getItems().add("INCOMPLETE");
    statusField.getItems().add("IN_PROGRESS");
    statusField.getItems().add("COMPLETE");

    // set urgencyField
    urgencyField.getItems().add("LOW");
    urgencyField.getItems().add("MED");
    urgencyField.getItems().add("HIGH");

    // disables button
    submit.setDisable(true);

    internalTransportationController = InternalTransportationRequestImpl.getInstance();
    serviceRequest = ServiceRequestImpl.getInstance();

    // connects Location Table to locationField and roomField
    locationDBController = LocationImpl.getInstance();
    location_display_list = locationDBController.getLocationPkAndName();
    for (int i = 0; i < location_display_list.length; i++) {
      locationField.getItems().add(location_display_list[i][1]);
      if (location_display_list[i][1].length() > 6
          && location_display_list[i][1].startsWith("patient")) {
        roomField.getItems().add(location_display_list[i][1]);
        System.out.println(roomField);
      }
    }

    // connects Patient Table to patientField
    patientDBController = PatientImpl.getInstance();
    patient_display_list = patientDBController.getLocationPkAndName(); // array list of employees
    for (int i = 0; i < patient_display_list.length; i++) {
      patientField.getItems().add(patient_display_list[i][1]);
    }

    // connects Employee Table to employeeField
    employeeDBController = EmployeeImpl.getInstance();
    employee_display_list = employeeDBController.getEmployeePkAndName();
    for (int i = 0; i < employee_display_list.length; i++) {
      employeeField.getItems().add(employee_display_list[i][1]);
    }
  }

  @FXML
  public void onSubmit() throws SQLException {
    String patientName = (String) patientField.getSelectionModel().getSelectedItem();
    int roomNumber = roomField.getSelectionModel().getSelectedIndex();
    String time = timeField.getText();
    int desiredLocation = locationField.getSelectionModel().getSelectedIndex();
    String destinationKey = location_display_list[desiredLocation][0];
    String roomKey = location_display_list[roomNumber][0];
    String date = dateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String extraDetails = extraField.getText();
    String status = (String) statusField.getSelectionModel().getSelectedItem();
    String urgent = (String) urgencyField.getSelectionModel().getSelectedItem();
    int employeeIndex = employeeField.getSelectionModel().getSelectedIndex();
    int employee = Integer.parseInt(employee_display_list[desiredLocation][0]);

    // System.out.println(date);

    InternalTransportationRequest newInternalTransportation =
        new InternalTransportationRequest(
            "InternalTransportationRequest",
            employee,
            extraDetails,
            status,
            urgent,
            "0",
            roomKey,
            patientName,
            destinationKey,
            date);

    InternalTransportationRequest roundTripInternalTransportation =
        new InternalTransportationRequest(
            "InternalTransportationRequest",
            employee,
            extraDetails,
            status,
            urgent,
            "0",
            destinationKey,
            patientName,
            roomKey,
            date);
    serviceRequest.addNewEntity(newInternalTransportation);

    if (yesRoundTrip.isSelected()) {
      serviceRequest.addNewEntity(roundTripInternalTransportation);
    }
    System.out.println("Creating a request (internal transportation)");
    onClear();
    confirmationUtil.loadPop();
  }

  @FXML
  public void enableSubmitButton() {
    boolean enabled = isFilledOption(); // helper
    this.submit.setDisable(!enabled);
  }

  @FXML
  public void onClear() throws SQLException {
    roomField.getSelectionModel().clearSelection();
    timeField.clear();
    dateField.setValue(null);
    employeeField.getSelectionModel().clearSelection();
    statusField.getSelectionModel().clearSelection();
    urgencyField.getSelectionModel().clearSelection();
    extraField.clear();
    this.yesRoundTrip.setSelected(false);
    this.noRoundTrip.setSelected(false);
    patientField.getSelectionModel().clearSelection();
    locationField.getSelectionModel().clearSelection();
    disableCheckBoxes();
    enableSubmitButton();
  }

  @FXML
  public void disableCheckBoxes() throws SQLException {
    if (yesRoundTrip.isSelected()) {
      noRoundTrip.setDisable(!noRoundTrip.isDisabled());
      this.pickUpLabel.setVisible(true);
      this.roundTripPickUp.setVisible(true);
    } else if (noRoundTrip.isSelected()) {
      yesRoundTrip.setDisable(!yesRoundTrip.isDisabled());
      this.pickUpLabel.setVisible(false);
      this.roundTripPickUp.setVisible(false);
    } else if (!yesRoundTrip.isSelected() && !noRoundTrip.isSelected()) {
      noRoundTrip.setDisable(false);
      yesRoundTrip.setDisable(false);
      this.pickUpLabel.setVisible(false);
      this.roundTripPickUp.setVisible(false);
    }
    enableSubmitButton();
  }

  @FXML
  public boolean isFilledOption() {
    boolean pass1 = (this.dateField.getValue() != null);
    boolean pass2 = (this.extraField.getText() != null);
    boolean pass3 = ((String) this.patientField.getSelectionModel().getSelectedItem() != null);
    boolean pass4 = ((String) this.locationField.getSelectionModel().getSelectedItem() != null);
    boolean pass5 = (this.roomField.getSelectionModel().getSelectedItem() != null);
    boolean pass6 = (this.timeField.getText() != null);
    boolean pass7 = (this.yesRoundTrip.isSelected() || this.noRoundTrip.isSelected());
    boolean pass8 = ((String) this.employeeField.getSelectionModel().getSelectedItem() != null);
    boolean pass9 = ((String) this.statusField.getSelectionModel().getSelectedItem() != null);
    boolean pass10 = ((String) this.urgencyField.getSelectionModel().getSelectedItem() != null);
    return pass1 && pass2 && pass3 && pass4 && pass5 && pass6 && pass7 && pass8 && pass9 && pass10;
  }

  //  public void addNewEntity() throws SQLException {
  //    InternalTransportationRequest it = new InternalTransportationRequest();
  //    ServiceRequestImpl.getInstance().addNewEntity(it);
  //  }

  /* public void foo() throws IOException {
    var arrayList = InternalTransportationRequestImpl.getInstance().getAll();

    ArrayList<String> name = new ArrayList<>();
    FXMLLoader loader = new FXMLLoader(App.class.getResource("views/ServiceRequestCardPane.fxml"));

    //  iterate through each request where ServiceRequestStatus != COMPLETE
    for (InternalTransportationRequest request : arrayList) {
      if (!request.getServiceStatus().equals("COMPLETE")) {

        int id = request.getCreator_employee();
        var allEmployee = EmployeeImpl.getInstance().getAll();

        for (Employee e : allEmployee) {
          if (e.getEmployeeID() == id) {
            // String name = new String();
            name.add(e.getFirstName() + " " + e.getLastName());
          }
        }
      }
    }
    Pane pane = loader.load();
    App.getChildPane().setRight(pane);

    VBox box = (VBox) pane.getChildren().get(1);
    // InternalTransportationController.

    Label label = new Label();
    ArrayList<Label> labels = new ArrayList<>();

    int i;
    String x = null;
    for (i = 0; i < name.size(); i++) {
      x = name.get(i);
      labels.add(new Label(x));

      // style
      labels.get(i).setStyle("-fx-background-color: #FFFFFF");
      labels.get(i).setPrefWidth(175);
      labels.get(i).setPrefHeight(63);
      box.getChildren().add(labels.get(i));
    }
  }

   */

}
