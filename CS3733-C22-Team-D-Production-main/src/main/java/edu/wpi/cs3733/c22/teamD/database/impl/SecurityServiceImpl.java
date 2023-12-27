package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.SecurityService;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class SecurityServiceImpl implements IDatabaseAPI<SecurityService, Integer> {

  private static final SecurityServiceImpl instance = new SecurityServiceImpl();
  @Getter private ArrayList<SecurityService> securityServiceList = null;
  private Statement statement = null;
  private final String TABLE_NAME = "SECURITY_SERVICE";
  private Connection connection = null;
  private ServiceRequestImpl serviceRequestImpl = null;

  /** Private constructor that populates ArrayList */
  private SecurityServiceImpl() {
    this.serviceRequestImpl = ServiceRequestImpl.getInstance();
    this.securityServiceList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/SecurityService.csv");
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
              "CREATE TABLE SECURITY_SERVICE("
                  + "serviceID int primary key, "
                  + "CONSTRAINT fk_serviceID4 FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE"
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

    for (SecurityService request : this.securityServiceList) {
      final PreparedStatement UPDATE_SECURITY_LIST =
          this.connection.prepareStatement("INSERT INTO SECURITY_SERVICE VALUES(?)");

      UPDATE_SECURITY_LIST.setInt(1, request.getServiceID());
      UPDATE_SECURITY_LIST.executeUpdate();

      UPDATE_SECURITY_LIST.close();
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
                  + "FROM SECURITY_SERVICE\n"
                  + "INNER JOIN SERVICE_REQUEST\n"
                  + "    ON SECURITY_SERVICE.SERVICEID = SERVICE_REQUEST.SERVICEID");
      final ResultSet selection = JOIN.executeQuery();

      while (selection.next()) {
        final SecurityService request =
            new SecurityService(
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

        this.securityServiceList.add(request);
        this.serviceRequestImpl.addToMaster(request);
      }
      JOIN.close();
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get current instance of SecurityService. Create it null.
   *
   * @return MEServiceRequest object.
   */
  public static SecurityServiceImpl getInstance() {
    return instance;
  }

  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.securityServiceList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));
    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.securityServiceList.add(
          new SecurityService(
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

    for (SecurityService request : this.securityServiceList) {
      this.serviceRequestImpl.insertEntity(request);

      final PreparedStatement UPDATE_SECURITY_LIST =
          this.connection.prepareStatement("INSERT INTO SECURITY_SERVICE VALUES(?)");

      UPDATE_SECURITY_LIST.setInt(1, request.getServiceID());
      UPDATE_SECURITY_LIST.executeUpdate();

      UPDATE_SECURITY_LIST.close();
    }
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    // todo delete from the parent table

    //    final PreparedStatement DELETE =
    //        this.connection.prepareStatement("DELETE FROM COMPUTER_SERVICE WHERE SERVICEID=?");
    //
    //    DELETE.setInt(1, primaryKey);
    //    DELETE.executeUpdate();
    //    DELETE.executeUpdate();

    this.securityServiceList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  public void addNewEntity(SecurityService entity) throws SQLException {
    final PreparedStatement UPDATE_SECURITY_LIST =
        this.connection.prepareStatement("INSERT INTO SECURITY_SERVICE VALUES(?)");

    UPDATE_SECURITY_LIST.setInt(1, entity.getServiceID());

    UPDATE_SECURITY_LIST.executeUpdate();

    this.securityServiceList.add(entity);
    // this.serviceRequestImpl.addToMaster(entity);
  }

  @Override
  public ArrayList<SecurityService> getAll() {
    return this.securityServiceList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE SECURITY_SERVICE SET " + field + "=? WHERE SERVICEID=?");
    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM SECURITY_SERVICE");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
