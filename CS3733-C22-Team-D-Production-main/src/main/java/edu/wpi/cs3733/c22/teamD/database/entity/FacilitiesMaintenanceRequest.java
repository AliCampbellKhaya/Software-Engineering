package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilitiesMaintenanceRequest extends ServiceRequest {

  public FacilitiesMaintenanceRequest(
      String serviceRequestType,
      int creator_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location) {
    super(
        serviceRequestType,
        creator_employee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        location);
  }

  public FacilitiesMaintenanceRequest(
      int serviceID,
      String serviceRequestType,
      int creator_employee,
      int assigned_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String completedTime,
      String location) {
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
  }
}
