package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipment;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class MedicalEquipmentImpl implements IDatabaseAPI<MedicalEquipment, Integer> {

  private static final MedicalEquipmentImpl instance = new MedicalEquipmentImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<MedicalEquipment> medicalEquipmentList = null;

  /** Private constructor that populates ArrayList */
  private MedicalEquipmentImpl() {
    this.medicalEquipmentList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/MedicalEquipment.csv");
  }

  /**
   * Get current instance of MedicalEquipmentO. Create it null.
   *
   * @return MedicalEquipmentO object.
   */
  public static MedicalEquipmentImpl getInstance() {
    return instance;
  }

  @Override
  public void tableOnStartup(String filename) {
    try {
      this.statement = Ddb.getStatement();
      this.connection = Ddb.getConnection();

      /* True if the table was created */
      final boolean tableCreated = this.createTable();

      /* If the table was created, read data from the .csv into the tables */
      if (tableCreated) {
        this.restoreFromCSV(filename);
      } else {
        /* Reload the table into the ArrayList because the data already exist in the DB */
        this.refresh();
      }
    } catch (IOException | SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean createTable() throws SQLException {
    this.connection = Ddb.getConnection();

    final PreparedStatement prepared =
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'MEDICAL_EQUIPMENT'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE MEDICAL_EQUIPMENT("
                  + "equipmentID int primary key, "
                  + "location varchar(25), "
                  + "status varchar(15), "
                  + "equipmentType varchar(100), "
                  + "CONSTRAINT chk_status CHECK (status IN ('AVAILABLE', 'IN_USE')), "
                  + "CONSTRAINT chk_equipmentType CHECK (equipmentType IN ('BED', 'XRAY', 'INFUSION_PUMP', 'RECLINER')), "
                  + "CONSTRAINT fk_location FOREIGN KEY (location) REFERENCES TOWER_LOCATION(NODEID) "
                  + "ON DELETE SET NULL)");

      CREATE.execute();
      CREATE.close();
      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    this.clearTable();

    for (MedicalEquipment entity : this.medicalEquipmentList) {
      final PreparedStatement UPDATE_SANITATION =
          this.connection.prepareStatement("INSERT INTO MEDICAL_EQUIPMENT VALUES(?, ?, ?, ?)");

      UPDATE_SANITATION.setInt(1, entity.getEquipmentID());
      UPDATE_SANITATION.setString(2, entity.getLocation());
      UPDATE_SANITATION.setString(3, entity.getStatus());
      UPDATE_SANITATION.setString(4, entity.getEquipmentType());
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
      this.medicalEquipmentList.clear();

      final String QUERY = "SELECT * FROM MEDICAL_EQUIPMENT";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.medicalEquipmentList.add(
            new MedicalEquipment(
                selection.getInt("equipmentID"),
                selection.getString("location"),
                selection.getString("status"),
                selection.getString("equipmentType")));
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
    CSVUtil.generateCSV(filename, this.medicalEquipmentList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM MEDICAL_EQUIPMENT");
    WIPE.execute();
    WIPE.close();

    this.medicalEquipmentList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.medicalEquipmentList.add(
          new MedicalEquipment(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              tempHeadingsList[2],
              tempHeadingsList[3]));
    }

    this.populateFromList();

    buffer.close();
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    final String QUERY =
        String.format("DELETE FROM MEDICAL_EQUIPMENT WHERE EQUIPMENTID=%s", primaryKey);
    statement.execute(QUERY);
    for (int i = 0; i < medicalEquipmentList.size(); i++) {
      if (medicalEquipmentList.get(i).getEquipmentID() == primaryKey) {
        medicalEquipmentList.remove(i);
      }
    }
  }

  @Override
  public void addNewEntity(MedicalEquipment entity) throws SQLException {
    final String query =
        String.format(
            "INSERT INTO MEDICAL_EQUIPMENT VALUES(%d,'%s','%s','%s')",
            entity.getEquipmentID(),
            entity.getLocation(),
            entity.getStatus(),
            entity.getEquipmentType());
    statement.execute(query);
    this.medicalEquipmentList.add(entity);
  }

  /**
   * Get all instances of a MedicalEquipment
   *
   * @return ArrayList of MedicalEquipment
   */
  @Override
  public ArrayList<MedicalEquipment> getAll() {
    return this.medicalEquipmentList;
  }

  @Override
  public void modifyEntity(Integer equipmentID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE MEDICAL_EQUIPMENT SET " + field + "=? WHERE EQUIPMENTID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, equipmentID);
    INSERT.executeUpdate();
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE =
        this.connection.prepareStatement("DELETE FROM MEDICAL_EQUIPMENT");
    WIPE.execute();
    WIPE.close();
  }
}
