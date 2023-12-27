package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.ITEquipment;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ITEquipmentImplTest {
  @Test
  void getInstance() {
    Ddb.getInstance();
    ITEquipmentImpl testInstance = ITEquipmentImpl.getInstance();
    ITEquipmentImpl testInstance2 = ITEquipmentImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    ITEquipmentImpl testInstance = ITEquipmentImpl.getInstance();
    ArrayList<ITEquipment> testITList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testITList, testInstance.getAll());
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
    ITEquipmentImpl testInstance = ITEquipmentImpl.getInstance();
    assertEquals(testInstance.getItEquipmentList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    ITEquipmentImpl testInstance = ITEquipmentImpl.getInstance();
    ArrayList<ITEquipment> testITRList = testInstance.getAll();
    ITEquipment testITR = testITRList.get(testITRList.size() - 1);
    testITRList.remove(testITR);
    try {
      testInstance.deleteTuple(testITR.getItID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testITRList, testInstance.getAll());
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
