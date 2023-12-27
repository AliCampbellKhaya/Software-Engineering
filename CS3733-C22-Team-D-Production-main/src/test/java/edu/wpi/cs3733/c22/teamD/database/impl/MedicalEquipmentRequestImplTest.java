package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.MedicalEquipmentRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class MedicalEquipmentRequestImplTest {
  @Test
  void getInstance() {
    Ddb.getInstance();
    MedicalEquipmentRequestImpl testInstance = MedicalEquipmentRequestImpl.getInstance();
    MedicalEquipmentRequestImpl testInstance2 = MedicalEquipmentRequestImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    MedicalEquipmentRequestImpl testInstance = MedicalEquipmentRequestImpl.getInstance();
    ArrayList<MedicalEquipmentRequest> testMERList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testMERList, testInstance.getAll());
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
    MedicalEquipmentRequestImpl testInstance = MedicalEquipmentRequestImpl.getInstance();
    assertEquals(testInstance.getEquipmentRequestList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    MedicalEquipmentRequestImpl testInstance = MedicalEquipmentRequestImpl.getInstance();
    ArrayList<MedicalEquipmentRequest> testMERList = testInstance.getAll();
    MedicalEquipmentRequest testITR = testMERList.get(testMERList.size() - 1);
    testMERList.remove(testITR);
    try {
      testInstance.deleteTuple(testITR.getServiceID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testMERList, testInstance.getAll());
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
