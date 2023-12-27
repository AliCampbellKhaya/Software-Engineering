package edu.wpi.cs3733.c22.teamD.controllers.data;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import net.kurobako.gesturefx.GesturePane;

public class LocationDataController extends DataPageController {

  @FXML private JFXButton addLocation;
  @FXML private JFXButton deleteLocation;
  @FXML private JFXButton editLocation;
  @FXML private Label xCoordLabel;
  @FXML private Label nodeLabel;
  @FXML private Label yCoordLabel;
  @FXML private Label floorLabel;
  @FXML private Label buildingLabel;
  @FXML private Label typeLabel;
  @FXML private Label longLabel;
  @FXML private Label shortLabel;
  @FXML private TableView<Location> table;

  @FXML private TableColumn<Location, String> nodeID;
  @FXML private TableColumn<Location, String> floor;
  @FXML private TableColumn<Location, String> nodeType;
  @FXML private TableColumn<Location, String> longName;
  @FXML private TableColumn<Location, String> shortName;

  @FXML private GesturePane mapPane;
  private StackPane parentPane;
  private Pane iconLayer;
  private ImageView mapContainer;

  @FXML
  public void initialize() {
    this.parentPane = new StackPane();
    this.iconLayer = new Pane();

    final IDatabaseAPI<Location, String> instance = LocationImpl.getInstance();

    this.nodeID.setCellValueFactory(new PropertyValueFactory<>("nodeID"));
    this.floor.setCellValueFactory(new PropertyValueFactory<>("floor"));
    this.nodeType.setCellValueFactory(new PropertyValueFactory<>("nodeType"));
    this.longName.setCellValueFactory(new PropertyValueFactory<>("longName"));
    this.shortName.setCellValueFactory(new PropertyValueFactory<>("shortName"));

    final ObservableList<Location> data = FXCollections.observableArrayList();
    data.addAll(instance.getAll());

    this.table.setItems(data);

    this.mapContainer =
        new ImageView(
            new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/FL3.png"))));

    this.mapPane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);
    this.mapPane.setContent(this.parentPane);
    this.parentPane.getChildren().add(this.mapContainer);
    this.parentPane.getChildren().add(this.iconLayer);

    this.table.setOnMouseClicked(
        event -> {
          if (table.getSelectionModel().getSelectedItem() != null) {
            final Location l = table.getSelectionModel().getSelectedItem();
            this.nodeLabel.setText(l.getNodeID());
            this.xCoordLabel.setText(Integer.toString(l.getXCoord()));
            this.yCoordLabel.setText(Integer.toString(l.getYCoord()));
            this.floorLabel.setText(l.getFloor());
            this.buildingLabel.setText(l.getBuilding());
            this.typeLabel.setText(l.getNodeType());
            this.longLabel.setText(l.getLongName());
            this.shortLabel.setText(l.getShortName());

            this.iconLayer.getChildren().clear();

            final Pane nodeGraphic = new Pane();
            this.iconLayer.getChildren().add(nodeGraphic);

            /* Set the style of the node */
            nodeGraphic.setPrefSize(20, 20);
            nodeGraphic.setLayoutX(l.getXCoord() - 10);
            nodeGraphic.setLayoutY(l.getYCoord() - 10);
            nodeGraphic.setStyle("-fx-background-color: '#013A75'; -fx-background-radius: 12.5");

            switch (l.getFloor()) {
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

            this.mapPane.centreOn(new javafx.geometry.Point2D(l.getXCoord(), l.getYCoord()));
          }
        });

    Platform.runLater(() -> this.mapPane.centreOn(new javafx.geometry.Point2D(1650, 900)));

    this.addLocation.setOnAction(e -> this.loadPop("views/data/AddLocation.fxml"));
    this.editLocation.setOnAction(e -> this.loadPop("views/data/EditLocation.fxml"));
    this.deleteLocation.setOnAction(e -> this.loadPop("views/data/DeleteLocationEntity.fxml"));
  }
}
