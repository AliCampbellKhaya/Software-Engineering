package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.Location;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class LocationImplTest {

  @Test
  void getInstance() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    LocationImpl testInstance2 = LocationImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    ArrayList<Location> testLocationList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void restoreFromCSV() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    ArrayList<Location> testLocationList = testInstance.getAll();
    try {
      testInstance.restoreFromCSV(new File("./src/test/resources/csv"));
    } catch (IOException | SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void getAll() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    assertEquals(testInstance.getLocationList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    ArrayList<Location> testLocationList = testInstance.getAll();
    Location testLocation = testLocationList.get(testLocationList.size() - 1);
    testLocationList.remove(testLocation);
    try {
      testInstance.deleteTuple(testLocation.getNodeID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void addNewEntity() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    ArrayList<Location> testLocationList = testInstance.getAll();
    Location testLocation =
        new Location("testAddNewEntity", 1, 1, "1", "1", "1", "testAddNewEntity", "test");
    testLocationList.add(testLocation);
    try {
      testInstance.addNewEntity(testLocation);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void modifyEntity() {
    // TODO: Not sure how to test with no linked list involved
    // Might want to check if this updates the linked list / should it even do so
  }

  @Test
  void getLocationPkAndName() {
    Ddb.getInstance();
    LocationImpl testInstance = LocationImpl.getInstance();
    ArrayList<Location> testLocationList = testInstance.getAll();
    assertEquals(testInstance.getLocationPkAndName().length, testLocationList.size());
  }
}
