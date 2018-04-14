package seedu.address.logic.commands;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPOINTMENTS;

//@@author XavierMaYuqian
/**
 * Lists all unarchived persons in the address book to the user.
 */
public class ListAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "listappointment";
    public static final String COMMAND_ALIAS = "lap";

    public static final String MESSAGE_SUCCESS = "Listed all appointments";


    @Override
    public CommandResult execute() {
        model.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
