package edu.wpi.cs3733.c22.teamD.controllers.data;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EquipmentDataController extends DataPageController {

  @FXML private JFXButton addEquipment;
  @FXML private JFXButton deleteEquipment;
  @FXML private JFXButton editEquipment;
  @FXML private PieChart overallChart;
  @FXML private PieChart bedChart;
  @FXML private TableView<MedicalEquipment> table;
  @FXML private TableColumn<MedicalEquipment, Integer> equipmentID;
  @FXML private TableColumn<MedicalEquipment, String> locationID;
  @FXML private TableColumn<MedicalEquipment, String> status;
  @FXML private TableColumn<MedicalEquipment, String> equipmentType;

  @FXML
  public void initialize() {
    final IDatabaseAPI<MedicalEquipment, Integer> instance = MedicalEquipmentImpl.getInstance();

    this.equipmentID.setCellValueFactory(new PropertyValueFactory<>("equipmentID"));
    this.locationID.setCellValueFactory(new PropertyValueFactory<>("location"));
    this.status.setCellValueFactory(new PropertyValueFactory<>("status"));
    this.equipmentType.setCellValueFactory(new PropertyValueFactory<>("equipmentType"));

    final ObservableList<MedicalEquipment> data = FXCollections.observableArrayList();
    data.addAll(instance.getAll());

    this.table.setItems(data);

    var cleanEquipment =
        instance.getAll().stream()
            .filter(e -> e.getStatus().equals("AVAILABLE"))
            .collect(Collectors.toList());

    var bedData =
        instance.getAll().stream()
            .filter(e -> e.getEquipmentType().equals("BED"))
            .collect(Collectors.toList());

    var cleanBed =
        bedData.stream()
            .filter(e -> e.getStatus().equals("AVAILABLE"))
            .collect(Collectors.toList());

    ObservableList<PieChart.Data> pieChartData =
        FXCollections.observableArrayList(
            new PieChart.Data("Available", cleanEquipment.size()),
            new PieChart.Data("Not Available", instance.getAll().size() - cleanEquipment.size()));

    ObservableList<PieChart.Data> bedChartData =
        FXCollections.observableArrayList(
            new PieChart.Data("Available", cleanBed.size()),
            new PieChart.Data("Not Available", bedData.size() - cleanBed.size()));

    this.overallChart.setData(pieChartData);
    this.bedChart.setData(bedChartData);

    this.overallChart.setLabelsVisible(false);
    this.bedChart.setLabelsVisible(false);

    this.addEquipment.setOnAction(e -> this.loadPop("views/data/AddEquipment.fxml"));
    this.editEquipment.setOnAction(e -> this.loadPop("views/data/EditEquipment.fxml"));
    this.deleteEquipment.setOnAction(e -> this.loadPop("views/data/DeleteEquipmentEntity.fxml"));
  }
}
