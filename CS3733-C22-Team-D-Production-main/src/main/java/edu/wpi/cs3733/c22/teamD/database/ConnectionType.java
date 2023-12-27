package edu.wpi.cs3733.c22.teamD.database;

public enum ConnectionType {
  EMBEDDED("jdbc:derby:Ddb"),
  CLIENT_SERVER("jdbc:derby://localhost:1527/BWDB");

  private final String connectionName;

  ConnectionType(String connectionName) {
    this.connectionName = connectionName;
  }

  public String getConnectionName() {
    return connectionName;
  }
}
