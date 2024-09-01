package jackson.graphics;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import jackson.Jackson;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Jackson jackson;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User_Icon.jpg"));
    private Image jacksonImage = new Image(this.getClass().getResourceAsStream("/images/Jackson_Icon.png"));

    @FXML
    public void initialize() {
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
        this.userInput.setPromptText("Type here!");
    }

    /** Injects the Duke instance */
    public void setJackson(Jackson j) {
        this.jackson = j;
        this.dialogContainer.getChildren().addAll(
                DialogBox.getJacksonDialog(this.jackson.load(), this.jacksonImage, "intro"),
                DialogBox.getJacksonDialog(this.jackson.start(), this.jacksonImage, "intro"));
    }

    /**
     * Creates two dialog boxes, one for user input and one for Jackson's response.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText();
        String response = this.jackson.getResponse(input);
        String commandType = this.jackson.getCommandType();

        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, this.userImage),
                DialogBox.getJacksonDialog(response, this.jacksonImage, commandType)
        );

        if (commandType.equals("exit")) {
            this.dialogContainer.getChildren().addAll(
                    DialogBox.getJacksonDialog(jackson.sayGoodbye(), this.jacksonImage, commandType),
                    DialogBox.getJacksonDialog("This window will close in 5 seconds hor...", this.jacksonImage, "error")
            );
            this.userInput.setDisable(true);
            this.userInput.setPromptText("Exiting...");
            this.sendButton.setDisable(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> Platform.exit()));
            timeline.play();
        }
        this.userInput.clear();
    }
}