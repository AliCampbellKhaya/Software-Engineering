package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Medicine;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class MedicineImpl implements IDatabaseAPI<Medicine, Integer> {

  private static final MedicineImpl instance = new MedicineImpl();
  private Statement statement = null;
  private Connection connection = null;
  private final String TABLE_NAME = "MEDICINE";
  private ArrayList<Medicine> medicineList = null;

  /** Private constructor that populates ArrayList */
  private MedicineImpl() {
    this.medicineList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/Medicine.csv");
  }

  /**
   * Get current instance of MedicineO. Create it null.
   *
   * @return MedicineO object.
   */
  public static MedicineImpl getInstance() {
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
              "CREATE TABLE MEDICINE("
                  + "medicineID int primary key, "
                  + "name varchar(25), "
                  + "quantity int"
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

    for (Medicine entity : this.medicineList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO MEDICINE VALUES(?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getMedicineID());
      UPDATE_SANITATION.setString(2, entity.getName());
      UPDATE_SANITATION.setInt(3, entity.getQuantity());
      UPDATE_SANITATION.executeUpdate();
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
      final String QUERY = "SELECT * FROM MEDICINE";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.medicineList.add(
            new Medicine(
                selection.getInt("MEDICINEID"),
                selection.getString("NAME"),
                selection.getInt("QUANTITY")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Saves the current table into a file with a .csv format */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.medicineList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM MEDICINE");
    WIPE.execute();
    WIPE.close();
    this.medicineList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.medicineList.add(
          new Medicine(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              Integer.parseInt(tempHeadingsList[2])));
    }

    this.populateFromList();

    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    final String QUERY = String.format("DELETE FROM MEDICINE WHERE medicineID=%d", primaryKey);
    statement.execute(QUERY);
    for (int i = 0; i < medicineList.size(); i++) {
      if (this.medicineList.get(i).getMedicineID() == primaryKey) {
        this.medicineList.remove(i);
      }
    }
  }

  @Override
  public void addNewEntity(Medicine entity) throws SQLException {}

  /**
   * Get all instances of an Medicine.csv
   *
   * @return ArrayList of Medicines
   */
  @Override
  public ArrayList<Medicine> getAll() {
    return this.medicineList;
  }

  @Override
  public void modifyEntity(Integer medicineID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement("UPDATE MEDICINE SET " + field + "=? WHERE MEDICINEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, medicineID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM MEDICINE");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
