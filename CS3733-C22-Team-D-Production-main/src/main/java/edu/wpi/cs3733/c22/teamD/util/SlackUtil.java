package edu.wpi.cs3733.c22.teamD.util;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;

public class SlackUtil {

  private static final Slack instance = Slack.getInstance();
  private static MethodsClient methods;
  private static final boolean setupDone = false;

  public SlackUtil() {
    if (!setupDone) {
      //      String token = System.getenv("SLACK_TOKEN");
      String token = "xoxb-3135969220710-3128373410311-isG5yXXtIU8OlrsbVEJobWFF";
      methods = instance.methods(token);
    }
  }

  public ChatPostMessageResponse sendMessage(String msg) throws SlackApiException, IOException {
    ChatPostMessageRequest request =
        ChatPostMessageRequest.builder()
            .channel("#code_announcements") // Use a channel ID `C1234567` is preferrable
            .text("@channel: " + msg)
            .build();
    ChatPostMessageResponse response = this.methods.chatPostMessage(request);

    return response;
  }
}
