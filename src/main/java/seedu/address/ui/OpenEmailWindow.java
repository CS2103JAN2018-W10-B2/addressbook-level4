package seedu.address.ui;

import java.io.IOException;
import java.net.URL;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import seedu.address.MainApp;

//@@author glorialaw
/**
 * Generates the full email that was received
 */
public class OpenEmailWindow {
    private static final String openWindow = "/view/emailOpen.fxml";
    private Stage puWindow = new Stage();

    @FXML
    private AnchorPane emailComposePopup;

    @FXML
    private Label fromContent;

    @FXML
    private Label subjectContent;

    @FXML
    private TextArea msgContent;

    @FXML
    private Button newEmailButton;

    @FXML
    private Button replyButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button closeButton;

    /**
     * Open the email message window
     */
    public OpenEmailWindow(String email, String subject, Message msg) throws IOException, SyntaxException {
        String errorMsg = "Please ensure that you are connected to the internet.";
        //get URL
        FXMLLoader fxmlLoader = loadScene(openWindow);
        Parent root = (Parent) fxmlLoader.load();
        fromContent.setText(email);
        subjectContent.setText(subject);
        if (msg != null) {
            setContent(msg);
        } else {
            msgContent.setText(errorMsg);
        }
        puWindow.initModality(Modality.APPLICATION_MODAL);
        puWindow.initStyle(StageStyle.UNDECORATED);
        puWindow.setTitle("Compose Email");
        puWindow.setScene(new Scene(root));
        puWindow.show();
    }

    /**
     * Parses the message content into a string.
     * @param message is the email message packet
     */
    private void setContent (Message message) {
        String content = "";
        try {
            if (message.getContent() instanceof String) {
                content = (String) message.getContent();
                msgContent.setText(content);
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                if (multipart.getCount() > 0) {
                    int i;
                    for (i = 0; i < multipart.getCount(); i++) {
                        content += multipart.getBodyPart(i).getContent().toString();
                    }
                    msgContent.setText(content);
                }
            } else {
                msgContent.setText("This message is in an unsupported format.");
            }
        } catch (IOException e) {
            System.out.println("ioexception");
        } catch (MessagingException e) {
            System.out.println("messaging exception");
        }

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

    /**
     * opens a new email window
     */
    @FXML
    private void openComposeWindow() {
        try {
            ComposeEmailWindow cew = new ComposeEmailWindow("", "", "", "");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    /**
     * opens a new reply email window
     */
    @FXML
    private void openReplyWindow() {
        try {
            ComposeEmailWindow cew = new ComposeEmailWindow("RE: ", this.fromContent.getText(),
                    this.subjectContent.getText(), this.msgContent.getText());
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    /**
     * opens a new forward email window
     */
    @FXML
    private void openForwardWindow() {
        try {
            ComposeEmailWindow cew = new ComposeEmailWindow("FWD: ", this.fromContent.getText(),
                     this.subjectContent.getText(), this.msgContent.getText());
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    @FXML
    private void closeWindow () {
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
