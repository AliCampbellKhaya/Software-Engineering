package edu.wpi.cs3733.c22.teamD.navigation;

import edu.wpi.cs3733.c22.teamD.App;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Navigation {

  private static void load(final Screen screen) {
    try {
      final FXMLLoader loader = new FXMLLoader(App.class.getResource(screen.getFilename()));

      if (screen.getFilename().equals("views/Parent.fxml")) {
        GridPane mainScene = loader.load();
        App.setChildPane((BorderPane) mainScene.getChildren().get(0));
        App.getPrimaryStage().setScene(new Scene(mainScene));
        // App.getPrimaryStage().setFullScreen(true);

        App.getPrimaryStage().setMaximized(true);

        Navigation.navigate(Screen.DEFAULT);
      } else {
        App.getChildPane().setCenter(loader.load());
      }

    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  public static void home() {
    load(Screen.DEFAULT);
  }

  public static void navigate(final Screen screen) {
    load(screen);
  }
}
