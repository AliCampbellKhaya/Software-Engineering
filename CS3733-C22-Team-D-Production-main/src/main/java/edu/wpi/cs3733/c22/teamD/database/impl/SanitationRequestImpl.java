package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.SanitationRequest;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class SanitationRequestImpl implements IDatabaseAPI<SanitationRequest, Integer> {

  private static final SanitationRequestImpl instance = new SanitationRequestImpl();
  private final String TABLE_NAME = "SANITATION_SERVICE";
  @Getter private ArrayList<SanitationRequest> sanitationRequestList = null;
  private Statement statement = null;
  private Connection connection = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private SanitationRequestImpl() {
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.sanitationRequestList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/SanitationService.csv");
  }

  /**
   * Get current instance of MEServiceRequest. Create it null.
   *
   * @return MEServiceRequest object.
   */
  public static SanitationRequestImpl getInstance() {
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
              "CREATE TABLE SANITATION_SERVICE("
                  + "serviceID int primary key, "
                  + "CONSTRAINT fk_srID FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE)");

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

    for (SanitationRequest request : this.sanitationRequestList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO SANITATION_SERVICE VALUES(?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
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
                  + "FROM SANITATION_SERVICE\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "ON SANITATION_SERVICE.SERVICEID = SERVICE_REQUEST.SERVICEID");

      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final SanitationRequest request =
            new SanitationRequest(
                selection.getInt("SERVICEID"),
                selection.getString("SERVICEREQUESTTYPE"),
                selection.getInt("CREATOR_EMPLOYEE"),
                selection.getInt("ASSIGNED_EMPLOYEE"),
                selection.getString("DESCRIPTION"),
                selection.getString("SERVICESTATUS"),
                selection.getString("URGENCY"),
                selection.getString("INITIATEDTIME"),
                selection.getString("COMPLETEDTIME"),
                selection.getString("LOCATION"));
        this.sanitationRequestList.add(request);
        System.out.println(this.sanitationRequestList.size());
        this.serviceRequestImpl.addToMaster(request);
      }
      JOIN.close();
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*
   * Saves the current table into a file with a .csv format
   */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.sanitationRequestList);
  }

  @Override
  public void restoreFromCSV(String filename) throws SQLException, FileNotFoundException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM SANITATION_SERVICE");
    WIPE.execute();
    WIPE.close();
    this.sanitationRequestList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.sanitationRequestList.add(
          new SanitationRequest(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              Integer.parseInt(tempHeadingsList[2]),
              Integer.parseInt(tempHeadingsList[3]),
              tempHeadingsList[4],
              tempHeadingsList[5],
              tempHeadingsList[6],
              tempHeadingsList[7],
              tempHeadingsList[8],
              tempHeadingsList[9]));
    }

    for (SanitationRequest request : this.sanitationRequestList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO SANITATION_SERVICE VALUES(?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());

      UPDATE_SANITATION.executeUpdate();

      UPDATE_SANITATION.close();
    }
    buffer.close();
  }

  @Override
  public void addNewEntity(SanitationRequest entity) throws SQLException {
    final PreparedStatement UPDATE_SANITATION =
        this.connection.prepareStatement("INSERT INTO SANITATION_SERVICE VALUES(?)");

    UPDATE_SANITATION.setInt(1, entity.getServiceID());
    UPDATE_SANITATION.executeUpdate();

    this.sanitationRequestList.add(entity);
    //  this.serviceRequestImpl.addToMaster(entity);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    // todo delete from the parent table

    //    final PreparedStatement DELETE =
    //        this.connection.prepareStatement("DELETE FROM COMPUTER_SERVICE WHERE SERVICEID=?");
    //
    //    DELETE.setInt(1, primaryKey);
    //    DELETE.executeUpdate();

    this.sanitationRequestList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  public void deleteObject(Integer primaryKey) {
    this.sanitationRequestList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  /**
   * Get all instances of a MEServiceRequest
   *
   * @return ArrayList of MEServiceRequest
   */
  @Override
  public ArrayList<SanitationRequest> getAll() {
    return this.sanitationRequestList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE SANITATION_SERVICE SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM SANITATION_SERVICE");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
