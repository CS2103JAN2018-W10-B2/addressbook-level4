package seedu.address.ui;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * the card for the email panel
 */
//@@author glorialaw
public class EmailCard extends UiPart<Region> {
    private static final String FXML = "EmailCard.fxml";

    @FXML
    private HBox cardPane;

    @FXML
    private Label email;

    @FXML
    private Label subject;

    @FXML
    private Label preview;

    public EmailCard(Message message, int index) {
        super(FXML);
        try {
            email.setText(message.getFrom()[0].toString());
            subject.setText(message.getSubject());
            //check if it is multipart
            if (message.getContent() instanceof String) {
                preview.setText(((String) message.getContent()).substring(0, 20));
            } else {
                Multipart multipart = (Multipart) message.getContent();
                if (multipart.getCount() > 0) {
                    String msg =  multipart.getBodyPart(0).getContent().toString();
                    preview.setText(msg);
                }
            }
        } catch (MessagingException e) {
            System.out.println("Messaging Exception");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

}

