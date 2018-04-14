package seedu.address.ui;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;

/**
 * Mail panel
 */
//@@author glorialaw
public class MailPanel extends UiPart<VBox> {
    private static final long freq = 60;
    private static final String FXML = "EmailPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(MailPanel.class);
    private final ObservableList<EmailCard> emailList = FXCollections.observableArrayList();
    private int messageCount = 0;
    private final IMAPFolder inbox = getInbox();

    @FXML
    private Label loading;
    @FXML
    private ListView<EmailCard> emailListView;

    public MailPanel() throws MessagingException, IOException {
        super(FXML);
        Task<Void> fetchEmails = new Task<Void>() {
            @Override
            protected Void call() {
                setConnections();
                return null;
            }

            @Override
            protected void succeeded() {
                MailPanel.this.getRoot().getChildren().remove(loading);
            }

        };

        new Thread(fetchEmails).start();
        ScheduledExecutorService runChecks = Executors.newScheduledThreadPool(1);
        Runnable rc = new Runnable() {
            @Override
            public void run() {
                Platform.setImplicitExit(false);
                refreshMessages();
            }
        };
        runChecks.scheduleAtFixedRate(rc, freq, freq, TimeUnit.SECONDS);
        registerAsAnEventHandler(this);
    }

    private void setConnections() {
        loading.setText("Loading Emails...");
        //gets the list of messages
        int length = 0;
        Message[] messages = messageList();
        if (messages != null) {
            length = messages.length;
            messageCount = length;
            for (int i = 0; i < length; i++) {
                emailList.add(new EmailCard(messages[i]));
            }
        } else if (messages == null) {
            emailList.add(new EmailCard(null));
        }
        FXCollections.reverse(emailList);
        emailListView.setItems(emailList);
        emailListView.setCellFactory(listView -> new EmailListViewCell());

    }

    /**
     * Returns a list of messages in the inbox
     * @return list of messages
     */
    public Message[] messageList() {
        if (inbox != null) {
            try {
                inbox.open(Folder.READ_ONLY);
                //gets & returns messages
                Message[] messages = inbox.getMessages();
                inbox.addMessageCountListener(new MessageCountAdapter() {
                    public void messagesAdded(MessageCountEvent mce) {
                        //System.out.println("Listener finally worked");
                        Platform.runLater(() -> {
                            Message[] newMessages = mce.getMessages();
                            FXCollections.reverse(emailList);
                            for (Message message : newMessages) {
                                emailList.add(new EmailCard(message));
                            }
                            FXCollections.reverse(emailList);
                        });
                    }
                });
                return messages;
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                return null;
            } catch (MessagingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Refreshes the inbox so that the contents can be updated
     * @return
     */
    @FXML
    private void refreshMessages() {
        //System.out.println("Inside refresh messages");
        try {
            if (!inbox.isOpen()) {
                inbox.open(Folder.READ_ONLY);
                inbox.idle();
            }
        } catch (MessagingException me) {
            me.getCause();
        }

    }

    private IMAPFolder getInbox() {
        String username = "sg.salesperson@gmail.com";
        String password = "gloriacs2103";
        String host = "imap.gmail.com";
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.trust", host);
        props.put("mail.imaps.timeout", "10000");

        Session session = Session.getInstance(props);
        IMAPStore store = null;
        Folder inbox = null;

        try {
            store = (IMAPStore) session.getStore("imaps");
            store.connect(username, password);
            inbox = (IMAPFolder) store.getFolder("INBOX");
            return (IMAPFolder) inbox;
        } catch (NoSuchProviderException e) {
            System.out.println("NoSuchProviderException");
            return null;
        } catch (MessagingException e) {
            System.out.println("Caught MessagingException @MailPanel");
            System.out.println(e.getCause());
            return null;
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code EmailCard}.
     */
    class EmailListViewCell extends ListCell<EmailCard> {

        @Override
        protected void updateItem(EmailCard email, boolean empty) {
            super.updateItem(email, empty);

            if (empty || email == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(email.getRoot());
            }
        }
    }

}
