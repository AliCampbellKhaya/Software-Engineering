package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.LaundryItem;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class LaundryItemImplTest {
  @Test
  void getInstance() {
    Ddb.getInstance();
    LaundryItemImpl testInstance = LaundryItemImpl.getInstance();
    LaundryItemImpl testInstance2 = LaundryItemImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    LaundryItemImpl testInstance = LaundryItemImpl.getInstance();
    ArrayList<LaundryItem> testITRList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testITRList, testInstance.getAll());
  }

  @Test
  void restoreFromCSV() {
    //        Ddb.getInstance();
    //        InternalTransportationRequestImpl testInstance =
    // InternalTransportationRequestImpl.getInstance();
    //        ArrayList<InternalTransportationRequest> testITRList = testInstance.getAll();
    //        try {
    //            testInstance.restoreFromCSV(new File("./src/test/resources/csv"));
    //        } catch (IOException | SQLException e) {
    //            e.printStackTrace();
    //        }
    //        assertEquals(testITRList, testInstance.getAll());
  }

  @Test
  void getAll() {
    Ddb.getInstance();
    LaundryItemImpl testInstance = LaundryItemImpl.getInstance();
    assertEquals(testInstance.getLaundryItemList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    LaundryItemImpl testInstance = LaundryItemImpl.getInstance();
    ArrayList<LaundryItem> testLaundryList = testInstance.getAll();
    LaundryItem testITR = testLaundryList.get(testLaundryList.size() - 1);
    testLaundryList.remove(testITR);
    try {
      testInstance.deleteTuple(testITR.getLaundryID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testLaundryList, testInstance.getAll());
  }

  @Test
  void addNewEntity() {
    //        Ddb.getInstance();
    //        InternalTransportationRequestImpl testInstance =
    // InternalTransportationRequestImpl.getInstance();
    //        ArrayList<InternalTransportationRequest> testLocationList = testInstance.getAll();
    //        Location testLocation =
    //                new Location("testAddNewEntity", 1, 1, "1", "1", "1", "testAddNewEntity",
    // "test");
    //        testLocationList.add(testLocation);
    //        try {
    //            testInstance.addNewEntity(testLocation);
    //        } catch (SQLException e) {
    //            e.printStackTrace();
    //        }
    //        assertEquals(testLocationList, testInstance.getAll());
  }

  @Test
  void modifyEntity() {
    // TODO: Not sure how to test with no linked list involved
    // Might want to check if this updates the linked list / should it even do so
  }
}
