package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipmentRequest;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class MedicalEquipmentRequestImpl implements IDatabaseAPI<MedicalEquipmentRequest, Integer> {

  private static final MedicalEquipmentRequestImpl instance = new MedicalEquipmentRequestImpl();
  @Getter private ArrayList<MedicalEquipmentRequest> equipmentRequestList = null;
  private Statement statement = null;
  private final String TABLE_NAME = "MEDICAL_EQUIPMENT_REQUEST";
  private Connection connection = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private MedicalEquipmentRequestImpl() {
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.equipmentRequestList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/MedicalEquipmentRequest.csv");
  }

  /**
   * Get current instance of MEServiceRequest. Create it null.
   *
   * @return MEServiceRequest object.
   */
  public static MedicalEquipmentRequestImpl getInstance() {
    return instance;
  }

  public void tableOnStartup(String filename) {
    try {
      this.statement = Ddb.getStatement();
      this.connection = Ddb.getConnection();

      final boolean tableCreated = this.createTable();

      if (tableCreated) {
        restoreFromCSV(filename);
      } else {
        this.refresh();
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean createTable() throws SQLException {
    this.connection = Ddb.getConnection();

    final PreparedStatement COUNT =
        this.connection.prepareStatement("select count(*) from sys.SYSTABLES where TABLENAME = ?");
    COUNT.setString(1, this.TABLE_NAME);

    final ResultSet rs = COUNT.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE MEDICAL_EQUIPMENT_REQUEST("
                  + "serviceID int primary key, "
                  + "requested_medicalEquipment int,"
                  + "CONSTRAINT fk_serviceID FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE, "
                  + "CONSTRAINT fk_requested FOREIGN KEY (requested_medicalEquipment) REFERENCES MEDICAL_EQUIPMENT(EQUIPMENTID) "
                  + "ON DELETE SET NULL"
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

    for (MedicalEquipmentRequest request : this.equipmentRequestList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO MEDICAL_EQUIPMENT_REQUEST VALUES(?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setInt(2, request.getRequested_medicalEquipment());
      UPDATE_SANITATION.executeUpdate();

      UPDATE_SANITATION.close();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.createTable();
    this.populateFromList();
  }

  @Override
  public void refresh() {
    try {
      final PreparedStatement JOIN =
          this.connection.prepareStatement(
              "SELECT *\n"
                  + "FROM MEDICAL_EQUIPMENT_REQUEST\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "    ON MEDICAL_EQUIPMENT_REQUEST.SERVICEID = SERVICE_REQUEST.SERVICEID");

      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final MedicalEquipmentRequest request =
            new MedicalEquipmentRequest(
                selection.getInt("SERVICEID"),
                selection.getString("SERVICEREQUESTTYPE"),
                selection.getInt("CREATOR_EMPLOYEE"),
                selection.getInt("ASSIGNED_EMPLOYEE"),
                selection.getString("DESCRIPTION"),
                selection.getString("SERVICESTATUS"),
                selection.getString("URGENCY"),
                selection.getString("INITIATEDTIME"),
                selection.getString("COMPLETEDTIME"),
                selection.getString("LOCATION"),
                selection.getInt("REQUESTED_MEDICALEQUIPMENT"));

        this.equipmentRequestList.add(request);
        this.serviceRequestImpl.addToMaster(request);
      }
      JOIN.close();
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.equipmentRequestList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM MEDICAL_EQUIPMENT_REQUEST");
    WIPE.execute();
    WIPE.close();
    this.equipmentRequestList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.equipmentRequestList.add(
          new MedicalEquipmentRequest(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              Integer.parseInt(tempHeadingsList[2]),
              Integer.parseInt(tempHeadingsList[3]),
              tempHeadingsList[4],
              tempHeadingsList[5],
              tempHeadingsList[6],
              tempHeadingsList[7],
              tempHeadingsList[8],
              tempHeadingsList[9],
              Integer.parseInt(tempHeadingsList[10])));
    }

    for (MedicalEquipmentRequest request : this.equipmentRequestList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO MEDICAL_EQUIPMENT_REQUEST VALUES(?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setInt(2, request.getRequested_medicalEquipment());
      UPDATE_SANITATION.executeUpdate();

      UPDATE_SANITATION.close();
    }
    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    // todo delete from the parent table

    //    final PreparedStatement DELETE =
    //        this.connection.prepareStatement("DELETE FROM COMPUTER_SERVICE WHERE SERVICEID=?");
    //
    //    DELETE.setInt(1, primaryKey);
    //    DELETE.executeUpdate();

    this.equipmentRequestList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  public void addNewEntity(MedicalEquipmentRequest entity) throws SQLException {
    final PreparedStatement UPDATE_SANITATION =
        this.connection.prepareStatement("INSERT INTO MEDICAL_EQUIPMENT_REQUEST VALUES(?, ?)");

    UPDATE_SANITATION.setInt(1, entity.getServiceID());
    UPDATE_SANITATION.setInt(2, entity.getRequested_medicalEquipment());
    UPDATE_SANITATION.executeUpdate();

    this.equipmentRequestList.add(entity);
    // this.serviceRequestImpl.addToMaster(entity);
  }

  /**
   * Get all instances of a MEServiceRequest
   *
   * @return ArrayList of MEServiceRequest
   */
  @Override
  public ArrayList<MedicalEquipmentRequest> getAll() {
    return this.equipmentRequestList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE MEDICAL_EQUIPMENT_REQUEST SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM MEDICAL_EQUIPMENT_REQUEST");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
