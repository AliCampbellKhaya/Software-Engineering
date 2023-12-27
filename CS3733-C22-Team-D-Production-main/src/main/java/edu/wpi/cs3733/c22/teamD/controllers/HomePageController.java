package edu.wpi.cs3733.c22.teamD.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.ConnectionType;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import net.kurobako.gesturefx.GesturePane;

public class HomePageController {

  @FXML private JFXButton mapNav;
  @FXML private JFXToggleButton clientToggle;
  private boolean togState = !Ddb.getInstance().isEmbedded();
  public Pane codesButton;
  @FXML private TableView<ServiceRequest> requestTable;
  @FXML private TableColumn<ServiceRequest, Integer> serviceID;
  @FXML private TableColumn<ServiceRequest, String> creator;
  @FXML private TableColumn<ServiceRequest, String> type;
  @FXML private TableColumn<ServiceRequest, String> urgency;
  @FXML private TableColumn<ServiceRequest, String> locationID;
  @FXML private GesturePane imagePane;
  @FXML private StackPane parentPane;
  @FXML private GesturePane codePane;
  private Pane iconLayer;
  private ImageView mapContainer;

  private LocationImpl locationImpl;

  @FXML private JFXButton hospitalCodeButton = new JFXButton();

  @FXML
  public void initialize() {
    this.locationImpl = LocationImpl.getInstance();
    this.parentPane = new StackPane();
    this.iconLayer = new Pane();
    hospitalCodeButton.setOnMouseClicked(e -> navigateToCodes());

    if (togState) {
      this.togState = !this.togState;
      this.clientToggle.fire();
    }

    this.clientToggle.setOnAction(
        e -> {
          if (Ddb.getInstance().isEmbedded()) {
            try {
              Ddb.getInstance().switchDatabase(ConnectionType.CLIENT_SERVER);
            } catch (SQLException ex) {
              ex.printStackTrace();
            }
          } else {
            try {
              Ddb.getInstance().switchDatabase(ConnectionType.EMBEDDED);
            } catch (SQLException ex) {
              ex.printStackTrace();
            }
          }
          togState = !Ddb.getInstance().isEmbedded();
        });

    final IDatabaseAPI<ServiceRequest, Integer> instance = ServiceRequestImpl.getInstance();

    this.serviceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
    this.creator.setCellValueFactory(new PropertyValueFactory<>("creator_employee"));
    this.type.setCellValueFactory(new PropertyValueFactory<>("serviceRequestType"));
    this.urgency.setCellValueFactory(new PropertyValueFactory<>("urgency"));
    this.locationID.setCellValueFactory(new PropertyValueFactory<>("location"));

    final ObservableList<ServiceRequest> data = FXCollections.observableArrayList();

    data.addAll(instance.getAll());

    this.requestTable.setItems(data);

    this.mapContainer =
        new ImageView(
            new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/FL3.png"))));

    final ImageView alertImage =
        new ImageView(
            new Image(
                Objects.requireNonNull(App.class.getResourceAsStream("images/hospital-2.jpeg"))));

    this.codePane.setContent(alertImage);

    this.imagePane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);
    this.codePane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);

    this.codePane.setFitMode(GesturePane.FitMode.CENTER);
    this.codePane.setFitHeight(true);
    this.codePane.setGestureEnabled(false);

    alertImage.fitHeightProperty().bind(this.codePane.heightProperty());
    alertImage.setPreserveRatio(true);

    this.imagePane.setContent(this.parentPane);
    this.parentPane.getChildren().add(this.mapContainer);
    this.parentPane.getChildren().add(this.iconLayer);

    Platform.runLater(() -> this.imagePane.centreOn(new javafx.geometry.Point2D(1600, 926)));

    this.requestTable.setOnMouseClicked(
        event -> {
          if (requestTable.getSelectionModel().getSelectedItem() != null) {
            final ServiceRequest sr = requestTable.getSelectionModel().getSelectedItem();
            var locationList =
                this.locationImpl.getAll().stream()
                    .filter(e -> e.getNodeID().equals(sr.getLocation()))
                    .collect(Collectors.toList());

            if (locationList.isEmpty()) return;

            final Location location = locationList.get(0);

            this.iconLayer.getChildren().clear();

            final Pane nodeGraphic = new Pane();
            this.iconLayer.getChildren().add(nodeGraphic);

            /* Set the style of the node */
            nodeGraphic.setPrefSize(20, 20);
            nodeGraphic.setLayoutX(location.getXCoord() - 10);
            nodeGraphic.setLayoutY(location.getYCoord() - 10);
            nodeGraphic.setStyle("-fx-background-color: '#013A75'; -fx-background-radius: 12.5");

            switch (location.getFloor()) {
              case "L1":
                this.mapContainer.setImage(App.getFloors().get(0));
                break;

              case "L2":
                this.mapContainer.setImage(App.getFloors().get(1));
                break;

              case "1":
                this.mapContainer.setImage(App.getFloors().get(2));
                break;

              case "2":
                this.mapContainer.setImage(App.getFloors().get(3));
                break;

              case "3":
                this.mapContainer.setImage(App.getFloors().get(4));
                break;
            }

            this.imagePane.centreOn(
                new javafx.geometry.Point2D(location.getXCoord(), location.getYCoord()));
          }
        });

    this.mapNav.setOnMouseClicked(e -> Navigation.navigate(Screen.MAP_EDITOR));
  }

  @FXML
  public void navigateToEmployee() {
    Navigation.navigate(Screen.EMPLOYEE_DATA);
  }

  @FXML
  public void navigateToLocation() {
    Navigation.navigate(Screen.LOCATION_DATA);
  }

  @FXML
  public void navigateToEquipment() {
    Navigation.navigate(Screen.EQUIPMENT_DATA);
  }

  @FXML
  public void navigateToRequest() {
    Navigation.navigate(Screen.TRAFFIC);
  }

  @FXML
  public void navigateToMap() {
    Navigation.navigate(Screen.MAP_EDITOR);
  }

  @FXML
  public void navigateToCodes() {
    Navigation.navigate(Screen.EMERGENCY_CODES);
  }
}
