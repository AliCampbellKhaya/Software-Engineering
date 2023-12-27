package edu.wpi.cs3733.c22.teamD.controllers.experimental;

import com.jfoenix.controls.JFXListView;
import edu.wpi.cs3733.c22.teamD.navigation.Navigation;
import edu.wpi.cs3733.c22.teamD.navigation.Screen;
import java.awt.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SearchBarController {
  boolean gay = true;
  @FXML private TextField searchBar;
  @FXML private JFXListView<String> listView;
  @FXML private Text errorText;

  private ObservableList keyWords = FXCollections.observableArrayList();

  public void initialize() {
    this.searchBar.setFocusTraversable(
        false); // prevents auto highlighting. Interferes with certain design features
    populateList(); // adds strings to list

    FilteredList filter =
        new FilteredList(keyWords, s -> true); // creates a filter for the list based on search

    // uses lambda function to "watch" text in search bar to update the filter list
    searchBar
        .textProperty()
        .addListener(
            obs -> {
              String filterS = searchBar.getText(); // grabs text from search bar

              // if searchBar is empty, display everything in keyWords
              // else, display all strings in keyWords that contain the string in searchBar
              if (filterS == null
                  || filterS.length()
                      == 0) { // TODO: replace null. This leads to minor optimization flaws
                filter.setPredicate(s -> true);
              } else {
                filter.setPredicate(s -> ((String) s).contains(filterS.toLowerCase()));
              }
            });

    this.listView.setItems(filter); // updats the listView to show what is displayed in the filter
  }

  public void populateList() {
    keyWords.addAll( // TODO: update this to be more coder-friendly and automatic. Consider using
        // reverse RegEx to convert enums to strings
        "home page",
        "map editor",
        "side view map",
        "employee data",
        "location data",
        "equipment data",
        "service requests",
        "internal transport services",
        "laundry services",
        "sanitation services",
        "medical equipment delivery services",
        "medicine delivery services",
        "security services",
        "facilities maintenance",
        "maintenance services");
  }

  public void showList() {
    if ((this.searchBar.getText().isEmpty() || this.searchBar.getText().length() == 0)) {
      this.listView.setVisible(false);
    } else {
      this.listView.setVisible(gay);
    }
  }

  public void selectSearch() {
    String searchFor = this.listView.getSelectionModel().getSelectedItem().toUpperCase();
    doSearch(searchFor);
  }

  public void directSearch() {
    String s = this.searchBar.getText();
    boolean exists = false;
    for (int i = 0; i < this.keyWords.size(); i++) {
      if (s.equals(this.keyWords.get(i))) {
        i = this.keyWords.size();
        exists = true;
      }
    }
    if (!exists) {
      displayError();
    } else {
      doSearch(s.toUpperCase());
    }
  }

  public void doSearch(String searchFor) {
    searchFor = searchFor.replaceAll(" SERVICES", "");
    searchFor = searchFor.replaceAll(" PAGE", "");
    searchFor = searchFor.replaceAll("SERVICE REQUESTS", "TRAFFIC");

    searchFor = searchFor.replaceAll(" ", "_");

    if (searchFor.contains("MEDICINE")) {
      Navigation.navigate(Screen.MEDICINE);
    } else if (searchFor.contains("HOME")) {
      Navigation.navigate(Screen.DEFAULT);
    } else {
      Navigation.navigate(Screen.valueOf(searchFor));
    }
  }

  /** displays the invalid search text. in I4, this will be replaced with near search results */
  public void displayError() {
    this.errorText.setVisible(true);
  }
}
