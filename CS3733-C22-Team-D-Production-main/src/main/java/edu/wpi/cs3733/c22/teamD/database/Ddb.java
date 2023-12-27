package edu.wpi.cs3733.c22.teamD.database;

import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import edu.wpi.cs3733.c22.teamD.database.impl.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import lombok.Getter;

public class Ddb {

  private static final Ddb instance = new Ddb();
  @Getter private static boolean hasSwitched = false;
  @Getter private static Statement statement = null;
  @Getter private static Connection connection = null;
  @Getter private boolean isEmbedded = true;
  private EmployeeImpl employeeImpl = null;
  private LocationImpl locationImpl = null;
  private ITEquipmentImpl itEquipmentImpl = null;
  private LaundryItemImpl laundryItemImpl = null;
  private PatientImpl patientImpl = null;
  private MedicineImpl medicineImpl = null;
  private LaundryOrderImpl laundryOrder = null;

  private ServiceRequestImpl serviceRequestImpl = null;

  private ComputerRequestImpl computerRequestImpl = null;
  private SanitationRequestImpl sanitationRequestImpl = null;
  private InternalTransportationRequestImpl internalTransportationRequestImpl = null;
  private LaundryRequestImpl laundryRequestImpl = null;
  private MedicineDeliveryRequestImpl medicineDeliveryRequestImpl = null;
  private MedicalEquipmentImpl medicalEquipmentImpl = null;
  private MedicalEquipmentRequestImpl medicalEquipmentRequestImpl = null;
  private FacilitiesMaintenanceRequestImpl facilitiesMaintenanceRequestImpl = null;
  private SecurityServiceImpl securityServiceImpl = null;

  private File directory = new File("src/main/resources/edu/wpi/cs3733/c22/teamD/BackupCSV");

  /*
   - Equipment
   - IT Equipment
   - Laundry Items
   - Anything that is not a request

  */

  /** Private constructor that generates the tables if needed */
  private Ddb() {
    try {
      Class.forName("org.apache.derby.jdbc.ClientDriver");
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

      connection =
          DriverManager.getConnection(
              ConnectionType.EMBEDDED.getConnectionName() + "; create=true");
      statement = connection.createStatement();

      /*
         Non service request need to follow LocationImpl structure
         - constructor
         - populateFromList
         - public void clearTable() throw SQLException

         Service Request specifics need to follow:
         - same thing as before
         - remove this.serviceRequestImpl line from the for loop

         Side notes : createTable should have
         - this.connection = Ddb.getConnection() as the first executable line

         After done, comment in on the Ddb constructor and the
         switchDatabase method should have that Impl added on.
      */

      this.employeeImpl = EmployeeImpl.getInstance();
      this.locationImpl = LocationImpl.getInstance();
      this.itEquipmentImpl = ITEquipmentImpl.getInstance();
      this.laundryItemImpl = LaundryItemImpl.getInstance();
      this.patientImpl = PatientImpl.getInstance();
      this.medicineImpl = MedicineImpl.getInstance();
      this.medicalEquipmentImpl = MedicalEquipmentImpl.getInstance();

      this.serviceRequestImpl = ServiceRequestImpl.getInstance();

      this.computerRequestImpl = ComputerRequestImpl.getInstance();
      this.sanitationRequestImpl = SanitationRequestImpl.getInstance();
      this.internalTransportationRequestImpl = InternalTransportationRequestImpl.getInstance();
      this.laundryRequestImpl = LaundryRequestImpl.getInstance();
      this.medicineDeliveryRequestImpl = MedicineDeliveryRequestImpl.getInstance();
      this.medicalEquipmentRequestImpl = MedicalEquipmentRequestImpl.getInstance();
      this.facilitiesMaintenanceRequestImpl = FacilitiesMaintenanceRequestImpl.getInstance();
      this.securityServiceImpl = SecurityServiceImpl.getInstance();
      this.laundryOrder = LaundryOrderImpl.getInstance();

      //
      //      this.switchDatabase(ConnectionType.EMBEDDED);
      //
      //      this.switchDatabase(ConnectionType.CLIENT_SERVER);
      //
      //      this.switchDatabase(ConnectionType.EMBEDDED);

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the only instance of 'Ddb'. If it does not exist, create it.
   *
   * @return instance of Ddb
   */
  public static Ddb getInstance() {
    return instance;
  }

  public void addServiceRequestEntity(ServiceRequest request) throws SQLException {
    int id = ServiceRequestImpl.getInstance().getAll().size() + 1;
    request.setServiceID(id);
    ServiceRequestImpl.getInstance().addNewEntity(request);
  }

  public void downloadAll() throws IOException, URISyntaxException {
    if (!directory.exists()) {
      directory.mkdir();
    }

    for (File file : directory.listFiles()) if (!file.isDirectory()) file.delete();

    this.itEquipmentImpl.storeToCSV(new File(directory.getAbsolutePath() + "/ITEquipment.csv"));
    this.computerRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/ComputerRequest.csv"));
    this.patientImpl.storeToCSV(new File(directory.getAbsolutePath() + "/Patient.csv"));
    this.employeeImpl.storeToCSV(new File(directory.getAbsolutePath() + "/Employee.csv"));
    this.internalTransportationRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/InternalTransportationRequest.csv"));
    this.laundryItemImpl.storeToCSV(new File(directory.getAbsolutePath() + "/LaundryItem.csv"));
    this.laundryRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/LaundryRequest.csv"));
    this.locationImpl.storeToCSV(new File(directory.getAbsolutePath() + "/Location.csv"));
    this.medicineImpl.storeToCSV(new File(directory.getAbsolutePath() + "/Medicine.csv"));
    this.medicalEquipmentImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/MedicalEquipment.csv"));
    this.medicalEquipmentRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/MedicalEquipmentRequest.csv"));
    this.medicineDeliveryRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/MedicineDeliveryRequest.csv"));
    this.sanitationRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/SanitationRequest.csv"));
    this.facilitiesMaintenanceRequestImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/FacilitiesMaintenance.csv"));
    this.securityServiceImpl.storeToCSV(
        new File(directory.getAbsolutePath() + "/SecurityService.csv"));
  }

  //  public void uploadAll() throws URISyntaxException {
  //    if (!directory.exists()) {
  //      directory.mkdir();
  //    }
  //
  //    if (!hasSwitched) {
  //      this.employeeImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/Employee.csv").toURI()));
  //      this.locationImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/Location.csv").toURI()));
  //      this.itEquipmentImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/ITEquipment.csv").toURI()));
  //      this.patientImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/Patient.csv").toURI()));
  //      this.medicineImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/Medicine.csv").toURI()));
  //      this.laundryItemImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/LaundryItem.csv").toURI()));
  //      ServiceRequestImpl.getInstance().refresh();
  //      this.computerRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/ComputerRequest.csv").toURI()));
  //      this.sanitationRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/SanitationRequest.csv").toURI()));
  //      this.internalTransportationRequestImpl.tableOnStartup(
  //          new
  // File(App.class.getResource("BackupCSV/InternalTransportationRequest.csv").toURI()));
  //      this.laundryRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/LaundryRequest.csv").toURI()));
  //      this.medicineDeliveryRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/MedicineDeliveryRequest.csv").toURI()));
  //      this.medicalEquipmentImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/MedicalEquipment.csv").toURI()));
  //      this.medicalEquipmentRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/MedicalEquipmentRequest.csv").toURI()));
  //      this.facilitiesMaintenanceRequestImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/FacilitiesMaintenance.csv").toURI()));
  //      this.securityServiceImpl.tableOnStartup(
  //          new File(App.class.getResource("BackupCSV/SecurityService.csv").toURI()));
  //
  //      hasSwitched = true;
  //    } else {
  //      try {
  //        this.employeeImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/Employee.csv").toURI()));
  //        this.locationImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/Location.csv").toURI()));
  //        this.itEquipmentImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/ITEquipment.csv").toURI()));
  //        this.patientImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/Patient.csv").toURI()));
  //        this.medicineImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/Medicine.csv").toURI()));
  //        this.laundryItemImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/LaundryItem.csv").toURI()));
  //        ServiceRequestImpl.getInstance().refresh();
  //        this.computerRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/ComputerRequest.csv").toURI()));
  //        this.sanitationRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/SanitationRequest.csv").toURI()));
  //        this.internalTransportationRequestImpl.restoreFromCSV(
  //            new
  // File(App.class.getResource("BackupCSV/InternalTransportationRequest.csv").toURI()));
  //        this.laundryRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/LaundryRequest.csv").toURI()));
  //        this.medicineDeliveryRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/MedicineDeliveryRequest.csv").toURI()));
  //        this.medicalEquipmentImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/MedicalEquipment.csv").toURI()));
  //        this.medicalEquipmentRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/MedicalEquipmentRequest.csv").toURI()));
  //        this.facilitiesMaintenanceRequestImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/FacilitiesMaintenance.csv").toURI()));
  //        this.securityServiceImpl.restoreFromCSV(
  //            new File(App.class.getResource("BackupCSV/SecurityService.csv").toURI()));
  //
  //      } catch (SQLException | IOException e) {
  //        e.printStackTrace();
  //      }
  //    }
  //  }

  public void switchDatabase(ConnectionType type) throws SQLException {
    if (type.getConnectionName().equals("jdbc:derby://localhost:1527/BWDB")) {
      this.isEmbedded = false;
    } else {
      this.isEmbedded = true;
    }

    connection = DriverManager.getConnection(type.getConnectionName());
    statement = connection.createStatement();

    this.employeeImpl.uploadNewConnection();
    this.locationImpl.uploadNewConnection();
    this.itEquipmentImpl.uploadNewConnection();
    this.laundryItemImpl.uploadNewConnection();
    this.patientImpl.uploadNewConnection();
    this.medicineImpl.uploadNewConnection();
    this.medicalEquipmentImpl.uploadNewConnection();

    this.serviceRequestImpl.uploadNewConnection();

    this.computerRequestImpl.uploadNewConnection();
    this.sanitationRequestImpl.uploadNewConnection();
    this.internalTransportationRequestImpl.uploadNewConnection();
    this.laundryRequestImpl.uploadNewConnection();
    this.medicineDeliveryRequestImpl.uploadNewConnection();
    this.medicalEquipmentRequestImpl.uploadNewConnection();
    this.facilitiesMaintenanceRequestImpl.uploadNewConnection();
    this.securityServiceImpl.uploadNewConnection();
    this.laundryOrder.uploadNewConnection();
  }
}

// todo enumerate the connection names
