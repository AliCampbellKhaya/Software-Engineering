package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.SanitationRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class SanitationRequestImplTest {
  @Test
  void getInstance() {
    Ddb.getInstance();
    SanitationRequestImpl testInstance = SanitationRequestImpl.getInstance();
    SanitationRequestImpl testInstance2 = SanitationRequestImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    SanitationRequestImpl testInstance = SanitationRequestImpl.getInstance();
    ArrayList<SanitationRequest> testSRList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testSRList, testInstance.getAll());
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
    SanitationRequestImpl testInstance = SanitationRequestImpl.getInstance();
    assertEquals(testInstance.getSanitationRequestList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    SanitationRequestImpl testInstance = SanitationRequestImpl.getInstance();
    ArrayList<SanitationRequest> testSRList = testInstance.getAll();
    SanitationRequest testSR = testSRList.get(testSRList.size() - 1);
    testSRList.remove(testSR);
    try {
      testInstance.deleteTuple(testSR.getServiceID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testSRList, testInstance.getAll());
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
