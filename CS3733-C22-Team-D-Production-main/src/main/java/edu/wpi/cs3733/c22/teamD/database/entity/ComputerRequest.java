package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComputerRequest extends ServiceRequest {
  private int itID;

  public ComputerRequest(
      String serviceRequestType,
      int creator_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location,
      int itID) {
    super(
        serviceRequestType,
        creator_employee,
        description,
        serviceStatus,
        urgency,
        initiatedTime,
        location);
    this.itID = itID;
  }

  public ComputerRequest(
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
      int itID) {
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
    this.itID = itID;
  }
}
