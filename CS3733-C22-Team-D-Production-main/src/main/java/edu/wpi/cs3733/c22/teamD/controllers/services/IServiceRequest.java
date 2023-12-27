package edu.wpi.cs3733.c22.teamD.controllers.services;

import java.sql.SQLException;
import javafx.fxml.FXML;

public interface IServiceRequest {
  // @FXML JFXButton submitButton = new JFXButton();

  @FXML
  public void enableSubmitButton();

  @FXML
  public void onClear() throws SQLException;

  @FXML
  public boolean isFilledOption();
}
