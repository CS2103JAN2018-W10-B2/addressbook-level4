# glorialaw
###### \out\production\resources\view\EmailCard.fxml
``` fxml

<HBox id="cardPane" fx:id="cardPane" xmlns="http://javafx.com/javafx/9.0.1" xmlns:fx="http://javafx.com/fxml/1">
    <GridPane prefHeight="104.0" prefWidth="343.0" HBox.hgrow="ALWAYS">
        <columnConstraints>
            <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
        </columnConstraints>
        <VBox alignment="CENTER_LEFT" minHeight="103.0" prefHeight="104.0" prefWidth="229.0" GridPane.columnIndex="0">
            <padding>
                <Insets bottom="5" left="15" right="5" top="5" />
            </padding>
            <HBox alignment="CENTER_LEFT" prefHeight="20.0" prefWidth="323.0" spacing="5">
                <Label fx:id="email" styleClass="cell_big_label" text="\\$sender">
               <font>
                  <Font name="System Bold" size="14.0" />
               </font></Label>
            </HBox>
            <Label fx:id="subject" prefHeight="17.0" prefWidth="324.0" styleClass="cell_small_label" text="\$subject">
            <font>
               <Font name="System Bold" size="12.0" />
            </font></Label>
         <Label fx:id="preview" prefHeight="51.0" prefWidth="321.0" text="\$preview" wrapText="true" />
        </VBox>
      <rowConstraints>
         <RowConstraints />
      </rowConstraints>
    </GridPane>
</HBox>
```
###### \out\production\resources\view\emailcompose.fxml
``` fxml

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TextArea?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.text.Text?>

<AnchorPane fx:id="emailComposePopup" onKeyPressed="#handleKeyPress" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/9.0.1" xmlns:fx="http://javafx.com/fxml/1">
   <children>
      <GridPane layoutX="-7.0" prefHeight="420.0" prefWidth="600.0">
        <columnConstraints>
          <ColumnConstraints hgrow="SOMETIMES" maxWidth="499.3333206176758" minWidth="10.0" prefWidth="64.33333714803061" />
            <ColumnConstraints hgrow="SOMETIMES" maxWidth="568.6666831970215" minWidth="10.0" prefWidth="462.6666870117187" />
            <ColumnConstraints hgrow="SOMETIMES" maxWidth="568.6666831970215" minWidth="10.0" prefWidth="57.99997965494788" />
        </columnConstraints>
        <rowConstraints>
          <RowConstraints maxHeight="127.99999237060547" minHeight="9.333323001861572" prefHeight="9.333323001861572" vgrow="SOMETIMES" />
          <RowConstraints maxHeight="257.6666769981384" minHeight="3.3333435058594034" prefHeight="28.00001525878909" vgrow="SOMETIMES" />
          <RowConstraints maxHeight="335.6666564941406" minHeight="10.0" prefHeight="310.99998474121094" vgrow="SOMETIMES" />
        </rowConstraints>
         <children>
            <TextField id="recipients" fx:id="recipients" prefHeight="25.0" prefWidth="473.0" promptText="separate recipients with ," GridPane.columnIndex="1">
               <padding>
                  <Insets bottom="5.0" left="5.0" right="5.0" top="5.0" />
               </padding>
            </TextField>
            <Label text="To:" />
            <Label text="Subject:" GridPane.rowIndex="1" />
            <TextField id="subject" fx:id="subject" promptText="enter subject..." GridPane.columnIndex="1" GridPane.rowIndex="1" />
            <TextArea id="message" fx:id="message" prefHeight="200.0" prefWidth="200.0" promptText="your message here..." GridPane.columnIndex="1" GridPane.rowIndex="2" />
            <Label text="Message:" GridPane.rowIndex="2" />
            <Button fx:id="sendButton" defaultButton="true" mnemonicParsing="false" onAction="#sendEmail" text="Send" textAlignment="CENTER" GridPane.columnIndex="2" GridPane.rowIndex="2" />
         </children>
         <padding>
            <Insets bottom="10.0" left="10.0" right="10.0" top="10.0" />
         </padding>
      </GridPane>
      <Text strokeType="OUTSIDE" strokeWidth="0.0" text="Text" />
   </children>
</AnchorPane>
```
###### \out\production\resources\view\EmailPanel.fxml
``` fxml

<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <ListView fx:id="emailListView" VBox.vgrow="ALWAYS" />
</VBox>
```
###### \src\main\java\seedu\address\model\person\CustTimeZone.java
``` java
/**
 * Represents a person's timezone in the address book.
 */

public class CustTimeZone {

    public static final String MESSAGE_TIMEZONE_CONSTRAINTS =
            "Time zones should be have the standard time zone abbreviations, and should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String TIMEZONE_VALIDATION_REGEX = "[^\\s].*";

    //will convert from string to TimeZone when calendar is implemented
    public final String timeZone;

    public CustTimeZone(String timeZone) {
        requireNonNull(timeZone);
        checkArgument(isValidTimeZone(timeZone), MESSAGE_TIMEZONE_CONSTRAINTS);
        this.timeZone = timeZone;
    }

    /**
     * Returns true if a given string is a valid time zone.
     */
    public static boolean isValidTimeZone(String test) {
        return test.matches(TIMEZONE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return timeZone; }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof CustTimeZone
                && this.timeZone.equals(((CustTimeZone) other).timeZone));
    }

    @Override
    public int hashCode() {
        return timeZone.hashCode(); }

}
```
###### \src\main\java\seedu\address\ui\ComposeEmailWindow.java
``` java
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

    /**
     * Creates a new Email compose window
     */
    public ComposeEmailWindow() throws IOException, SyntaxException {
        //get URL
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlFileUrl = MainApp.class.getResource(FXML);
        fxmlLoader.setLocation(fxmlFileUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(null);
        Parent root = (Parent) fxmlLoader.load();

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
    public void sendEmail(){
        new MailServer(parseRecipients(), subject.getText(), message.getText());
        puWindow.close();
    }

    private String[] parseRecipients(){
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
}
```
###### \src\main\java\seedu\address\ui\EmailCard.java
``` java
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
            if( message.getContent() instanceof String ) {
                preview.setText(((String) message.getContent()).substring(0, 20));
            } else {
                Multipart multipart = (Multipart) message.getContent();
                if( multipart.getCount() > 0 ) {
                    String msg =  multipart.getBodyPart(0).getContent().toString();
                    preview.setText(msg);
                }
            }
        } catch ( MessagingException e ) {
            System.out.println("Messaging Exception");
        } catch ( IOException e ) {
            System.out.println("IOException");
        }
    }

}

```
###### \src\main\java\seedu\address\ui\MailPanel.java
``` java
public class MailPanel extends UiPart<Region> {
    private static final String FXML = "EmailPanel.fxml";

    @FXML
    private ListView<EmailCard> emailListView;

    public MailPanel() throws MessagingException, IOException {
        super(FXML);
        setConnections();
        registerAsAnEventHandler(this);
    }

    private void setConnections() {
        //gets the list of messages
        System.out.println("In set Connections");
        ObservableList<EmailCard> emailList = FXCollections.observableArrayList();
        //try {
        Message[] messages = messageList();
        emailList = FXCollections.observableArrayList();
        for (int i = 0; i < messages.length; i++) {
            emailList.add(new EmailCard(messages[i], i));
        }
        emailListView.setItems(emailList);
        emailListView.setCellFactory(listView -> new EmailListViewCell());
    }

    /**
     * Returns a list of messages in the inbox
     * @return list of messages
     */
    public Message[] messageList() {
        String USERNAME = "sell.it.sg@gmail.com";
        String PASSWORD = "gloriacs2103";
        String HOST = "imap.gmail.com";
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", HOST);
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.trust", HOST);
        props.put("mail.imaps.timeout", "10000");

        Session session = Session.getInstance(props);
        IMAPStore store = null;
        Folder inbox = null;

        try {
            store = (IMAPStore) session.getStore("imaps");
            System.out.println("store works");
            store.connect(USERNAME, PASSWORD);
            System.out.println("store connected");
            inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            //gets & returns messages
            System.out.println("right before messages");
            Message[] messages = inbox.getMessages();
            System.out.println("right after messages");
            System.out.println(messages[0].getSubject());
            System.out.println(messages[0].getFrom()[0]);
            return messages;
        } catch (NoSuchProviderException e) {
            System.out.println("NoSuchProviderException");
        } catch (MessagingException e) {
            System.out.println("Caught MessagingException @MailPanel");
        }
        return null;
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
```
###### \src\main\java\seedu\address\ui\MailServer.java
``` java
public class MailServer {
    private static String USERNAME = "sell.it.sg@gmail.com";
    private static String PASSWORD = "gloriacs2103";
    private static String HOST = "smtp.gmail.com";

    public MailServer(String[] recipient, String subject, String msg) {
        sendEmail(recipient, subject, msg);
    }

    /**
     * starts a new smtp session
     */
    public static Session startSession() {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.user", USERNAME);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };
        Session session = Session.getInstance(props, auth);
        return session;
    }

    /**
     * Puts together the email message and sends it
     *
     * @param recipients - the addresses the message is sent to
     * @param subject    - user chooses subject
     * @param msg        - contents the user types
     */
    private static void sendEmail(String[] recipients, String subject, String msg) {
        Session session = startSession();
        //create the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            for (String recipient : recipients) {
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            }
            message.setSubject(subject);
            message.setText(msg);
            //send the message
            Transport.send(message);

        } catch (MessagingException e) {
            System.out.println("Messaging Exception Detected");
            System.out.println(e.getCause());
        }

    }
}
```
###### \src\main\java\seedu\address\ui\PersonCard.java
``` java
    /**
     * opens email sender when email is pushed
     * @throws IOException
     */
    @FXML
    private void writeEmail() throws IOException {
        try{
            ComposeEmailWindow cew = new ComposeEmailWindow();
        } catch ( IOException e ){
            System.out.println("IOException");
        }
    }
}
```
