package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Patient;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class PatientImpl implements IDatabaseAPI<Patient, Integer> {

  private static final PatientImpl instance = new PatientImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<Patient> patientList = null;

  /** Private constructor that populates ArrayList */
  private PatientImpl() {
    this.patientList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/Patient.csv");
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

    final PreparedStatement prepared =
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'PATIENT'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE PATIENT("
                  + "patientID int primary key, "
                  + "firstName varchar(25), "
                  + "lastName varchar(25)"
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

    for (Patient entity : this.patientList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO PATIENT VALUES(?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getPatientID());
      UPDATE_SANITATION.setString(2, entity.getFirstName());
      UPDATE_SANITATION.setString(3, entity.getLastName());
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
      final String QUERY = "SELECT * FROM PATIENT";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.patientList.add(
            new Patient(
                selection.getInt("patientID"),
                selection.getString("FIRSTNAME"),
                selection.getString("LASTNAME")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get current instance of PatientO. Create it null.
   *
   * @return PatientO object.
   */
  public static PatientImpl getInstance() {
    return instance;
  }

  /** Saves the current table into a file with a .csv format */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.patientList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM PATIENT");
    WIPE.execute();
    WIPE.close();
    this.patientList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.patientList.add(
          new Patient(
              Integer.parseInt(tempHeadingsList[0]), tempHeadingsList[1], tempHeadingsList[2]));
    }

    final String BASE_QUERY = "INSERT INTO PATIENT VALUES";
    StringBuilder query = new StringBuilder(BASE_QUERY);

    this.populateFromList();

    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    final String QUERY = String.format("DELETE FROM PATIENT WHERE patientID=%d", primaryKey);
    statement.execute(QUERY);
    for (int i = 0; i < patientList.size(); i++) {
      if (this.patientList.get(i).getPatientID() == primaryKey) {
        this.patientList.remove(i);
      }
    }
  }

  @Override
  public void addNewEntity(Patient entity) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement("INSERT INTO PATIENT VALUES(?, ?, ?)");

    INSERT.setInt(1, entity.getPatientID());
    INSERT.setString(2, entity.getFirstName());
    INSERT.setString(3, entity.getLastName());

    INSERT.executeUpdate();
    INSERT.close();

    this.patientList.add(entity);
  }

  /**
   * Get all instances of an Patient
   *
   * @return ArrayList of Patients
   */
  @Override
  public ArrayList<Patient> getAll() {
    return this.patientList;
  }

  @Override
  public void modifyEntity(Integer patientID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement("UPDATE PATIENT SET " + field + "=? WHERE PATIENTID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, patientID);
    INSERT.executeUpdate();
  }

  public String[][] getLocationPkAndName() {
    ArrayList<Patient> tmp_list = getAll();
    int array_size = tmp_list.size();
    String[][] final_list = new String[array_size][2];
    for (int i = 0; i < tmp_list.size(); i++) {
      final_list[i][0] = String.valueOf(tmp_list.get(i).getPatientID());
      final_list[i][1] = tmp_list.get(i).getFirstName() + " " + tmp_list.get(i).getLastName();
    }
    return final_list;
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM PATIENT");
    WIPE.execute();
    WIPE.close();
  }
}
