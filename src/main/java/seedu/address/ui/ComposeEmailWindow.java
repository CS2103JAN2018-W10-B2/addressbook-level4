package seedu.address.ui;

import java.io.IOException;
import java.net.URL;

import javax.mail.MessagingException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
    private static final String composeWindow = "/view/emailcompose.fxml";
    private static final String successWindow = "/view/successwindow.fxml";
    private Stage puWindow = new Stage();
    private String content;

    @FXML
    private AnchorPane successPopup;

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

    @FXML
    private Button closeButton;

    @FXML
    private Label results;

    /**
     * Creates a new Email compose window
     */
    public ComposeEmailWindow(String type, String email, String sub, String content)
            throws IOException, SyntaxException {
        //get URL
        FXMLLoader fxmlLoader = loadScene(composeWindow);
        Parent root = (Parent) fxmlLoader.load();
        recipients.setText(email);
        subject.setText(type + sub);
        if (content != "") {
            addPreviousEmail(type, email, sub, content);
        }
        puWindow.initModality(Modality.APPLICATION_MODAL);
        puWindow.initStyle(StageStyle.UNDECORATED);
        puWindow.setTitle("Compose Email");
        //puWindow.getScene().setRoot(root);
        //this.main.setRoot(root);
        puWindow.setScene(new Scene(root));
        puWindow.show();
    }

    /**
     * Shows the message that user is trying to reply to or forward
     * @param type indicates whether or not it is a forwarded email or reply
     * @param email is the person that sent the previous email
     * @param subject the subject of the previous email
     * @param content the contents of the previous email
     */
    private void addPreviousEmail(String type, String email, String subject, String content) {
        String previousMessage = "\n\n\n";
        if (type == "RE: ") {
            previousMessage = previousMessage + "-----Original Message-----\n";
        } else {
            previousMessage = previousMessage + "Begin Forwarded Message: \n\n";
        }
        previousMessage = previousMessage + "From: " + email + "\n"
                + "To: sg.salesperson@gmail.com\n"
                + "Subject: " + subject + "\n\n"
                + content + "\n\n\n";
        message.setText(previousMessage);
    }
    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    public void sendEmail() throws IOException {
        FXMLLoader fxmlLoader = loadScene(successWindow);
        Parent confirm = (Parent) fxmlLoader.load();
        try {
            new MailServer(parseRecipients(), subject.getText(), message.getText());
            results.setText("Email Sent Successfully");
            results.setTextFill(Color.web("#4cc486"));
        } catch (MessagingException e) {
            results.setText(e.getCause().toString());
            results.setTextFill(Color.web("Red"));
        }
        puWindow.getScene().setRoot(confirm);
        puWindow.show();
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

    /**
     *loads the scene
     */
    private FXMLLoader loadScene (String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlFileUrl = MainApp.class.getResource(fxml);
        fxmlLoader.setLocation(fxmlFileUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(null);
        return fxmlLoader;
    }
}
