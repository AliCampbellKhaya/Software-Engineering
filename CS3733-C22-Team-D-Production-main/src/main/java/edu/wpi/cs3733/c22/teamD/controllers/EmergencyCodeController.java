package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.slack.api.methods.SlackApiException;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.util.SlackUtil;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

public class EmergencyCodeController {

  @FXML private Label selectedCode;
  @FXML private Button EmergencyCodeButton;
  @FXML private Tooltip tooltippy;
  @FXML private JFXComboBox emergencyLocation;
  @FXML private JFXButton emergencySubmit;
  @FXML private JFXButton redButton;
  @FXML private JFXButton greenButton;
  @FXML private JFXButton pinkButton;
  @FXML private JFXButton blueButton;
  @FXML private JFXButton greyButton;
  @FXML private JFXButton amberButton;
  @FXML private JFXButton turquoiseButton;
  @FXML private JFXButton whiteButton;

  //  @FXML private JFXButton testButton = new JFXButton();

  private String[][] locationDisplayList;
  private LocationImpl locationImpl;
  private String currentColor = "";
  private SlackUtil slack;

  public void initialize() {
    slack = new SlackUtil();
    emergencySubmit.setDisable(true);
    locationImpl = LocationImpl.getInstance();

    locationDisplayList = LocationImpl.getInstance().getLocationPkAndName();
    for (int i = 0; i < locationDisplayList.length; i++) {
      emergencyLocation.getItems().add(locationDisplayList[i][1]);
    }

    emergencySubmit.setOnAction(
        e -> {
          sendAnnouncement();
        });

    emergencyLocation.setOnAction(
        e -> {
          enableSubmitButton();
        });

    greenButton.setOnAction(
        e -> {
          this.currentColor = "Green";
          updateLabel();
        });
    redButton.setOnAction(
        e -> {
          this.currentColor = "Red";
          updateLabel();
        });
    pinkButton.setOnAction(
        e -> {
          this.currentColor = "Pink";
          updateLabel();
        });
    greyButton.setOnAction(
        e -> {
          this.currentColor = "Grey";
          updateLabel();
        });
    blueButton.setOnAction(
        e -> {
          this.currentColor = "Blue";
          updateLabel();
        });
    amberButton.setOnAction(
        e -> {
          this.currentColor = "Amber";
          updateLabel();
        });
    turquoiseButton.setOnAction(
        e -> {
          this.currentColor = "Turquoise";
          updateLabel();
        });
    whiteButton.setOnAction(
        e -> {
          try {
            this.slack.sendMessage(
                "Code White in "
                    + locationDisplayList[emergencyLocation.getSelectionModel().getSelectedIndex()][
                        1]);
          } catch (IOException ex) {
            ex.printStackTrace();
          } catch (SlackApiException ex) {
            ex.printStackTrace();
          }
        });
  }

  private void sendAnnouncement() {
    String announcement = "";
    switch (currentColor) {
      case "Red":
        announcement = "Code Red";
        break;
      case "Green":
        announcement = "Code Green";
        break;
      case "Pink":
        announcement = "Code Pink";
        break;
      case "Blue":
        announcement = "Code Blue";
        break;
      case "Grey":
        announcement = "Code Grey";
        break;
      case "Amber":
        announcement = "Code Amber";
        break;
      case "Turquoise":
        announcement = "Code Turquoise";
        break;
      case "White":
        announcement = "Code White";
        break;
    }
    announcement +=
        " located in "
            + locationDisplayList[emergencyLocation.getSelectionModel().getSelectedIndex()][1];
    try {
      sendSlackNotifaction(announcement);
    } catch (SlackApiException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateLabel() {
    this.selectedCode.setText("Selected Code: " + this.currentColor);
    enableSubmitButton();
  }

  public void enableSubmitButton() { // Enables the submit button if all fields are full

    boolean case1 = emergencyLocation.getValue() != null;
    boolean case2 = selectedCode.getText().length() > 3;

    if (case1 && case2) {
      this.emergencySubmit.setDisable(false);
    } else {
      this.emergencySubmit.setDisable(true);
    }
  }

  @FXML
  private void handleButtonAction(ActionEvent event) {
    System.out.println("You clicked me!");
    selectedCode.setText("Hello World");
  }

  public void sendSlackNotifaction(String msg) throws SlackApiException, IOException {
    this.slack.sendMessage(msg);
  }

  public void clickImageAction(MouseEvent mouseEvent) {}
}
