package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.LaundryOrder;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class LaundryOrderImpl implements IDatabaseAPI<LaundryOrder, Integer> {

  private static final LaundryOrderImpl instance = new LaundryOrderImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<LaundryOrder> laundryOrderList = null;

  /** Private constructor that populates ArrayList */
  private LaundryOrderImpl() {
    this.laundryOrderList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/LaundryOrder.csv");
  }

  /**
   * Get current instance of LaundryOrderO. Create it null.
   *
   * @return LaundryOrderO object.
   */
  public static LaundryOrderImpl getInstance() {
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
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_ORDER");
    WIPE.execute();
    WIPE.close();
    this.laundryOrderList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.laundryOrderList.add(
          new LaundryOrder(
              Integer.parseInt(tempHeadingsList[0]), Integer.parseInt(tempHeadingsList[1])));
    }

    final String BASE_QUERY = "INSERT INTO LAUNDRY_ORDER VALUES";
    StringBuilder query = new StringBuilder(BASE_QUERY);

    for (LaundryOrder tmp : laundryOrderList) {
      query.append(String.format("(%d,%d)", tmp.getServiceID(), tmp.getLaundryID()));
      statement.execute(query.toString());
      query = new StringBuilder(BASE_QUERY);
    }
    buffer.close();
  }

  @Override
  public boolean createTable() throws SQLException {
    this.connection = Ddb.getConnection();

    final PreparedStatement prepared =
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'LAUNDRY_ORDER'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE LAUNDRY_ORDER("
                  + "serviceID int, "
                  + "laundryID int, "
                  + "CONSTRAINT pk_laundry_order PRIMARY KEY (serviceID,laundryID), "
                  + "CONSTRAINT fk_service_orderID FOREIGN KEY (serviceID) REFERENCES SERVICE_REQUEST(SERVICEID) "
                  + "ON DELETE CASCADE, "
                  + "CONSTRAINT fk_laundry_orderID FOREIGN KEY (laundryID) REFERENCES LAUNDRY_ITEM(LAUNDRYID) "
                  + "ON DELETE CASCADE "
                  + ")");
      CREATE.execute();
      CREATE.close();
      rs.close();
      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    this.clearTable();

    for (LaundryOrder entity : this.laundryOrderList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO LAUNDRY_ORDER VALUES(?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getServiceID());
      UPDATE_SANITATION.setInt(2, entity.getLaundryID());
      UPDATE_SANITATION.executeUpdate();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.createTable();
    this.populateFromList();
  }

  public void refresh() {
    try {
      this.laundryOrderList.clear();

      final String QUERY = "SELECT * FROM LAUNDRY_ORDER";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.laundryOrderList.add(
            new LaundryOrder(selection.getInt("serviceID"), selection.getInt("laundryID")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*
   * Saves the current table into a file with a .csv format
   */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.laundryOrderList);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.laundryOrderList.removeIf(request -> request.getServiceID() == primaryKey);
  }

  @Override
  public void addNewEntity(LaundryOrder entity) throws SQLException {
    final String query =
        String.format(
            "INSERT INTO LAUNDRY_ORDER VALUES(%d,%d)",
            entity.getServiceID(), entity.getLaundryID());
    statement.execute(query);
    this.laundryOrderList.add(entity);
  }

  /**
   * Get all instances of a LaundryOrder
   *
   * @return ArrayList of LaundryOrder
   */
  @Override
  public ArrayList<LaundryOrder> getAll() {
    return this.laundryOrderList;
  }

  @Override
  public void modifyEntity(Integer serviceID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE LAUNDRY_ORDER SET " + field + "=? WHERE SERVICEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, serviceID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_ORDER");
    WIPE.execute();
    WIPE.close();
  }
}
