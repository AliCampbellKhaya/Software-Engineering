package edu.wpi.cs3733.c22.teamD.navigation;

public enum Screen {
  COMPUTER_SERVICE("views/services/ComputerServiceRequest.fxml"),
  DEFAULT("views/Home.fxml"),
  EMERGENCY_CODES("views/EmergencyCode.fxml"),
  EMPLOYEE_DATA("views/data/EmployeeData.fxml"),
  EQUIPMENT_DATA("views/data/EquipmentData.fxml"),
  INTERNAL_TRANSPORT("views/services/InternalTransportation.fxml"),
  LAUNDRY("views/services/LaundryServices.fxml"),
  LOCATION_DATA("views/data/LocationData.fxml"),
  LOGIN("views/Login.fxml"),
  MAP_EDITOR("views/mapeditor/MapEditor.fxml"),
  MEDICAL_EQUIPMENT_DELIVERY("views/services/MedicalEquipmentDelivery.fxml"),
  PARENT("views/Parent.fxml"),
  SANITATION("views/services/SanitationServices.fxml"),
  TRAFFIC("views/AddServiceRequestTraffic.fxml"),
  MEDICINE("views/services/MedicineDeliveryRequest.fxml"),
  SECURITY("views/services/SecurityService.fxml"),

  FACILITIES_MAINTENANCE("views/services/FacilitiesRequest.fxml"),
  SEARCH("views/experimental/SearchBarStyled.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}

  /*
   ABOUT("views/About.fxml"),
  AVREQUEST("views/AudioVisualRequest.fxml"),


  EXTRANSPORT("views/ExternalTransportation.fxml"),
  FOODREQUEST("views/FoodDeliveryService.fxml"),
  GIFTANDFLORAL("views/GiftAndFloral.fxml"),
  LANGUAGE("views/LanguageInterpreter.fxml"),
  RELIGIOUS("views/ReligiousRequest.fxml"),

   */
