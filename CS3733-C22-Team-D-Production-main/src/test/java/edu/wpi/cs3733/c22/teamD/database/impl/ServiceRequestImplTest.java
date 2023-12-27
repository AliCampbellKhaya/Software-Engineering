package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ServiceRequestImplTest {
  // TODO: More complicated tests, add after checking others

  @Test
  void getInstance() {
    ServiceRequestImpl testInstance1 = ServiceRequestImpl.getInstance();
    ServiceRequestImpl testInstance2 = ServiceRequestImpl.getInstance();
    assertEquals(testInstance1, testInstance2);
  }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    ServiceRequestImpl testInstance = ServiceRequestImpl.getInstance();
    ArrayList<ServiceRequest> testSRList = testInstance.getAll();
    ServiceRequest testSR = testSRList.get(testSRList.size() - 1);
    testSRList.remove(testSR);
    try {
      testInstance.deleteTuple(testSR.getServiceID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testSRList, testInstance.getAll());
  }

  @Test
  void inList() {}

  @Test
  void modifyEntity() {}

  @Test
  void addNewEntity() {}

  @Test
  void getAll() {}

  @Test
  void insertEntity() {}

  @Test
  void addToMaster() {}

  @Test
  void addArrayList() {}
}
