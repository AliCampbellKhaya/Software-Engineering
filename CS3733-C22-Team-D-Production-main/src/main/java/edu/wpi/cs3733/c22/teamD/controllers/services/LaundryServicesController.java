package edu.wpi.cs3733.c22.teamD.controllers.services;

// Imports

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.entity.LaundryRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LaundryRequestImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import edu.wpi.cs3733.c22.teamD.util.ConfirmationUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LaundryServicesController implements IServiceRequest {
  // Instance of DB Controllers
  private LaundryRequestImpl laundryA;
  private LocationImpl locationDBController;
  private String[][] requestDisplayList;
  private String[][] locationDisplayList;

  // Buttons
  @Getter @FXML private JFXButton submit;
  @Getter @FXML private JFXButton clear;

  // Check boxes
  @Getter @FXML private JFXCheckBox laundryPickupCheckBox;
  @Getter @FXML private JFXCheckBox laundryDeliveryCheckBox;
  @Getter @FXML private JFXCheckBox bedLinensCheckBox;
  @Getter @FXML private JFXCheckBox patientGownsCheckBox;
  @Getter @FXML private JFXCheckBox scrubsCheckBox;
  @Getter @FXML private JFXCheckBox drapesCheckBox;
  @Getter @FXML private JFXCheckBox otherCheckBox;
  @Getter @FXML private TextField otherField;

  // Text fields
  @Getter @FXML private JFXComboBox locationCombo;
  @Getter @FXML private JFXComboBox assignTo;
  @Getter @FXML private JFXComboBox urgencyField;

  @Getter @FXML private JFXTextArea extraBox;

  private EmployeeImpl employeeDBController;

  private ArrayList<Employee> employee_display_list;
  private ConfirmationUtil confirmationUtil = new ConfirmationUtil();

  // TODO do javadocs
  @FXML
  public void initialize() {

    urgencyField.getItems().add("LOW");
    urgencyField.getItems().add("MED");
    urgencyField.getItems().add("HIGH");
    extraBox.setPromptText("Enter details..");

    // DB controller handling
    laundryA = LaundryRequestImpl.getInstance();
    locationDBController = LocationImpl.getInstance();
    employeeDBController = EmployeeImpl.getInstance();

    // populate location combo box

    locationDisplayList = LocationImpl.getInstance().getLocationPkAndName();
    for (int i = 0; i < locationDisplayList.length; i++) {
      locationCombo.getItems().add(locationDisplayList[i][1]);
    }
    employee_display_list = employeeDBController.getAll(); // array list of employees
    for (int i = 0; i < employee_display_list.size(); i++) {
      assignTo.getItems().add(employee_display_list.get(i).getFirstName());
    }
  }

  /** Stores TextField information to LaundryService entity */
  @FXML
  public void onSubmit() throws SQLException {
    int locationIndex = locationCombo.getSelectionModel().getSelectedIndex();
    String locationKey = locationDisplayList[locationIndex][0];
    int assignIndex = assignTo.getSelectionModel().getSelectedIndex();
    int assigneeKey = employee_display_list.get(assignIndex).getEmployeeID();
    String urgency = (String) urgencyField.getSelectionModel().getSelectedItem();
    String extraDetails = extraBox.getText();

    // DB entity
    Random rand = new Random(); // creates a new uuid
    int rand1 = rand.nextInt(1000000);

    LaundryRequest laundryServiceDB =
        new LaundryRequest(
            "LaundryRequest", assigneeKey, extraDetails, "IN_PROGRESS", urgency, "0", locationKey);

    ServiceRequestImpl.getInstance().addNewEntity(laundryServiceDB);
    System.out.println("Creating a request (laundry)");
    onClear();
    confirmationUtil.loadPop();
  }

  /** Disables check boxes for first option prompt */
  @FXML
  public void disableCheckBoxes() { // TODO fix logic
    if (laundryPickupCheckBox.isSelected()) { // if option1 chosen, block option2
      laundryDeliveryCheckBox.setDisable(!laundryDeliveryCheckBox.isDisabled());
      // TODO: set up for pick up
    } else if (laundryDeliveryCheckBox.isSelected()) { // if option2 chosen, block option1
      laundryPickupCheckBox.setDisable(!laundryPickupCheckBox.isDisabled());
    } else if (!laundryPickupCheckBox.isSelected()
        && !laundryDeliveryCheckBox.isSelected()) { // if option is deselected, show all options
      laundryDeliveryCheckBox.setDisable(false);
      laundryPickupCheckBox.setDisable(false);
      // TODO: set up for pick up
    }

    // TODO: else case error handling
    enableSubmitButton();
  }

  /** Enables otherTextField if the otherCheckBox option was selected */
  @FXML
  public void enableOtherTextField() {
    boolean isSelected = otherCheckBox.isSelected();
    otherField.setDisable(!isSelected);
    enableSubmitButton();
  }

  /** Enables submitButton if all mandatory fields are filled out */
  @FXML
  public void enableSubmitButton() {

    boolean goodToGo = isFilledOption(); // helper
    this.submit.setDisable(!goodToGo);
  }

  /**
   * Helper Function checks 4 conditions: 1) either laundryPickupCheckBox or laundryDeliveryCheckBox
   * has been selected 2) locationField is filled out 3) either bedLinensCheckBox, gownsCheckBox,
   * scrubsCheckBox, drapesCheckBox, or otherCheckBox is selected 4) if otherCheckBox is selected,
   * otherField is also filled out
   *
   * @return true if all requirements are met, false if 1+ are not
   */
  @FXML
  public boolean isFilledOption() {
    // Arrays of check boxes that need to have >=1 selected
    JFXCheckBox firstOption[] = {laundryPickupCheckBox, laundryDeliveryCheckBox};
    JFXCheckBox secondOption[] = {
      bedLinensCheckBox, patientGownsCheckBox, scrubsCheckBox, drapesCheckBox, otherCheckBox
    };

    boolean pass1 = false;
    boolean pass2 = false;
    boolean pass3 = !((String) this.locationCombo.getSelectionModel().getSelectedItem() == null);
    boolean pass4 = !(this.otherCheckBox.isSelected() && this.otherField.getText().isEmpty());

    for (int i = 0; i < firstOption.length; i++) {
      if (firstOption[i].isSelected()) {
        pass1 = true;
        i = firstOption.length;
      }
    }

    for (int i = 0; i < secondOption.length; i++) {
      if (secondOption[i].isSelected()) {
        pass2 = true;
        i = secondOption.length;
      }
    }

    return pass1 && pass2 && pass3 && pass4;
  }

  /** cancels request */
  public void cancel() {
    Navigation.navigate(Screen.TRAFFIC);
  }

  public void onClear() {
    laundryPickupCheckBox.setSelected(false);
    laundryDeliveryCheckBox.setSelected(false);
    bedLinensCheckBox.setSelected(false);
    patientGownsCheckBox.setSelected(false);
    scrubsCheckBox.setSelected(false);
    drapesCheckBox.setSelected(false);
    otherCheckBox.setSelected(false);

    locationCombo.getSelectionModel().clearSelection();
    otherField.clear();
    extraBox.clear();

    enableOtherTextField();
    disableCheckBoxes();
    enableSubmitButton();
  }
}
