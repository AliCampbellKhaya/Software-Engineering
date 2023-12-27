package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

@Getter
public class LocationImpl implements IDatabaseAPI<Location, String> {

  private static final LocationImpl instance = new LocationImpl();
  private Statement statement = null;
  private Connection connection = null;
  private ArrayList<Location> locationList = null;

  /** Private constructor that populates ArrayList */
  private LocationImpl() {
    this.locationList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.tableOnStartup("csv/TowerLocationsD.csv");
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

  /**
   * Create a table if it does not exist
   *
   * @return True if the table is created
   * @throws SQLException If there is an error creating the table
   */
  @Override
  public boolean createTable() throws SQLException {
    /* Gets the count of the tables named TOWER_LOCATION */
    this.connection = Ddb.getConnection();

    final PreparedStatement prepared =
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'TOWER_LOCATION'");

    final ResultSet rs = prepared.executeQuery();
    rs.next();

    final int count = rs.getInt(1);

    /* If there is no table that exist */
    if (count == 0 || rs.isClosed()) {
      /* Creates the table */
      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE TOWER_LOCATION("
                  + "nodeID varchar(25) primary key, "
                  + "xcoord int, "
                  + "ycoord int, "
                  + "floor varchar(5), "
                  + "building varchar(25), "
                  + "nodeType varchar(25), "
                  + "longName varchar(100), "
                  + "shortName varchar(100)"
                  + ")");
      CREATE.execute();

      CREATE.close();
      prepared.close();
      rs.close();

      return true;
    }
    return false;
  }

  @Override
  public void populateFromList() throws SQLException {
    /* Clear the table that we are inserting into */
    this.clearTable();

    /* For each location in the ArrayList, insert it into the table */
    for (Location locale : this.locationList) {
      final PreparedStatement INSERT =
          this.connection.prepareStatement("INSERT INTO TOWER_LOCATION VALUES(?,?,?,?,?,?,?,?)");

      INSERT.setString(1, locale.getNodeID());
      INSERT.setInt(2, locale.getXCoord());
      INSERT.setInt(3, locale.getYCoord());
      INSERT.setString(4, locale.getFloor());
      INSERT.setString(5, locale.getBuilding());
      INSERT.setString(6, locale.getNodeType());
      INSERT.setString(7, locale.getLongName());
      INSERT.setString(8, locale.getShortName());

      INSERT.executeUpdate();
      INSERT.close();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.createTable();
    this.populateFromList();
  }

  /**
   * Get current instance of LocationO. Create it null.
   *
   * @return LocationO object.
   */
  public static LocationImpl getInstance() {
    return instance;
  }

  /** Reload the table into the array list */
  @Override
  public void refresh() {
    try {
      /* Clears active locations */
      this.locationList.clear();

      /* Get all rows in the table */
      final PreparedStatement QUERY =
          this.connection.prepareStatement("SELECT * FROM TOWER_LOCATION");
      final ResultSet selection = QUERY.executeQuery();

      /* Creates new objects and adds them into the ArrayList */
      while (selection.next()) {
        this.locationList.add(
            new Location(
                selection.getString("NODEID"),
                selection.getInt("XCOORD"),
                selection.getInt("YCOORD"),
                selection.getString("FLOOR"),
                selection.getString("BUILDING"),
                selection.getString("NODETYPE"),
                selection.getString("LONGNAME"),
                selection.getString("SHORTNAME")));
      }

      /* Close the SQL query connection */
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
    CSVUtil.generateCSV(filename, this.locationList);
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    this.clearTable();

    this.locationList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.locationList.add(
          new Location(
              tempHeadingsList[0],
              Integer.parseInt(tempHeadingsList[1]),
              Integer.parseInt(tempHeadingsList[2]),
              tempHeadingsList[3],
              tempHeadingsList[4],
              tempHeadingsList[5],
              tempHeadingsList[6],
              tempHeadingsList[7]));
    }

    this.populateFromList();
    buffer.close();
  }

  public void restoreFromCSV(File filename) throws IOException, SQLException {
    final Scanner buffer = new Scanner(Objects.requireNonNull(filename));

    this.clearTable();

    this.locationList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.locationList.add(
          new Location(
              tempHeadingsList[0],
              Integer.parseInt(tempHeadingsList[1]),
              Integer.parseInt(tempHeadingsList[2]),
              tempHeadingsList[3],
              tempHeadingsList[4],
              tempHeadingsList[5],
              tempHeadingsList[6],
              tempHeadingsList[7]));
    }

    this.populateFromList();
    buffer.close();
  }

  /**
   * Get all instances of a Location
   *
   * @return ArrayList of Locations
   */
  @Override
  public ArrayList<Location> getAll() {
    return this.locationList;
  }

  @Override
  public void deleteTuple(String nodeID) throws SQLException {
    final PreparedStatement QUERY =
        this.connection.prepareStatement("DELETE FROM TOWER_LOCATION WHERE NODEID=?");

    QUERY.setString(1, nodeID);
    QUERY.executeUpdate();
    QUERY.close();

    this.locationList.removeIf(location -> location.getNodeID().equals(nodeID));
  }

  @Override
  public void addNewEntity(Location entity) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "INSERT INTO TOWER_LOCATION VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

    INSERT.setString(1, entity.getNodeID());
    INSERT.setInt(2, entity.getXCoord());
    INSERT.setInt(3, entity.getYCoord());
    INSERT.setString(4, entity.getFloor());
    INSERT.setString(5, entity.getBuilding());
    INSERT.setString(6, entity.getNodeType());
    INSERT.setString(7, entity.getLongName());
    INSERT.setString(8, entity.getShortName());

    INSERT.executeUpdate();
    INSERT.close();

    this.locationList.add(entity);
  }

  @Override
  public void modifyEntity(String nodeID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement(
            "UPDATE TOWER_LOCATION SET " + field + "=? WHERE NODEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setString(2, nodeID);
    INSERT.executeUpdate();
  }

  public String[][] getLocationPkAndName() {
    ArrayList<Location> tmp_list = getAll();
    int array_size = tmp_list.size();
    String[][] final_list = new String[array_size][2];
    for (int i = 0; i < tmp_list.size(); i++) {
      final_list[i][0] = tmp_list.get(i).getNodeID();
      final_list[i][1] = tmp_list.get(i).getLongName();
    }
    return final_list;
  }

  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM TOWER_LOCATION");
    WIPE.executeUpdate();
    WIPE.close();
  }
}
