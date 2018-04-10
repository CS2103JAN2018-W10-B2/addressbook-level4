//@@author ongkuanyang
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.exceptions.AppointmentNotFoundException;

/**
 * Deletes an appointment identified using it's last displayed index from the address book.
 */
public class DeleteAppointmentCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "deleteappointment";
    public static final String COMMAND_ALIAS = "da";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the appointment identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_APPOINTMENT_SUCCESS = "Deleted appointment: %1$s";

    private final Index targetIndex;

    private Appointment appointmentToDelete;

    public DeleteAppointmentCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(appointmentToDelete);
        try {
            model.deleteAppointment(appointmentToDelete);
        } catch (AppointmentNotFoundException anfe) {
            throw new AssertionError("The target appointment cannot be missing");
        }

        return new CommandResult(String.format(MESSAGE_DELETE_APPOINTMENT_SUCCESS, appointmentToDelete));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Appointment> lastShownList = model.getFilteredAppointmentList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPOINTMENT_DISPLAYED_INDEX);
        }

        appointmentToDelete = lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteAppointmentCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteAppointmentCommand) other).targetIndex) // state check
                && Objects.equals(this.appointmentToDelete, ((DeleteAppointmentCommand) other).appointmentToDelete));
    }
}
//@@author
