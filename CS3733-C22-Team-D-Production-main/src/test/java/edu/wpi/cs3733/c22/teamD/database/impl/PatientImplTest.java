package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.Patient;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class PatientImplTest {
  @Test
  void getInstance() {
    Ddb.getInstance();
    PatientImpl testInstance = PatientImpl.getInstance();
    PatientImpl testInstance2 = PatientImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void refresh() {
    Ddb.getInstance();
    PatientImpl testInstance = PatientImpl.getInstance();
    ArrayList<Patient> testPatientList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testPatientList, testInstance.getAll());
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
    PatientImpl testInstance = PatientImpl.getInstance();
    assertEquals(testInstance.getPatientList(), testInstance.getAll());
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    PatientImpl testInstance = PatientImpl.getInstance();
    ArrayList<Patient> testPatientList = testInstance.getAll();
    Patient testPatient = testPatientList.get(testPatientList.size() - 1);
    testPatientList.remove(testPatient);
    try {
      testInstance.deleteTuple(testPatient.getPatientID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testPatientList, testInstance.getAll());
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
