package seedu.address.logic.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.model.GetPersonRequestEvent;
import seedu.address.commons.events.model.ReturnedPersonEvent;
import seedu.address.model.person.Person;

//@@author XavierMaYuqian
/**
 * export employees to a csv file.
 */
public class ExportPersonCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String COMMAND_ALIAS = "ep";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export current person to a csv file.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "All person Exported to person.csv!";

    public static final String MESSAGE_FAIL = "Export fail! Make sure you haven't opened person.csv and try again!";

    public static final String EXPORT_FILE_PATH = "data/person.csv";

    private ObservableList<Person> person;

    private boolean isTest;

    public ExportPersonCommand() {
        registerAsAnEventHandler(this);
    }

    public ExportPersonCommand(boolean isTest) {
        this.isTest = isTest;
        registerAsAnEventHandler(this);
    }

    @Override
    public CommandResult execute() {

        EventsCenter.getInstance().post(new GetPersonRequestEvent());

        File csv;
        if (isTest) {
            csv = new File("person.csv");
        } else {
            csv = new File(EXPORT_FILE_PATH);
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(csv));
            bw.write("Name,Phone,Email,Address,Tags\n");
            for (Person p : person) {
                String temp = p.getName().fullName + "," + p.getPhone().value + "," + p.getEmail().value + ","
                        + p.getAddress().value.replaceAll(",", " ");
                if (!p.getTags().isEmpty()) {
                    temp = temp + "," + p.getTags().toString()
                            .replaceAll(", ", " | ");
                }
                temp = temp + "\n";
                bw.write(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new CommandResult(MESSAGE_FAIL);
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

    protected void registerAsAnEventHandler(Object handler) {
        EventsCenter.getInstance().registerHandler(this);
    }

    @Subscribe
    private void handleReturnedEmployeesEvent(ReturnedPersonEvent event) {
        this.person = event.getEmployees();
    }
}
