/*-------------------------*/
/* DO NOT DELETE THIS TEST */
/*-------------------------*/
package edu.wpi.denimdragons;

import edu.wpi.cs3733.c22.teamD.database.*;
import edu.wpi.cs3733.c22.teamD.database.impl.EmployeeImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.LocationImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentImpl;
import edu.wpi.cs3733.c22.teamD.database.impl.MedicalEquipmentRequestImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultTest {
  @AfterAll
  //  static void cleanup() throws SQLException {
  //    LocationImpl.getInstance().deleteTuple("123");
  //  }

  // Ali
  @Test
  public void LocationGetInstance() {
    Ddb.getInstance();
    Assertions.assertNotNull(LocationImpl.getInstance().getAll());
  }

  // Qui
  //  @Test
  //  public void LocationAddTuple() throws SQLException {
  //    Ddb.getInstance();
  //    ArrayList<Location> testList = new ArrayList<>();
  //    testList = LocationImpl.getInstance().getAll();
  //    LocationImpl.getInstance().addTuple("123");
  //    Location location = new Location();
  //    location.setNodeID("123");
  //    testList.add(location);
  //    Assertions.assertEquals(testList.toString(),
  // LocationImpl.getInstance().getAll().toString());
  //  }
  // Andy
  @Test
  public void EmployeesGetInstance() {
    Ddb.getInstance();
    Assertions.assertNotNull(EmployeeImpl.getInstance().getAll());
  }

  // NINI
  @Test
  public void MedicalEquipmentGetInstance() {
    Ddb.getInstance();
    Assertions.assertNotNull(MedicalEquipmentImpl.getInstance().getAll());
  }

  // Ilyas
  @Test
  public void MEServiceRequestGetInstance() {
    Ddb.getInstance();
    Assertions.assertNotNull(MedicalEquipmentRequestImpl.getInstance().getAll());
  }
}
