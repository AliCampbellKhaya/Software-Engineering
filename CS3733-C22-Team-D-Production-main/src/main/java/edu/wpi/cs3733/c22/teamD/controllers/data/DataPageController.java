package edu.wpi.cs3733.c22.teamD.controllers.data;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.controllers.data.popups.PopupController;
import java.awt.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.controlsfx.control.PopOver;

public abstract class DataPageController implements DataPage {
  @FXML JFXButton addDB = new JFXButton();
  @FXML JFXButton modifyDB = new JFXButton();
  @FXML JFXButton deleteDB = new JFXButton();

  public void loadPop(String resource) {
    final FXMLLoader form = new FXMLLoader(App.class.getResource(resource));
    final PopOver popUp = new PopOver();
    popUp.setArrowSize(0);

    try {
      popUp.setContentNode(form.load());

      // for add, do add
      final PopupController popController = form.getController();
      popController.setPopOver(popUp);

    } catch (IOException e) {
      e.printStackTrace();
    }
    final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    popUp.show(App.getPrimaryStage(), mouseLocation.getX(), mouseLocation.getY());
  }
}
