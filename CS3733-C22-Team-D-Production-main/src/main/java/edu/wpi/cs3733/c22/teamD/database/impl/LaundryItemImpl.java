package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.LaundryItem;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class LaundryItemImpl implements IDatabaseAPI<LaundryItem, Integer> {

  private static final LaundryItemImpl instance = new LaundryItemImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<LaundryItem> laundryItemList = null;

  /** Private constructor that populates ArrayList */
  private LaundryItemImpl() {
    this.laundryItemList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/LaundryItem.csv");
  }

  /**
   * Get current instance of LaundryItemO. Create it null.
   *
   * @return LaundryItemO object.
   */
  public static LaundryItemImpl getInstance() {
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

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_ITEM");
    WIPE.execute();
    WIPE.close();
    this.laundryItemList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.laundryItemList.add(
          new LaundryItem(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              Boolean.parseBoolean(tempHeadingsList[2])));
    }

    final String BASE_QUERY = "INSERT INTO LAUNDRY_ITEM VALUES";
    StringBuilder query = new StringBuilder(BASE_QUERY);

    for (LaundryItem tmp : laundryItemList) {
      query.append(
          String.format(
              "(%d,'%s',%s)", tmp.getLaundryID(), tmp.getLaundryType(), tmp.getInStock()));
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
            "select count(*) from sys.SYSTABLES where TABLENAME = 'LAUNDRY_ITEM'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE LAUNDRY_ITEM("
                  + "laundryID int primary key, "
                  + "laundryType varchar(25), "
                  + "inStock boolean)");
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

    for (LaundryItem entity : this.laundryItemList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO LAUNDRY_ITEM VALUES(?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getLaundryID());
      UPDATE_SANITATION.setString(2, entity.getLaundryType());
      UPDATE_SANITATION.setBoolean(3, entity.getInStock());
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
      this.laundryItemList.clear();

      final String QUERY = "SELECT * FROM LAUNDRY_ITEM";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.laundryItemList.add(
            new LaundryItem(
                selection.getInt("laundryID"),
                selection.getString("laundryType"),
                selection.getBoolean("inStock")));
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
    CSVUtil.generateCSV(filename, this.laundryItemList);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.laundryItemList.removeIf(request -> request.getLaundryID() == primaryKey);
  }

  @Override
  public void addNewEntity(LaundryItem entity) throws SQLException {
    final String query =
        String.format(
            "INSERT INTO MEDICAL_EQUIPMENT_REQUEST VALUES(%d,'%s',%s)",
            entity.getLaundryID(), entity.getLaundryType(), entity.getInStock());
    statement.execute(query);
    this.laundryItemList.add(entity);
  }

  /**
   * Get all instances of a LaundryItem
   *
   * @return ArrayList of LaundryItem
   */
  @Override
  public ArrayList<LaundryItem> getAll() {
    return this.laundryItemList;
  }

  @Override
  public void modifyEntity(Integer laundryID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE LAUNDRY_ITEM SET " + field + "=? WHERE LAUNDRYID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, laundryID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM LAUNDRY_ITEM");
    WIPE.execute();
    WIPE.close();
  }
}
