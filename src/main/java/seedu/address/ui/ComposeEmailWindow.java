package seedu.address.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import seedu.address.MainApp;

//@@author glorialaw
/**
 * makes the compose email window
 */
public class ComposeEmailWindow {
    private static final String FXML = "/view/emailcompose.fxml";
    private Stage puWindow = new Stage();
    @FXML
    private AnchorPane emailComposePopup;

    @FXML
    private TextField recipients;

    @FXML
    private TextField subject;

    @FXML
    private TextArea message;

    @FXML
    private Button sendButton;

    @FXML
    private Button cancelButton;

    /**
     * Creates a new Email compose window
     */
    public ComposeEmailWindow(String email) throws IOException, SyntaxException {
        //get URL
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlFileUrl = MainApp.class.getResource(FXML);
        fxmlLoader.setLocation(fxmlFileUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(null);
        Parent root = (Parent) fxmlLoader.load();
        recipients.setText(email);
        puWindow.initModality(Modality.APPLICATION_MODAL);
        puWindow.initStyle(StageStyle.UNDECORATED);
        puWindow.setTitle("Compose Email");
        puWindow.setScene(new Scene(root));
        puWindow.show();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    public void sendEmail() {
        new MailServer(parseRecipients(), subject.getText(), message.getText());
        puWindow.close();
    }

    private String[] parseRecipients() {
        System.out.println(recipients.getText());
        return recipients.getText().split(",");
    }

    /**
     * Closes window with ESC key
     */
    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
        case ESCAPE:
            puWindow.close();
            break;
        default:
            // let JavaFx handle the keypress
        }
    }

    @FXML
    private void cancelWindow() {
        puWindow.close();
    }
}
