package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InternalTransportationRequest extends ServiceRequest {
  private String patient_Patient;
  private String end_Location;
  private String pickupTime;

  public InternalTransportationRequest(
      String serviceRequestType,
      int creator_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location,
      String patient_Patient,
      String end_Location,
      String pickupTime) {
    super(
        serviceRequestType,
        creator_employee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        location);
    this.patient_Patient = patient_Patient;
    this.end_Location = end_Location;
    this.pickupTime = pickupTime;
  }

  public InternalTransportationRequest(
      int serviceID,
      String serviceRequestType,
      int creator_employee,
      int assigned_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String completedTime,
      String start_location,
      String patient_Patient,
      String end_Location,
      String pickupTime) {
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
        start_location);
    this.patient_Patient = patient_Patient;
    this.end_Location = end_Location;
    this.pickupTime = pickupTime;
  }
}
