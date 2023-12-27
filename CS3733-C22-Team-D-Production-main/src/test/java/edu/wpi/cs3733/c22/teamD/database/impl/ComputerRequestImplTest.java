package edu.wpi.cs3733.c22.teamD.database.impl;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import edu.wpi.cs3733.c22.teamD.database.entity.ComputerRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ComputerRequestImplTest {

  @Test
  void refresh() {
    Ddb.getInstance();
    ComputerRequestImpl testInstance = ComputerRequestImpl.getInstance();
    ArrayList<ComputerRequest> testCRList = testInstance.getAll();
    testInstance.refresh();
    assertEquals(testCRList, testInstance.getAll());
  }

  @Test
  void getInstance() {
    Ddb.getInstance();
    ComputerRequestImpl testInstance = ComputerRequestImpl.getInstance();
    ComputerRequestImpl testInstance2 = ComputerRequestImpl.getInstance();
    assertEquals(testInstance, testInstance2);
  }

  @Test
  void storeToCSV() {}

  //    @Test
  //    void restoreFromCSV() {
  //        Ddb.getInstance();
  //        ComputerRequestImpl testInstance = ComputerRequestImpl.getInstance();
  //        ArrayList<ComputerRequest> testCRList = testInstance.getAll();
  //        try {
  //            testInstance.restoreFromCSV(new File("./src/test/resources/csv"));
  //        } catch (IOException | SQLException e) {
  //            e.printStackTrace();
  //        }
  //        assertEquals(testCRList, testInstance.getAll());
  //    }

  @Test
  void deleteTuple() {
    Ddb.getInstance();
    ComputerRequestImpl testInstance = ComputerRequestImpl.getInstance();
    ArrayList<ComputerRequest> testCRList = testInstance.getAll();
    ComputerRequest testLocation = testCRList.get(testCRList.size() - 1);
    testCRList.remove(testLocation);
    try {
      testInstance.deleteTuple(testLocation.getServiceID());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertEquals(testCRList, testInstance.getAll());
  }

  @Test
  void addNewEntity() {}

  @Test
  void getAll() {
    Ddb.getInstance();
    ComputerRequestImpl testInstance = ComputerRequestImpl.getInstance();
    assertEquals(testInstance.getComputerServiceList(), testInstance.getAll());
  }

  @Test
  void modifyEntity() {}
}
