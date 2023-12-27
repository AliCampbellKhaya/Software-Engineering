package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import edu.wpi.cs3733.c22.teamD.util.ArrayUtil;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class ParentController {

  @FXML private JFXDrawer sidebar;
  private ObservableList<Node> nodeList = null;
  private ImageView image = null;
  private VBox sidepane = null;

  private JFXButton exitButton;

  @FXML
  public void initialize() {
    try {
      final Enum<Screen>[] OPTIONS =
          new Screen[] {
            Screen.SEARCH,
            Screen.DEFAULT,
            Screen.MAP_EDITOR,
            Screen.EMPLOYEE_DATA,
            Screen.LOCATION_DATA,
            Screen.EQUIPMENT_DATA,
            Screen.TRAFFIC,
          };

      /* Load side pane .fxml */
      this.sidepane =
          FXMLLoader.load(Objects.requireNonNull(App.class.getResource("views/Sidebar.fxml")));

      /* Get the child VBox pane and ImageView */
      this.image = (ImageView) sidepane.getChildren().get(0);
      final VBox childPane = (VBox) sidepane.getChildren().get(2);

      /* Set button state */
      this.image.setVisible(false);
      this.sidebar.close();
      this.nodeList = childPane.getChildren();

      /* Set button defaults for each side icon */
      this.nodeList.forEach(
          ArrayUtil.lambdaCounter(
              (i, button) -> {
                final JFXButton ref = (JFXButton) button;
                ref.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                ref.setOnMouseEntered(
                    e -> ref.setStyle("-fx-background-color: #013D7B; -fx-text-fill: #F6BD38"));
                ref.setOnMouseExited(
                    e -> ref.setStyle("-fx-background-color: #012D5A; -fx-text-fill: white"));
                ref.setOnMouseClicked(e -> Navigation.navigate((Screen) OPTIONS[i]));
              }));

      this.exitButton = (JFXButton) sidepane.getChildren().get(3);
      this.exitButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      this.exitButton.setOnAction(event -> confirmExit());
      this.exitButton.setOnMouseEntered(
          e -> this.exitButton.setStyle("-fx-background-color: #013D7B; -fx-text-fill: #F6BD38"));
      this.exitButton.setOnMouseExited(
          e -> this.exitButton.setStyle("-fx-background-color: #012D5A; -fx-text-fill: white"));

      this.sidebar.setOnDrawerOpened(
          e -> {
            this.image.setVisible(true);
            this.nodeList.forEach(
                button -> ((JFXButton) button).setContentDisplay(ContentDisplay.LEFT));
            this.exitButton.setContentDisplay(ContentDisplay.LEFT);
          });

      this.sidebar.setSidePane(sidepane);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void confirmExit() {

    final FXMLLoader popUpExit = new FXMLLoader(App.class.getResource("views/ConfirmExit.fxml"));
    final PopOver popOverConfirm = new PopOver();
    popOverConfirm.setArrowSize(0);

    try {
      popOverConfirm.setContentNode(popUpExit.load());

      final ConfirmExitController confirmExitController = popUpExit.getController();
      confirmExitController.setPopOver(popOverConfirm);

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    popOverConfirm.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
  }

  @FXML
  public void openSidebar() {
    this.sidebar.open();
  }

  @FXML
  public void closeSidebar() {
    this.image.setVisible(false);
    this.nodeList.forEach(
        button -> ((JFXButton) button).setContentDisplay(ContentDisplay.GRAPHIC_ONLY));
    exitButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    this.exitButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    this.sidebar.close();
  }

  @FXML
  public void closeApplication() {
    Platform.exit();
  }
}
