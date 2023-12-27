package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LaundryRequest extends ServiceRequest {
  public LaundryRequest(
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

  public LaundryRequest(
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
