package edu.wpi.cs3733.c22.teamD.controllers.mapeditor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.ServiceRequestImpl;
import edu.wpi.cs3733.c22.teamD.util.ArrayUtil;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;

public class MapEditorController {

  private final String[] LEVELS = new String[] {"L1", "L2", "1", "2", "3"};
  @FXML private VBox hiddenFloorVBox;
  @FXML private Rectangle lowerLevelTwo;
  @FXML private Label cleanCount;
  @FXML private Label dirtyCount;
  @FXML private Group sideViewPopUp;
  private LocationImpl locationImpl;
  private MedicalEquipmentImpl medicalEquipmentImpl;
  private ServiceRequestImpl serviceRequestImpl;
  @FXML private ImageView visibilityIcon;
  @FXML private ImageView sideMapContainer;
  @FXML private JFXButton visibility;
  @FXML private GesturePane pane;
  @FXML private JFXNodesList nodesList;
  @FXML private JFXNodesList settingNodeList;
  @FXML private JFXNodesList visibilityNodeList;
  private ImageView mapContainer;
  private StackPane parentPane;
  private Image eyeOn;
  private Image eyeOff;
  private Pane iconLayer;
  private ArrayList<Location> locationList;
  private ArrayList<Pane> sideFloor;
  private String floor;
  private Group locationGroup;
  private Group equipmentGroup;
  private Group requestGroup;

  /** Create map view. */
  @FXML
  public void initialize() {
    this.locationImpl = LocationImpl.getInstance();
    this.medicalEquipmentImpl = MedicalEquipmentImpl.getInstance();
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.locationList = this.locationImpl.getAll();

    this.eyeOff =
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/eye-off.png")));
    this.eyeOn =
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/eye-on.png")));
    this.visibilityIcon.setImage(this.eyeOn);
    this.loadSideView();

    this.locationGroup = new Group();
    this.equipmentGroup = new Group();
    this.requestGroup = new Group();
    this.parentPane = new StackPane();
    this.iconLayer = new Pane();
    this.sideFloor = new ArrayList<>();

    /* Remove the scroll bar from the gesture view */
    this.pane.setContent(parentPane);
    this.pane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);

    /* Instantiate the image to the first floor */
    this.mapContainer = new ImageView(App.getFloors().get(0));
    this.sideMapContainer =
        new ImageView(
            new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/SV.png"))));

    this.sideMapContainer.setPreserveRatio(true);

    final Pane lower1 = new Pane();
    final Pane floor1 = new Pane();
    final Pane floor3 = new Pane();

    this.sideFloor.add(lower1);
    this.sideFloor.add(floor1);
    this.sideFloor.add(floor3);

    /* Add the image and icon overlay to the StackPane */
    parentPane.getChildren().add(this.mapContainer);
    parentPane.getChildren().add(this.iconLayer);

    this.iconLayer.getChildren().add(this.locationGroup);
    this.iconLayer.getChildren().add(this.equipmentGroup);
    this.iconLayer.getChildren().add(this.requestGroup);

    this.floor = LEVELS[0];
    this.loadIcons();

    final int[] x = {1684, 1684, 1698, 1698, 1684};
    final int[] y = {926, 926, 930, 924, 926};

    /* Set node list button functions */
    this.settingNodeList.setRotate(180);
    this.visibilityNodeList.setRotate(180);
    this.nodesList.setRotate(180);
    this.nodesList
        .getChildren()
        .forEach(
            ArrayUtil.lambdaCounter(
                (i, button) -> {
                  if (i == 6) return;
                  final JFXButton ref = (JFXButton) button;
                  if (Objects.equals(ref.getId(), "mainIcon")) return;
                  ref.setOnAction(
                      e -> {
                        this.nodesList.animateList(false);
                        this.mapContainer.setImage(App.getFloors().get(i - 1));
                        this.floor = LEVELS[i - 1];
                        this.loadIcons();
                        this.pane.centreOn(new javafx.geometry.Point2D(x[i - 1], y[i - 1]));
                      });
                }));
    /* Set the double click feature for creating a node */
    this.iconLayer.setOnMouseClicked(
        event -> {
          if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
            this.promptCreateNode(event);
        });

    Platform.runLater(() -> this.pane.centreOn(new javafx.geometry.Point2D(1684, 926)));
  }

  public void loadSideView() {
    final String[] floors = new String[] {"3", "1", "L1"};

    this.hiddenFloorVBox
        .getChildren()
        .forEach(
            ArrayUtil.lambdaCounter(
                (i, sidePane) -> {
                  final PopOver popOver = new PopOver();
                  popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);

                  var arrayList =
                      this.medicalEquipmentImpl.getAll().stream()
                          .filter(
                              e -> {
                                final String nodeID = e.getLocation();

                                var locationList =
                                    this.locationList.stream()
                                        .filter(a -> nodeID.equals(e.getLocation()))
                                        .collect(Collectors.toList());

                                if (!locationList.isEmpty()) {
                                  final Location locale = locationList.get(0);
                                  return locale.getFloor().equals(floors[i]);
                                }
                                return false;
                              })
                          .collect(Collectors.toList());

                  sidePane.setOnMouseClicked(
                      sideEvent -> {
                        final FXMLLoader form =
                            new FXMLLoader(App.class.getResource("views/mapeditor/SideInfo.fxml"));
                        try {
                          popOver.setContentNode(form.load());

                          final SideInfoController controller = form.getController();

                          long dirtyCount =
                              arrayList.stream()
                                  .filter(e -> e.getStatus().equals("IN_USE"))
                                  .count();
                          long cleanCount =
                              arrayList.stream()
                                  .filter(e -> e.getStatus().equals("AVAILABLE"))
                                  .count();

                          controller.setFloorLabel(floors[i]);
                          controller.setDirtyLabel((int) dirtyCount);
                          controller.setCleanLabel((int) cleanCount);
                        } catch (IOException e) {
                          e.printStackTrace();
                        }

                        popOver.show(sidePane);
                      });

                  sidePane.setOnMouseExited(sideEvent -> popOver.hide());
                }));
  }

  /** Visualizes the ArrayList<Location> as circles on the map. */
  public void loadIcons() {
    this.locationImpl.refresh();
    this.medicalEquipmentImpl.refresh();
    this.locationList = this.locationImpl.getAll();

    this.locationGroup.getChildren().clear();
    this.equipmentGroup.getChildren().clear();

    for (var node : locationList) {
      if (Objects.equals(node.getFloor(), floor)) {
        /* Create the node and add it to the icon layer */
        final Pane nodeGraphic = new Pane();
        this.locationGroup.getChildren().add(nodeGraphic);

        /* Set the style of the node */
        nodeGraphic.setPrefSize(20, 20);
        nodeGraphic.setLayoutX(node.getXCoord() - 10);
        nodeGraphic.setLayoutY(node.getYCoord() - 10);
        nodeGraphic.setStyle("-fx-background-color: '#013A75'; -fx-background-radius: 12.5");

        final PopOver popOver = new PopOver();
        final PopOver editPop = new PopOver();

        popOver.setArrowSize(0);
        editPop.setArrowSize(0);

        /* When the node is clicked, show information */
        nodeGraphic.setOnMouseClicked(
            event -> {
              if (event.getButton() == MouseButton.PRIMARY) this.displayNodeInfo(node, popOver);
              else if (event.getButton() == MouseButton.SECONDARY) this.manageNode(node, editPop);
            });

        /* If the PopOver is detached, keep it there */
        nodeGraphic.setOnMouseExited(
            event -> {
              if (!popOver.isDetached()) popOver.hide();
            });

        /* Set draggable feature for the location nodes */
        nodeGraphic.setOnMouseDragged(
            event -> {
              if (event.getButton() == MouseButton.PRIMARY && event.isShiftDown()) {
                this.pane.setGestureEnabled(false);
                nodeGraphic.setLayoutX(nodeGraphic.getLayoutX() + event.getX());
                nodeGraphic.setLayoutY(nodeGraphic.getLayoutY() + event.getY());
              }
            });

        /* Update the coordinates after dragging in the database and object */
        nodeGraphic.setOnMouseReleased(
            event -> {
              /* If the mouse dragged the node, update it */
              this.pane.setGestureEnabled(true);
              if (!event.isStillSincePress()) {
                final int newX = (int) nodeGraphic.getLayoutX();
                final int newY = (int) nodeGraphic.getLayoutY();

                try {
                  this.locationImpl.modifyEntity(node.getNodeID(), "xCoord", newX);
                  this.locationImpl.modifyEntity(node.getNodeID(), "yCoord", newY);
                } catch (SQLException e) {
                  System.err.println("[ERROR] Issue updating coordinates in DB.");
                }

                node.setXCoord(newX);
                node.setYCoord(newY);

                this.loadIcons();
              }
            });
      }
    }
    this.displayMedicalEquipment();
    this.displayServiceRequest();
  }

  /**
   * Prompt a PopOver to create a new node.
   *
   * @param event The MouseEvent to invoke the form to appear.
   */
  private void promptCreateNode(MouseEvent event) {
    /* Load the PopOver to create the node */
    final FXMLLoader form = new FXMLLoader(App.class.getResource("views/mapeditor/NodeForm.fxml"));
    final PopOver popOver = new PopOver();
    popOver.setArrowSize(0);

    try {
      popOver.setContentNode(form.load());

      final NodeFormController menu = form.getController();
      menu.setController(this);

      menu.setFloor(this.floor);
      menu.setPopOver(popOver);
      menu.setCoordinates(event.getX(), event.getY());
    } catch (IOException e) {
      e.printStackTrace();
    }

    popOver.show(App.getPrimaryStage(), event.getSceneX(), event.getSceneY());
  }

  /**
   * Display the attributes for a given Location node.
   *
   * @param locale The Location Object of the node.
   * @param popOver The PopOver object for displaying the .fxml.
   */
  private void displayNodeInfo(Location locale, PopOver popOver) {
    try {
      final FXMLLoader form =
          new FXMLLoader(App.class.getResource("views/mapeditor/LocationNodeInfo.fxml"));
      popOver.setContentNode(form.load());
      popOver.setTitle(String.format("%s Information", locale.getNodeID()));

      final LocationNodeInfoController controller = form.getController();

      controller.setId(locale.getNodeID());
      controller.setXCoord(String.valueOf(locale.getXCoord()));
      controller.setYCoord(String.valueOf(locale.getYCoord()));
      controller.setLongName(locale.getLongName());
      controller.setShortName(locale.getShortName());

    } catch (IOException e) {
      e.printStackTrace();
    }
    final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    popOver.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
  }

  /**
   * Handles the editing and deleting of a Location node.
   *
   * @param locale The Location Object of the node.
   * @param editPop The PopOver object for displaying the attributes.
   */
  private void manageNode(Location locale, PopOver editPop) {
    final ContextMenu options = new ContextMenu();
    final MenuItem edit = new MenuItem("Edit location");
    final MenuItem delete = new MenuItem("Delete location");

    edit.setOnAction(
        editEvent -> {
          final FXMLLoader editForm =
              new FXMLLoader(App.class.getResource("views/mapeditor/EditLocation.fxml"));

          try {
            editPop.setContentNode(editForm.load());
            final EditLocationController editController = editForm.getController();

            editController.setController(this);
            editController.setPopOver(editPop);
            editController.setFields(locale);
          } catch (IOException ex) {
            ex.printStackTrace();
          }

          final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
          editPop.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
        });

    delete.setOnAction(
        deleteEvent -> {
          try {
            this.locationImpl.deleteTuple(locale.getNodeID());
            this.loadIcons();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });

    options.getItems().addAll(edit, delete);

    final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    options.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
  }

  /** Prompt the user to choose a .csv to load into the map. */
  @FXML
  public void uploadCSV() {
    final FileChooser fileChooser = new FileChooser();
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Comma Separated Values", "*.csv", "*.CSV"));

    final Stage popUpDialog = new Stage();
    final File selectedFile = fileChooser.showOpenDialog(popUpDialog);

    popUpDialog.show();

    if (selectedFile != null) {
      try {
        this.locationImpl.restoreFromCSV(selectedFile);
      } catch (IOException | SQLException e) {
        System.err.println("File was not found."); // todo handle this
        e.printStackTrace();
      }
    } else {
      System.err.println("Backup file was not selected.");
    }

    this.loadIcons();
    popUpDialog.close();
  }

  /** Prompt the user to choose a directory to save a .csv to. */
  @FXML
  public void downloadCSV() {
    final FileChooser fileChooser = new FileChooser();
    final FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("Comma Separated Values", "*.csv", "*.CSV");
    fileChooser.getExtensionFilters().add(extFilter);

    final Stage popUpDialog = new Stage();
    final File file = fileChooser.showSaveDialog(popUpDialog);

    if (file != null) {
      try {
        this.locationImpl.storeToCSV(file);
      } catch (IOException e) {
        System.err.println("Error saving to the file.");
      }
    }
  }

  /** Display the MedicalEquipment node. */
  private void displayMedicalEquipment() {
    ArrayList<MedicalEquipment> meArrayList = this.medicalEquipmentImpl.getAll();

    for (var node : meArrayList) {
      final String id = node.getLocation();

      final List<Location> list =
          this.locationList.stream()
              .filter(e -> e.getNodeID().equals(id))
              .collect(Collectors.toList());

      if (list.isEmpty()) return;

      final Location singular = list.get(0);

      if (Objects.equals(singular.getFloor(), floor)) {
        /* Load the equipment icon and render it */
        final Image i =
            new Image(
                Objects.requireNonNull(App.class.getResourceAsStream("images/equipment.png")));
        final ImageView icon = new ImageView(i);
        this.equipmentGroup.getChildren().add(icon);

        /* Set medical equipment icon */
        icon.setFitWidth(25);
        icon.setFitHeight(25);
        icon.setLayoutX(singular.getXCoord() - 12.5);
        icon.setLayoutY(singular.getYCoord() - 12.5);

        /* Edit menu for medical equipment */
        final ContextMenu options = new ContextMenu();
        final MenuItem edit = new MenuItem("Edit equipment");
        options.getItems().addAll(edit);
        icon.setOnContextMenuRequested(
            event -> {
              final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
              options.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
            });

        /* Drag for medical equipment */
        icon.setOnMouseDragged(
            event -> {
              if (event.getButton() == MouseButton.PRIMARY && event.isShiftDown()) {
                this.pane.setGestureEnabled(false);
                icon.setLayoutX(icon.getLayoutX() + event.getX());
                icon.setLayoutY(icon.getLayoutY() + event.getY());
              }
            });

        /* Snap to nearest location */
        icon.setOnMouseReleased(
            event -> {
              this.pane.setGestureEnabled(true);

              final int newX = (int) icon.getLayoutX();
              final int newY = (int) icon.getLayoutY();

              String nodeID = node.getLocation();
              double shortestEuclideanDistance = 75;

              /* For each location on this floor, calculate the distance and then set the nodeID to that */
              for (var location : this.locationList) {
                if (location.getFloor().equals(this.floor)) {
                  double distance =
                      java.awt.geom.Point2D.distance(
                          newX, newY, location.getXCoord(), location.getYCoord());
                  if (distance < shortestEuclideanDistance) {
                    shortestEuclideanDistance = distance;
                    nodeID = location.getNodeID();
                  }
                }
              }
              node.setLocation(nodeID);
              try {
                this.medicalEquipmentImpl.modifyEntity(node.getEquipmentID(), "location", nodeID);
              } catch (SQLException e) {
                e.printStackTrace();
              }
              this.loadIcons();
            });

        /* Set action for editing medical equipment node */
        edit.setOnAction(
            editEvent -> {
              final PopOver editPop = new PopOver();
              editPop.setArrowSize(0);

              final FXMLLoader editForm =
                  new FXMLLoader(
                      App.class.getResource("views/mapeditor/EditMedicalEquipment.fxml"));

              try {
                editPop.setContentNode(editForm.load());
                final EditMedicalEquipment editController = editForm.getController();

                editController.setController(this);
                editController.setEquipmentID(node.getEquipmentID());
                editController.setLocation(node.getLocation());
                editController.setPopOver(editPop);

              } catch (IOException ex) {
                ex.printStackTrace();
              }

              final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
              editPop.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
            });
      }
    }
  }

  /** Display the MedicalEquipment node. */
  private void displayServiceRequest() {
    /* Filter out the list of non completed request */
    final List<ServiceRequest> serviceRequestArrayList =
        this.serviceRequestImpl.getAll().stream()
            .filter(sr -> !sr.getServiceStatus().equals("COMPLETE"))
            .collect(Collectors.toList());

    serviceRequestArrayList.forEach(
        request -> {
          /* Get the location of the request */
          final var nodeID = request.getLocation();

          /* Get the location object of the request */
          final var locationQueryResult =
              this.locationList.stream()
                  .filter(location -> location.getNodeID().equals(nodeID))
                  .collect(Collectors.toList());

          /* Check if the location exist */
          if (!locationQueryResult.isEmpty()) {
            final Location nodeLocation = locationQueryResult.get(0);

            /* If the object is on this floor */
            if (Objects.equals(nodeLocation.getFloor(), this.floor)) {

              /* Render the icon image for it*/
              final String path = String.format("images/%s.png", request.getServiceRequestType());

              final Image i =
                  new Image(Objects.requireNonNull(App.class.getResourceAsStream(path)));

              final ImageView icon = new ImageView(i);
              this.requestGroup.getChildren().add(icon);

              /* Set the node location statistics */
              icon.setFitWidth(25);
              icon.setFitHeight(25);
              icon.setLayoutX(nodeLocation.getXCoord() - 12.5);
              icon.setLayoutY(nodeLocation.getYCoord() - 12.5);
            }
          }
        });
  }

  @FXML
  private void toggleLocation() {
    this.locationGroup.setVisible(!this.locationGroup.isVisible());
  }

  @FXML
  private void toggleEquipment() {
    this.equipmentGroup.setVisible(!this.equipmentGroup.isVisible());
  }

  @FXML
  private void toggleRequest() {
    this.requestGroup.setVisible(!this.requestGroup.isVisible());
  }

  @FXML
  private void showSideMap() {
    this.sideViewPopUp.setVisible(!this.sideViewPopUp.isVisible());
  }
}
