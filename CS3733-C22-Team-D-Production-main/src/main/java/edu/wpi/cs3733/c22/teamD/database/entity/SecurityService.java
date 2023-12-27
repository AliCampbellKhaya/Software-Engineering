package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SecurityService extends ServiceRequest {

  public SecurityService(
      String serviceRequestType,
      int creatorEmployee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location) {
    super(
        serviceRequestType,
        creatorEmployee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        location);
  }

  public SecurityService(
      int serviceID,
      String serviceRequestType,
      int creatorEmployee,
      int assignedEmployee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String completedTime,
      String location) {
    super(
        serviceID,
        serviceRequestType,
        creatorEmployee,
        assignedEmployee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        completedTime,
        location);
  }
}
