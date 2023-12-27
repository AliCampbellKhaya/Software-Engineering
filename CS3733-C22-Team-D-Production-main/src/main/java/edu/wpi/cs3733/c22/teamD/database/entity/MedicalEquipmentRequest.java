package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalEquipmentRequest extends ServiceRequest {
  private int requested_medicalEquipment;

  public MedicalEquipmentRequest(
      String serviceRequestType,
      int creator_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location,
      int requested_medicalEquipment) {
    super(
        serviceRequestType,
        creator_employee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        location);
    this.requested_medicalEquipment = requested_medicalEquipment;
  }

  public MedicalEquipmentRequest(
      int serviceID,
      String serviceRequestType,
      int creator_employee,
      int assigned_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String completedTime,
      String location,
      int requested_medicalEquipment) {
    super(
        serviceID,
        serviceRequestType,
        creator_employee,
        assigned_employee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        completedTime,
        location);
    this.requested_medicalEquipment = requested_medicalEquipment;
  }
}
