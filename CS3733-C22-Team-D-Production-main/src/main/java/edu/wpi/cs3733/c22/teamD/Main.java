package edu.wpi.cs3733.c22.teamD;

import edu.wpi.cs3733.c22.teamD.database.Ddb;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, FileNotFoundException {
    final Ddb backend = Ddb.getInstance();
    App.launch(App.class, args);
  }
}
