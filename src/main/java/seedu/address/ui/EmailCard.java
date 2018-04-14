package seedu.address.ui;

import static java.lang.Integer.min;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

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

    private Message msg;

    public EmailCard(Message message) {
        super(FXML);
        String errorMsg = "Please ensure that you are connected to the internet.";
        if (message == null) {
            email.setText("unknown@unknown.com");
            subject.setText("Error: Unable to retrieve message");
            preview.setText(errorMsg);
            this.msg = null;
        } else if (message != null) {
            try {
                email.setText(message.getFrom()[0].toString());
                subject.setText(message.getSubject());
                this.msg = message;
                //check if it is multipart
                if (message.getContent() instanceof String) {
                    String content = (String) message.getContent();
                    preview.setText(((String) message.getContent()).substring(0, min(20, content.length())));
                } else if (message.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) message.getContent();
                    if (multipart.getCount() > 0) {
                        String msg = multipart.getBodyPart(0).getContent().toString();
                        preview.setText(msg.substring(0, min(20, msg.length())));
                    }
                } else if (message.isMimeType("message/rfc822")) {
                    Part part = (Part) message.getContent();
                    try {
                        if (part.getContent() instanceof String) {
                            String content = (String) part.getContent();
                            preview.setText(((String) part.getContent()).substring(0, min(20, content.length())));
                        } else if (part.isMimeType("multipart/*")) {
                            Multipart multipart = (Multipart) part.getContent();
                            if (multipart.getCount() > 0) {
                                String msg = multipart.getBodyPart(0).getContent().toString();
                                preview.setText(msg.substring(0, min(20, msg.length())));
                            }

                        } else {
                            preview.setText("This message is not in a supported format.");
                        }
                    } catch (MessagingException e) {
                        System.out.println("Messaging Exception");
                    } catch (IOException e) {
                        System.out.println("IOException");
                    }
                } else {
                    preview.setText("This message is not in a supported format.");
                }
            } catch (MessagingException e) {
                System.out.println("Messaging Exception");
            } catch (IOException e) {
                System.out.println("IOException");
            }
        }
    }

    /**
     * opens popup window prefilled with email when email from inbox is clicked
     * @throws IOException
     */
    @FXML
    private void openEmail() throws IOException {
        try {
            OpenEmailWindow cew = new OpenEmailWindow(this.email.getText(),
                    this.subject.getText(), this.msg);
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }
}

