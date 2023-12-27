package edu.wpi.cs3733.c22.teamD.database.impl;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.IDatabaseAPI;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import edu.wpi.cs3733.c22.teamD.database.login.PBKDF2Hasher;
import edu.wpi.cs3733.c22.teamD.util.CSVUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import lombok.Getter;

public class EmployeeImpl implements IDatabaseAPI<Employee, Integer> {

  private static final EmployeeImpl instance = new EmployeeImpl();
  private Statement statement = null;
  private Connection connection = null;
  @Getter private ArrayList<Employee> employeeList = null;
  private PBKDF2Hasher hasher;

  /** Private constructor that populates ArrayList */
  private EmployeeImpl() {
    this.employeeList = new ArrayList<>();
    this.statement = Ddb.getStatement();
    this.connection = Ddb.getConnection();

    this.hasher = new PBKDF2Hasher();

    this.tableOnStartup("csv/Employees.csv");
  }

  public void tableOnStartup(String filename) {
    try {
      this.statement = Ddb.getStatement();
      this.connection = Ddb.getConnection();

      final boolean tableCreated = this.createTable();

      if (tableCreated) {
        this.restoreFromCSV(filename);
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
        this.connection.prepareStatement(
            "select count(*) from sys.SYSTABLES where TABLENAME = 'EMPLOYEE'");

    final ResultSet rs = COUNT.executeQuery();
    rs.next();

    final int count = rs.getInt(1);
    if (count == 0 || rs.isClosed()) {

      final PreparedStatement CREATE =
          this.connection.prepareStatement(
              "CREATE TABLE EMPLOYEE("
                  + "employeeID int primary key, "
                  + "firstName varchar(25), "
                  + "lastName varchar(25), "
                  + "phoneNumber varchar(25), "
                  + "job varchar(25), "
                  + "token varchar(100)"
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

    for (Employee employee : this.employeeList) {
      final PreparedStatement INSERT =
          this.connection.prepareStatement("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?, ?)");
      INSERT.setInt(1, employee.getEmployeeID());
      INSERT.setString(2, employee.getFirstName());
      INSERT.setString(3, employee.getLastName());
      INSERT.setString(4, employee.getPhoneNumber());
      INSERT.setString(5, employee.getJob());
      INSERT.setString(6, employee.getToken());
      INSERT.executeUpdate();
      INSERT.close();
    }
  }

  @Override
  public void uploadNewConnection() throws SQLException {
    this.connection = Ddb.getConnection();
    this.createTable();
    this.populateFromList();
  }
  /**
   * Get current instance of EmployeeO. Create it null.
   *
   * @return EmployeeO object.
   */
  public static EmployeeImpl getInstance() {
    return instance;
  }

  @Override
  public void refresh() {
    try {
      final String QUERY = "SELECT * FROM EMPLOYEE";
      final ResultSet selection = statement.executeQuery(QUERY);

      while (selection.next()) {
        this.employeeList.add(
            new Employee(
                selection.getInt("employeeID"),
                selection.getString("FIRSTNAME"),
                selection.getString("LASTNAME"),
                selection.getString("PHONENUMBER"),
                selection.getString("JOB"),
                selection.getString("TOKEN")));
      }
      selection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void restoreFromCSV(String filename) throws IOException, SQLException {
    final Scanner buffer =
        new Scanner(Objects.requireNonNull(App.class.getResourceAsStream(filename)));

    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM EMPLOYEE");
    WIPE.execute();
    WIPE.close();
    this.employeeList.clear();

    if (buffer.hasNextLine()) buffer.nextLine();

    while (buffer.hasNextLine()) {
      String current = buffer.nextLine();
      String[] tempHeadingsList = current.split(",");
      this.employeeList.add(
          new Employee(
              Integer.parseInt(tempHeadingsList[0]),
              tempHeadingsList[1],
              tempHeadingsList[2],
              tempHeadingsList[3],
              tempHeadingsList[4],
              tempHeadingsList[5]));
    }

    for (Employee employee : this.employeeList) {
      final PreparedStatement INSERT =
          this.connection.prepareStatement("INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?, ?)");
      INSERT.setInt(1, employee.getEmployeeID());
      INSERT.setString(2, employee.getFirstName());
      INSERT.setString(3, employee.getLastName());
      INSERT.setString(4, employee.getPhoneNumber());
      INSERT.setString(5, employee.getJob());
      INSERT.setString(6, employee.getToken());
      INSERT.execute();
      INSERT.close();
    }
    buffer.close();
  }

  /** Saves the current table into a file with a .csv format */
  @Override
  public void storeToCSV(File filename) throws FileNotFoundException {
    CSVUtil.generateCSV(filename, this.employeeList);
  }

  @Override
  public void deleteTuple(Integer primaryKey) throws SQLException {
    final PreparedStatement DELETE =
        this.connection.prepareStatement("DELETE FROM EMPLOYEE WHERE EMPLOYEEID=?");
    DELETE.setInt(1, primaryKey);
    DELETE.executeUpdate();
    DELETE.close();

    this.employeeList.removeIf(employee -> employee.getEmployeeID() == primaryKey);
  }

  @Override
  public void addNewEntity(Employee entity) throws SQLException {}

  /**
   * Get all instances of an Employee
   *
   * @return ArrayList of Employees
   */
  @Override
  public ArrayList<Employee> getAll() {
    return this.employeeList;
  }

  @Override
  public void modifyEntity(Integer employeeID, String field, Object newValue) throws SQLException {
    final PreparedStatement INSERT =
        this.connection.prepareStatement("UPDATE EMPLOYEE SET " + field + "=? WHERE EMPLOYEEID=?");

    INSERT.setObject(1, newValue);
    INSERT.setInt(2, employeeID);
    INSERT.executeUpdate();
  }

  public String[][] getEmployeePkAndName() {
    ArrayList<Employee> tmp_list = getAll();
    int array_size = tmp_list.size();
    String[][] final_list = new String[array_size][2];
    for (int i = 0; i < tmp_list.size(); i++) {
      final_list[i][0] = String.valueOf(tmp_list.get(i).getEmployeeID());
      final_list[i][1] = tmp_list.get(i).getFirstName() + " " + tmp_list.get(i).getLastName();
    }
    return final_list;
  }

  @Override
  public void clearTable() throws SQLException {
    final PreparedStatement WIPE = this.connection.prepareStatement("DELETE FROM EMPLOYEE");
    WIPE.execute();
    WIPE.close();
  }

  public String addPassword(String password) {
    return hasher.hash(password.toCharArray());
  }
}
