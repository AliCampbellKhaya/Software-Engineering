package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class AddServiceRequestTraffic {

  @FXML private JFXToggleButton toggleSwitch;
  @FXML private Text jacobText1;
  @FXML private Text niniText1;
  @FXML private Text nickText1;
  @FXML private Text aliText1;
  @FXML private Text bridgetText1;
  @FXML private Text jackieText1;
  @FXML private Text ilyasText1;
  @FXML private Text andyText1;
  @FXML private Button searchButton;

  public void navigateToHome() {
    Navigation.navigate(Screen.DEFAULT);
  }

  @FXML
  public void navToMedicalEquipmentDeliveryRequest() {
    Navigation.navigate(Screen.MEDICAL_EQUIPMENT_DELIVERY);
  }

  @FXML
  public void navToInternalTransport(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.INTERNAL_TRANSPORT);
  }

  @FXML
  public void navToSanitationServices(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.SANITATION);
  }

  @FXML
  public void navToLaundry(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.LAUNDRY);
  }

  @FXML
  public void navToComputerServices(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.COMPUTER_SERVICE);
  }

  @FXML
  public void navToSecurityServices(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.SECURITY);
  }

  @FXML
  public void navToMedicineDelivery(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.MEDICINE);
  }

  @FXML
  public void navToFacilities(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.FACILITIES_MAINTENANCE);
  }

  /** Toggles the programmer names next to the page that they designed */
  @FXML
  public void toggleNames() {
    Text[] names = {
      jacobText1, niniText1, nickText1, aliText1, jackieText1, andyText1, bridgetText1, ilyasText1
    }; // makes array of text boxes for ease

    for (int i = 0; i < names.length; i++) { // parses text boxes
      names[i].setVisible(toggleSwitch.isSelected());
      // if the toggle switch is selected, this = !true = false, and vis versa
    }
  }

  @FXML
  public void navToSearch() {
    Navigation.navigate(Screen.SEARCH);
  }

  /*
   @FXML
  public void navToExternalTransport(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.EXTRANSPORT);
  }


  @FXML
  public void navToReligiousRequest(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.RELIGIOUS);
  }


  @FXML
  public void navToAVServices(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.AVREQUEST);
  }

  */

  /*
  @FXML
  public void navToFood(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.FOODREQUEST);
  }

  @FXML
  public void navToGiftAndFloral(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.GIFTANDFLORAL);
  }

  @FXML
  public void navToLanguageInterpreter(javafx.event.ActionEvent e) {
    Navigation.navigate(Screen.LANGUAGE);
  }

   */
}

/**
 * ME DELIVERY NINI LAUNDRY ME BRIDGET INT. TRANSP JACKIE SANITIATION NICK COMP SERV ANDY MED
 * DELIVERY ILYAS FACILITIES MAINTENANCE ALI SECURITY
 */
