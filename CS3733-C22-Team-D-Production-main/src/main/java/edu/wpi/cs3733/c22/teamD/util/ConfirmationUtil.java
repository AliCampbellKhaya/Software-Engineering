package edu.wpi.cs3733.c22.teamD.util;

import edu.wpi.cs3733.c22.teamD.App;
import edu.wpi.cs3733.c22.teamD.controllers.data.popups.ConfirmationPopUpController;
import java.awt.*;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.controlsfx.control.PopOver;

public class ConfirmationUtil {

  public void loadPop() {
    final FXMLLoader form =
        new FXMLLoader(App.class.getResource("views/data/ConfirmationPopUp.fxml"));
    final PopOver popUp = new PopOver();
    popUp.setArrowSize(0);

    try {
      popUp.setContentNode(form.load());
      final ConfirmationPopUpController popUpController = new ConfirmationPopUpController();
      popUpController.setPopOver(popUp);

    } catch (IOException e) {
      e.printStackTrace();
    }
    final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
    popUp.show(App.getPrimaryStage(), mouseLocation.getX() - 300, mouseLocation.getY() - 300);
  }
}
