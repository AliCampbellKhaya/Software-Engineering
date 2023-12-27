package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.ComputerRequest;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class ComputerRequestImpl implements IDatabaseAPI<ComputerRequest, Integer> {

  private static final ComputerRequestImpl instance = new ComputerRequestImpl();
  private final String TABLE_NAME = "COMPUTER_SERVICE";
  private Connection connection = null;
  private Statement statement = null;
  @Getter private ArrayList<ComputerRequest> computerServiceList = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private ComputerRequestImpl() {
    this.computerServiceList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();

    this.tableOnStartup("csv/ComputerRequest.csv");
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
              "CREATE TABLE COMPUTER_SERVICE("
                  + "serviceID int primary key, "
                  + "itID int, "
                  + "CONSTRAINT fk_computerServiceID FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE, "
                  + "CONSTRAINT fk_itID FOREIGN KEY (itID) REFERENCES IT_EQUIPMENT(itID) "
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

    for (ComputerRequest request : this.computerServiceList) {
      // this.serviceRequestImpl.insertEntity(request); /* this thing */

      final PreparedStatement UPDATE =
          this.connection.prepareStatement("INSERT INTO COMPUTER_SERVICE VALUES(?, ?)");

      UPDATE.setInt(1, request.getServiceID());
      UPDATE.setInt(2, request.getItID());
      UPDATE.executeUpdate();

      UPDATE.close();
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
                  + "FROM COMPUTER_SERVICE\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "    ON COMPUTER_SERVICE.SERVICEID = SERVICE_REQUEST.SERVICEID");

      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final ComputerRequest request =
            new ComputerRequest(
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
                selection.getInt("ITID"));

        this.computerServiceList.add(request);
        this.serviceRequestImpl.addToMaster(request);
      }
      JOIN.close();
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get current instance of ComputerServiceO. Create it null.
   *
   * @return ComputerServiceO object.
   */
  public static ComputerRequestImpl getInstance() {
    return instance;
  }

  /** Saves the current table into a file with a .csv format */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.computerServiceList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.computerServiceList.add(
          new ComputerRequest(
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

    for (ComputerRequest request : this.computerServiceList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE =
          this.connection.prepareStatement("INSERT INTO COMPUTER_SERVICE VALUES(?, ?)");

      UPDATE.setInt(1, request.getServiceID());
      UPDATE.setInt(2, request.getItID());
      UPDATE.executeUpdate();

      UPDATE.close();
    }
    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.computerServiceList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  @Override
  public void addNewEntity(ComputerRequest entity) throws SQLException {

    final PreparedStatement UPDATE =
        this.connection.prepareStatement("INSERT INTO COMPUTER_SERVICE VALUES(?, ?)");

    UPDATE.setInt(1, entity.getServiceID());
    UPDATE.setInt(2, entity.getItID());
    UPDATE.executeUpdate();

    this.computerServiceList.add(entity);
    UPDATE.close();
  }

  /**
   * Get all instances of an ComputerService
   *
   * @return ArrayList of ComputerServices
   */
  @Override
  public ArrayList<ComputerRequest> getAll() {
    return this.computerServiceList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE COMPUTER_SERVICE SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
    INSERT.close();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM COMPUTER_SERVICE");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
