package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.LaundryRequest;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class LaundryRequestImpl implements IDatabaseAPI<LaundryRequest, Integer> {

  private static final LaundryRequestImpl instance = new LaundryRequestImpl();
  private final String TABLE_NAME = "LAUNDRY_SERVICE";
  @Getter private ArrayList<LaundryRequest> laundryList = null;
  private Statement statement = null;
  private Connection connection = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private LaundryRequestImpl() {
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.laundryList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/LaundryService.csv");
  }

  /**
   * Get current instance of LaundryO. Create it null.
   *
   * @return LaundryO object.
   */
  public static LaundryRequestImpl getInstance() {
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
              "CREATE TABLE LAUNDRY_SERVICE("
                  + "serviceID int primary key, "
                  + "location varchar(25),"
                  + "CONSTRAINT fk_laundrySRID FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE, "
                  + "CONSTRAINT fk_laundryLocation FOREIGN KEY (location) REFERENCES TOWER_LOCATION(NODEID) "
                  + "ON DELETE SET NULL)");

      CREATE.execute();
      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    this.clearTable();

    for (LaundryRequest request : this.laundryList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO LAUNDRY_SERVICE VALUES(?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setString(2, request.getLocation());

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
                  + "FROM LAUNDRY_SERVICE\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "ON LAUNDRY_SERVICE.SERVICEID = SERVICE_REQUEST.SERVICEID");

      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final LaundryRequest request =
            new LaundryRequest(
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

        this.laundryList.add(request);
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
    CSVUtil.generateCSV(filename, this.laundryList);
  }

  @Override
  public void restoreFromCSV(String filename) throws SQLException, IOException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_SERVICE");
    WIPE.execute();
    WIPE.close();
    this.laundryList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.laundryList.add(
          new LaundryRequest(
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

    for (LaundryRequest request : this.laundryList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO LAUNDRY_SERVICE VALUES(?, ?)");

      UPDATE_SANITATION.setInt(1, request.getServiceID());
      UPDATE_SANITATION.setString(2, request.getLocation());

      UPDATE_SANITATION.executeUpdate();
      UPDATE_SANITATION.close();
    }
    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.laundryList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  /**
   * Get all instances of a LaundryService
   *
   * @return ArrayList of LaundryServices
   */
  @Override
  public ArrayList<LaundryRequest> getAll() {
    return this.laundryList;
  }

  public void addNewEntity(LaundryRequest entity) throws SQLException {
    // this.serviceRequestImpl.insertEntity(entity);

    final PreparedStatement UPDATE_SANITATION =
        this.connection.prepareStatement("INSERT INTO LAUNDRY_SERVICE VALUES(?, ?)");

    UPDATE_SANITATION.setInt(1, entity.getServiceID());
    UPDATE_SANITATION.setString(2, entity.getLocation());
    UPDATE_SANITATION.executeUpdate();

    this.laundryList.add(entity);
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE LAUNDRY_SERVICE SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_SERVICE");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
