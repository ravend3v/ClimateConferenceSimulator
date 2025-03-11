// File: src/main/java/utils/ChatterDisplayUtils.java
package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.util.List;

public class ChatterDisplayUtils {

    private Timeline chatterTimeline;
    private int currentChatterIndex = 0;

    public void startChatterTimeline(TextArea chatterArea, List<String> chatterMessages, int intervalMillis) {
        chatterTimeline = new Timeline(new KeyFrame(Duration.millis(intervalMillis), event -> {
            if (currentChatterIndex < chatterMessages.size()) {
                String message = chatterMessages.get(currentChatterIndex);
                Platform.runLater(() -> chatterArea.appendText(message + "\n"));
                currentChatterIndex++;
            } else {
                chatterTimeline.stop();
            }
        }));
        chatterTimeline.setCycleCount(Timeline.INDEFINITE);
        chatterTimeline.play();
    }

    public void stopChatterTimeline() {
        if (chatterTimeline != null) {
            chatterTimeline.stop();
        }
    }
}