package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.InternalTransportationRequest;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class InternalTransportationRequestImpl
    implements IDatabaseAPI<InternalTransportationRequest, Integer> {

  private static final InternalTransportationRequestImpl instance =
      new InternalTransportationRequestImpl();
  private final String TABLE_NAME = "INTERNAL_TRANSPORTATION";
  @Getter private ArrayList<InternalTransportationRequest> internalTransportationList = null;
  private Statement statement = null;
  private Connection connection = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private InternalTransportationRequestImpl() {
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.internalTransportationList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/InternalTransportation.csv");
  }

  /**
   * Get current instance of InternalTransportationRequestImpl. Create it null.
   *
   * @return InternalTransportationRequestImpl object.
   */
  public static InternalTransportationRequestImpl getInstance() {
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
              "CREATE TABLE INTERNAL_TRANSPORTATION("
                  + "serviceID int primary key, "
                  + "patient_Patient varchar(25),"
                  + "end_Location varchar(25),"
                  + "pickupTime varchar(25),"
                  + "CONSTRAINT fk_srNum FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE, "
                  + "CONSTRAINT fk_end FOREIGN KEY (end_Location) REFERENCES TOWER_LOCATION(NODEID) "
                  + "ON DELETE CASCADE)");

      CREATE.execute();

      COUNT.close();
      CREATE.close();
      rs.close();

      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    this.clearTable();

    for (InternalTransportationRequest request : this.internalTransportationList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement(
              "INSERT INTO INTERNAL_TRANSPORTATION VALUES(?, ?, ?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setString(2, request.getPatient_Patient());
      UPDATE_SANITATION.setString(3, request.getEnd_Location());
      UPDATE_SANITATION.setString(4, request.getPickupTime());

      UPDATE_SANITATION.executeUpdate();
      UPDATE_SANITATION.close();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.createTable();
    this.populateFromList();
  }

  /*
   * Saves the current table into a file with a .csv format
   */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.internalTransportationList);
  }

  @Override
  public void restoreFromCSV(String filename) throws SQLException, FileNotFoundException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM INTERNAL_TRANSPORTATION");
    WIPE.execute();
    WIPE.close();
    this.internalTransportationList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.internalTransportationList.add(
          new InternalTransportationRequest(
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
              tempHeadingsList[10],
              tempHeadingsList[11],
              tempHeadingsList[12]));
    }

    for (InternalTransportationRequest request : this.internalTransportationList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement(
              "INSERT INTO INTERNAL_TRANSPORTATION VALUES(?, ?, ?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setString(2, request.getPatient_Patient());
      UPDATE_SANITATION.setString(3, request.getEnd_Location());
      UPDATE_SANITATION.setString(4, request.getPickupTime());

      UPDATE_SANITATION.executeUpdate();
      UPDATE_SANITATION.close();
    }
    buffer.close();
  }

  @Override
  public void addNewEntity(InternalTransportationRequest entity) throws SQLException {

    final PreparedStatement INSERT =
        this.connection.prepareStatement("INSERT INTO INTERNAL_TRANSPORTATION VALUES(?, ?, ?, ?)");

    INSERT.setInt(1, entity.getServiceID());
    INSERT.setString(2, entity.getPatient_Patient());
    INSERT.setString(3, entity.getEnd_Location());
    INSERT.setString(4, entity.getPickupTime());
    INSERT.executeUpdate();
    INSERT.close();

    this.internalTransportationList.add(entity);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.internalTransportationList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  @Override
  public void refresh() {
    try {
      final PreparedStatement JOIN =
          this.connection.prepareStatement(
              "SELECT *\n"
                  + "FROM INTERNAL_TRANSPORTATION\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "ON INTERNAL_TRANSPORTATION.SERVICEID = SERVICE_REQUEST.SERVICEID");

      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final InternalTransportationRequest request =
            new InternalTransportationRequest(
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
                selection.getString("PATIENT_PATIENT"),
                selection.getString("END_LOCATION"),
                selection.getString("PICKUPTIME"));

        this.internalTransportationList.add(request);
        this.serviceRequestImpl.addToMaster(request);
      }
      JOIN.close();
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get all instances of a MEServiceRequest
   *
   * @return ArrayList of MEServiceRequest
   */
  @Override
  public ArrayList<InternalTransportationRequest> getAll() {
    return this.internalTransportationList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE INTERNAL_TRANSPORTATION SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
    INSERT.close();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM INTERNAL_TRANSPORTATION");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
