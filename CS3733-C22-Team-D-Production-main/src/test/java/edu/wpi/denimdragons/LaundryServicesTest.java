package edu.wpi.denimdragons;

import static org.testfx.api.FxAssert.verifyThat;

import edu.wpi.cs3733.c22.teamD.App;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

public class LaundryServicesTest extends ApplicationTest {
  @BeforeEach
  public void setUp() throws TimeoutException, InterruptedException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    Thread.sleep(3000);
    clickOn("#bRequest");
    clickOn("#LaundryButton");
  }

  @Test
  public void testIsFilledOptionPass() {
    clickOn("#laundryPickupCheckBox");
    clickOn("#locationField");
    write("beans");
    clickOn("#scrubsCheckBox");

    verifyThat("#submitButton", NodeMatchers.isEnabled());
  }

  @Test
  void testEnableSubmitButtonLocationPassWithOther() { // Done by Jacob Reiss <================
    clickOn("#laundryPickupCheckBox");
    clickOn("#locationField");
    write("beans");
    clickOn("#otherCheckBox");
    clickOn("#otherField");
    write("beans2: electric boogaloo");
    verifyThat("#submitButton", NodeMatchers.isEnabled());
  }

  @Test
  public void testEnableSubmitButtonFailCase1() { // Done by Jacob Reiss <=========================
    clickOn("#locationField");
    write("beans");
    clickOn("#scrubsCheckBox");
    verifyThat("#submitButton", NodeMatchers.isDisabled());
  }

  @Test
  public void testEnableSubmitButtonFailCase3() {
    clickOn("#laundryPickupCheckBox");
    clickOn("#scrubsCheckBox");
    verifyThat("#submitButton", NodeMatchers.isDisabled());
  }

  @Test
  void testEnableSubmitButtonLocationFailCase2() { // Done by Jacob Reiss <================
    clickOn("#laundryPickupCheckBox");
    clickOn("#locationField");
    write("beans");
    verifyThat("#submitButton", NodeMatchers.isDisabled());
  }

  @Test
  void testEnableSubmitButtonLocationFailCase4() { // Done by Jacob Reiss <================
    clickOn("#laundryPickupCheckBox");
    clickOn("#locationField");
    write("beans");
    clickOn("#otherCheckBox");
    verifyThat("#submitButton", NodeMatchers.isDisabled());
  }
}
