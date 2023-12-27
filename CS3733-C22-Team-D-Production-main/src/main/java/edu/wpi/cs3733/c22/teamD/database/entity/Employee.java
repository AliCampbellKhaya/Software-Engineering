package edu.wpi.cs3733.c22.teamD.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
  private int employeeID;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String job;
  private String token;
}
