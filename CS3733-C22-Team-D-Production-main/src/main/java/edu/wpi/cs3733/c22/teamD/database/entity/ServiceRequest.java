package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class ServiceRequest {
  private int serviceID;
  private String serviceRequestType;
  private int creator_employee;
  private int assigned_employee;
  private String description;
  private String serviceStatus;
  private String urgency;
  private String initiatedTime;
  private String completedTime;
  private String location;

  public ServiceRequest(
      String serviceRequestType,
      int creator_employee,
      String description,
      String serviceStatus,
      String urgency,
      String initiatedTime,
      String location) {
    this.serviceID = -1;
    this.serviceRequestType = serviceRequestType;
    this.creator_employee = creator_employee;
    this.assigned_employee = creator_employee;
    this.description = description;
    this.serviceStatus = serviceStatus;
    this.urgency = urgency;
    this.initiatedTime = initiatedTime;
    this.completedTime = "NaN";
    this.location = location;
  }

  public ServiceRequest(
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
    this.serviceID = serviceID;
    this.serviceRequestType = serviceRequestType;
    this.creator_employee = creator_employee;
    this.assigned_employee = assigned_employee;
    this.description = description;
    this.serviceStatus = serviceStatus;
    this.urgency = urgency;
    this.initiatedTime = initiatedTime;
    this.completedTime = completedTime;
    this.location = location;
  }
}
