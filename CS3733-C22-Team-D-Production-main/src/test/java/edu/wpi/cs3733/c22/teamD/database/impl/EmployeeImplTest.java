package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.Employee;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class EmployeeImplTest {

  @Test
  void getInstance() {
    Ddb.getInstance();
    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    EmployeeImpl testInstance2 = EmployeeImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    ArrayList<Employee> testEmployeeList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testEmployeeList, testInstance.getAll());
  }

  @Test
  void restoreFromCSV() {
    //    Ddb.getInstance();
    //    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    //    ArrayList<Employee> testLocationList = testInstance.getAll();
    //    try {
    //      testInstance.restoreFromCSV("./src/test/resources/csv");
    //    } catch (IOException | SQLException e) {
    //      e.printStackTrace();
    //    }
    //    assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void getAll() {
    Ddb.getInstance();
    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    assertEquals(testInstance.getEmployeeList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    ArrayList<Employee> testEmployeeList = testInstance.getAll();
    Employee testEmployee = testEmployeeList.get(testEmployeeList.size() - 1);
    testEmployeeList.remove(testEmployee);
    try {
      testInstance.deleteTuple(testEmployee.getEmployeeID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testEmployeeList, testInstance.getAll());
  }

  @Test
  void addNewEntity() {
    Ddb.getInstance();
    EmployeeImpl testInstance = EmployeeImpl.getInstance();
    ArrayList<Employee> testEmployeeList = testInstance.getAll();
    Employee testEmployee = new Employee(10, "FN", "LN", "911", "Nurse", "Token");
    testEmployeeList.add(testEmployee);
    try {
      testInstance.addNewEntity(testEmployee);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testEmployeeList, testInstance.getAll());
  }

  @Test
  void modifyEntity() {
    // TODO: Not sure how to test with no linked list involved
    // Might want to check if this updates the linked list / should it even do so
  }

  //  @Test
  //  void getLocationPkAndName() {
  //    Ddb.getInstance();
  //    EmployeeImpl testInstance = EmployeeImpl.getInstance();
  //    ArrayList<Employee> testEmployeeList = testInstance.getAll();
  //    assertEquals(testInstance.getLocationPkAndName().length, testEmployeeList.size());
  //  }
}
