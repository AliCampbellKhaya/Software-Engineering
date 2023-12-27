package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.*;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import lombok.Getter;

public class ServiceRequestImpl implements IDatabaseAPI<ServiceRequest, Integer> {

  private static final ServiceRequestImpl instance = new ServiceRequestImpl();
  private final String TABLE_NAME = "SERVICE_REQUEST";
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<ServiceRequest> masterList = null;

  private ServiceRequestImpl() {
    this.masterList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/DumbEmpty.csv");
  }

  public static ServiceRequestImpl getInstance() {
    return instance;
  }

  /**
   * Create the table for ServiceRequest
   *
   * @param filename Dummy filename
   */
  public void tableOnStartup(String filename) {
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    try {
      this.createTable();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean createTable() throws SQLException {
    this.connection = Ddb.getConnection();

    final PreparedStatement COUNT =
        this.connection.prepareStatement("select count(*) from sys.SYSTABLES where TABLENAME = ?");
    COUNT.setString(1, TABLE_NAME);

    final ResultSet rs = COUNT.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE SERVICE_REQUEST("
                  + "serviceID int primary key, "
                  + "serviceRequestType varchar(50),"
                  + "creator_employee int, "
                  + "assigned_employee int, "
                  + "description varchar(1000), "
                  + "serviceStatus varchar(25), "
                  + "urgency varchar(25), "
                  + "initiatedTime varchar(25), "
                  + "completedTime varchar(25), "
                  + "location varchar(25) DEFAULT NULL, "
                  + "CONSTRAINT fk_creator FOREIGN KEY (creator_employee) REFERENCES EMPLOYEE(employeeID) "
                  + "ON DELETE CASCADE,  "
                  + "CONSTRAINT fk_assigned FOREIGN KEY (assigned_employee) REFERENCES EMPLOYEE(employeeID)"
                  + "ON DELETE CASCADE,  "
                  + "CONSTRAINT fk_srLocation FOREIGN KEY (location) REFERENCES TOWER_LOCATION(nodeID) "
                  + "ON DELETE SET NULL, "
                  + "CONSTRAINT chk_urgency CHECK (urgency IN ('LOW', 'MED', 'HIGH')), "
                  + "CONSTRAINT chk_serviceStatus CHECK (serviceStatus IN ('INCOMPLETE', 'COMPLETE', 'IN_PROGRESS')),"
                  + "CONSTRAINT chk_serviceRequestType CHECK (serviceRequestType IN ("
                  + "'ComputerRequest', "
                  + "'InternalTransportationRequest', "
                  + "'LaundryRequest',"
                  + "'MedicalEquipmentRequest',"
                  + "'SecurityService',"
                  + "'FacilitiesMaintenanceRequest',"
                  + "'SanitationRequest',"
                  + "'MedicineDeliveryRequest'))"
                  + ")");
      CREATE.execute();
      CREATE.close();
      COUNT.close();
      rs.close();

      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    this.clearTable();

    for (ServiceRequest entity : this.masterList) {
      /* Array list of a lot of shit */
      final PreparedStatement INSERT =
          this.connection.prepareStatement(
              "INSERT INTO SERVICE_REQUEST VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

      INSERT.setInt(1, entity.getServiceID());
      INSERT.setString(2, entity.getServiceRequestType());
      INSERT.setInt(3, entity.getCreator_employee());
      INSERT.setInt(4, entity.getAssigned_employee());
      INSERT.setString(5, entity.getDescription());
      INSERT.setString(6, entity.getServiceStatus());
      INSERT.setString(7, entity.getUrgency());
      INSERT.setString(8, entity.getInitiatedTime());
      INSERT.setString(9, entity.getCompletedTime());
      INSERT.setString(10, entity.getLocation());

      INSERT.executeUpdate();
      INSERT.close();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.createTable();
    this.populateFromList();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    final PreparedStatement DELETE =
        this.connection.prepareStatement("DELETE FROM SERVICE_REQUEST WHERE SERVICEID=?");
    DELETE.setInt(1, primaryKey);

    final PreparedStatement SELECT =
        this.connection.prepareStatement(
            "SELECT SERVICEREQUESTTYPE FROM SERVICE_REQUEST WHERE SERVICEID=?");
    SELECT.setInt(1, primaryKey);

    final ResultSet selection = SELECT.executeQuery();

    if (selection.isClosed()) throw new SQLException("No matching ID");
    try {
      if (selection.next()) {
        final String type = selection.getString(1);

        switch (type) {
          case "FacilitiesMaintenanceRequest":
            FacilitiesMaintenanceRequestImpl.getInstance().deleteTuple(primaryKey);
            break;

          case "ComputerRequest":
            ComputerRequestImpl.getInstance().deleteTuple(primaryKey);
            break;

          case "InternalTransportationRequest":
            InternalTransportationRequestImpl.getInstance().deleteTuple(primaryKey);
            break;

          case "LaundryRequest":
            LaundryRequestImpl.getInstance().deleteTuple(primaryKey);
            break;

          case "MedicalEquipmentRequest":
            MedicalEquipmentImpl.getInstance().deleteTuple(primaryKey);
            break;

          case "SanitationRequest":
            SanitationRequestImpl.getInstance().deleteTuple(primaryKey);
            break;
          case "MedicineDeliveryRequest":
            MedicineDeliveryRequestImpl.getInstance().deleteTuple(primaryKey);
            break;
          case "SecurityService":
            SecurityServiceImpl.getInstance().deleteTuple(primaryKey);
            break;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    DELETE.executeUpdate();
    DELETE.close();
    selection.close();

    this.masterList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  public boolean inList(String field) {
    String[] fields =
        new String[] {
          "serviceID",
          "serviceRequestType",
          "creator_employee",
          "assigned_employee",
          "description",
          "servceStatus",
          "urgency",
          "initiatedTime",
          "completedTime"
        };
    for (String str : fields) {
      if (str.equals(field)) return true;
    }
    return false;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    if (inList(field)) {
      final PreparedStatement INSERT =
          this.connection.prepareStatement(
              "UPDATE SERVICE_REQUEST SET " + field + "=? WHERE SERVICEID=?");

      INSERT.setObject(1, newValue);
      INSERT.setInt(2, serviceID);
      INSERT.executeUpdate();

    } else {

      final PreparedStatement SELECT =
          this.connection.prepareStatement(
              "SELECT SERVICEREQUESTTYPE FROM SERVICE_REQUEST WHERE SERVICEID=?");
      SELECT.setInt(1, serviceID);
      final ResultSet selection = SELECT.executeQuery();
      final String type = selection.getString(1);

      switch (type) {
        case "FacilitiesMaintenanceRequest":
          FacilitiesMaintenanceRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;

        case "ComputerRequest":
          ComputerRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;

        case "InternalTransportationRequest":
          InternalTransportationRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;

        case "LaundryRequest":
          LaundryRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;

        case "MedicalEquipmentRequest":
          MedicalEquipmentImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;

        case "SanitationRequest":
          SanitationRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;
        case "MedicineDeliveryRequest":
          MedicineDeliveryRequestImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;
        case "SecurityService":
          SecurityServiceImpl.getInstance().modifyEntity(serviceID, field, newValue);
          break;
      }
    }
  }

  @Override
  public void addNewEntity(ServiceRequest entity) throws SQLException {
    entity.setServiceID(this.masterList.size() + 1);

    this.insertEntity(entity);

    if (entity instanceof SanitationRequest) {
      SanitationRequestImpl.getInstance().addNewEntity((SanitationRequest) entity);
    } else if (entity instanceof ComputerRequest) {
      ComputerRequestImpl.getInstance().addNewEntity((ComputerRequest) entity);
    } else if (entity instanceof FacilitiesMaintenanceRequest) {
      FacilitiesMaintenanceRequestImpl.getInstance()
          .addNewEntity((FacilitiesMaintenanceRequest) entity);
    } else if (entity instanceof LaundryRequest) {
      LaundryRequestImpl.getInstance().addNewEntity((LaundryRequest) entity);
    } else if (entity instanceof InternalTransportationRequest) {
      InternalTransportationRequestImpl.getInstance()
          .addNewEntity((InternalTransportationRequest) entity);
    } else if (entity instanceof MedicalEquipmentRequest) {
      MedicalEquipmentRequestImpl.getInstance().addNewEntity((MedicalEquipmentRequest) entity);
    } else if (entity instanceof MedicineDeliveryRequest) {
      MedicineDeliveryRequestImpl.getInstance().addNewEntity((MedicineDeliveryRequest) entity);
    } else if (entity instanceof FacilitiesMaintenanceRequest) {
      FacilitiesMaintenanceRequestImpl.getInstance()
          .addNewEntity((FacilitiesMaintenanceRequest) entity);
    } else if (entity instanceof SecurityService) {
      SecurityServiceImpl.getInstance().addNewEntity((SecurityService) entity);
    }
  }

  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.masterList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {}

  @Override
  public void refresh() {
    this.masterList.clear();
    try {
      this.tableOnStartup("csv/DumbEmpty.csv");
      final PreparedStatement WIPE =
          this.connection.prepareStatement("DELETE FROM SERVICE_REQUEST");
      WIPE.execute();
      WIPE.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ArrayList<ServiceRequest> getAll() {
    return this.masterList;
  }

  public void insertEntity(ServiceRequest entity) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "INSERT INTO SERVICE_REQUEST VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    INSERT.setInt(1, entity.getServiceID());
    INSERT.setString(2, entity.getServiceRequestType());
    INSERT.setInt(3, entity.getCreator_employee());
    INSERT.setInt(4, entity.getAssigned_employee());
    INSERT.setString(5, entity.getDescription());
    INSERT.setString(6, entity.getServiceStatus());
    INSERT.setString(7, entity.getUrgency());
    INSERT.setString(8, entity.getInitiatedTime());
    INSERT.setString(9, entity.getCompletedTime());
    INSERT.setString(10, entity.getLocation());

    INSERT.executeUpdate();
    INSERT.close();

    this.masterList.add(entity);
  }

  public void addToMaster(ServiceRequest request) {
    this.masterList.add(request);
  }

  public void addArrayList(ArrayList<ServiceRequest> specific) {
    this.masterList.addAll(specific);
  }

  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM SERVICE_REQUEST");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
