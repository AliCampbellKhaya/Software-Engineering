package edu.wpi.cs3733.c22.teamD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Getter @Setter private static Stage primaryStage;
  @Getter @Setter private static BorderPane childPane;
  @Getter private static ArrayList<Image> floors;

  /** Initialize JavaFX application */
  @Override
  public void init() {
    log.info("Application starting...");
    loadAssets();
  }

  /**
   * Sets up the initial application state
   *
   * @param primaryStage The main stage of our application
   * @throws IOException If the .fxml does not exist
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    App.primaryStage = primaryStage;
    final FXMLLoader loader = new FXMLLoader(App.class.getResource("views/Login.fxml"));
    GridPane mainScene = loader.load();
    App.childPane = (BorderPane) mainScene.getChildren().get(0);
    Scene scene = new Scene(mainScene);

    primaryStage.setMaximized(true);

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /** Runs when the JavaFX closes */
  @Override
  public void stop() {
    log.info("Application stopping...");
  }

  /** Preload the image assets */
  public void loadAssets() {
    floors = new ArrayList<>();
    floors.add(
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/LL1.png"))));
    floors.add(
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/LL2.png"))));
    floors.add(
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/FL1.png"))));
    floors.add(
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/FL2.png"))));
    floors.add(
        new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/map/FL3.png"))));
  }
}
