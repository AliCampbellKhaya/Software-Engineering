package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.ITEquipment;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class ITEquipmentImpl implements IDatabaseAPI<ITEquipment, Integer> {

  private static final ITEquipmentImpl instance = new ITEquipmentImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<ITEquipment> itEquipmentList = null;

  /** Private constructor that populates ArrayList */
  private ITEquipmentImpl() {
    this.itEquipmentList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/ITEquipment.csv");
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

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM IT_EQUIPMENT");
    WIPE.execute();
    WIPE.close();
    this.itEquipmentList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.itEquipmentList.add(
          new ITEquipment(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              tempHeadingsList[2],
              tempHeadingsList[3]));
    }

    for (ITEquipment entity : this.itEquipmentList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO IT_EQUIPMENT VALUES(?, ?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getItID());
      UPDATE_SANITATION.setString(2, entity.getModelNumber());
      UPDATE_SANITATION.setString(3, entity.getSerialNumber());
      UPDATE_SANITATION.setString(4, entity.getProductName());
      UPDATE_SANITATION.executeUpdate();
    }
    buffer.close();
  }

  @Override
  public boolean createTable() throws SQLException {
    this.connection = Ddb.getConnection();

    final PreparedStatement prepared =
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'IT_EQUIPMENT'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE IT_EQUIPMENT("
                  + "itID int primary key, "
                  + "modelNumber varchar(25), "
                  + "serialNumber varchar(25), "
                  + "productName varchar(25))");
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

    for (ITEquipment entity : this.itEquipmentList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO IT_EQUIPMENT VALUES(?, ?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getItID());
      UPDATE_SANITATION.setString(2, entity.getModelNumber());
      UPDATE_SANITATION.setString(3, entity.getSerialNumber());
      UPDATE_SANITATION.setString(4, entity.getProductName());
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
      final String QUERY = "SELECT * FROM IT_EQUIPMENT";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.itEquipmentList.add(
            new ITEquipment(
                selection.getInt("ITID"),
                selection.getString("MODELNUMBER"),
                selection.getString("SERIALNUMBER"),
                selection.getString("PRODUCTNAME")));
      }
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get current instance of ITEquipmentO. Create it null.
   *
   * @return ITEquipmentO object.
   */
  public static ITEquipmentImpl getInstance() {
    return instance;
  }

  /*
   * Saves the current table into a file with a .csv format
   */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.itEquipmentList);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    this.itEquipmentList.removeIf(request -> request.getItID() == primaryKey);
  }

  @Override
  public void addNewEntity(ITEquipment entity) throws SQLException {
    final PreparedStatement UPDATE_SANITATION =
        this.connection.prepareStatement("INSERT INTO IT_EQUIPMENT VALUES(?, ?, ?, ?)");

    UPDATE_SANITATION.setInt(1, entity.getItID());
    UPDATE_SANITATION.setString(2, entity.getModelNumber());
    UPDATE_SANITATION.setString(3, entity.getSerialNumber());
    UPDATE_SANITATION.setString(4, entity.getProductName());
    UPDATE_SANITATION.executeUpdate();

    this.itEquipmentList.add(entity);
  }

  /**
   * Get all instances of a ITEquipment
   *
   * @return ArrayList of ITEquipment
   */
  @Override
  public ArrayList<ITEquipment> getAll() {
    return this.itEquipmentList;
  }

  @Override
  public void modifyEntity(Integer itID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement("UPDATE IT_EQUIPMENT SET " + field + "=? WHERE ITID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, itID);
    INSERT.executeUpdate();
  }

  public String[][] getLocationPkAndName() {
    ArrayList<ITEquipment> tmp_list = getAll();
    int array_size = tmp_list.size();
    String[][] final_list = new String[array_size][2];
    for (int i = 0; i < tmp_list.size(); i++) {
      final_list[i][0] = String.valueOf(tmp_list.get(i).getItID());
      final_list[i][1] = tmp_list.get(i).getProductName();
    }
    return final_list;
  }

  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM IT_EQUIPMENT");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
