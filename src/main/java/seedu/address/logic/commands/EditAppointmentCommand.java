//@@author ongkuanyang
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMEZONE;
import static seedu.address.logic.parser.ParserUtil.parseAppointmentTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;
import seedu.address.model.appointment.exceptions.AppointmentNotFoundException;
import seedu.address.model.appointment.exceptions.DuplicateAppointmentException;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * Edits the details of an existing appointment in the address book.
 */
public class EditAppointmentCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "editappointment";
    public static final String COMMAND_ALIAS = "ea";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the appointment identified "
            + "by the index number used in the last appointment listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "For person indexes, if person is in appointment, he will be added. Otherwise, he will be removed.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[PERSON INDEX (must be a positive integer)]..."
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_DATETIME + "DATETIME] "
            + "[" + PREFIX_TIMEZONE + "TIMEZONE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "3 4 "
            + PREFIX_NAME + "Sell laptop "
            + PREFIX_DATETIME + "2018-06-13 13:25";

    public static final String MESSAGE_EDIT_APPOINTMENT_SUCCESS = "Edited Appointment: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_APPOINTMENT = "This appointment already exists in the address book.";

    private final Index index;
    private final List<Index> personIndexes;
    private final EditAppointmentDescriptor editAppointmentDescriptor;

    private Appointment appointmentToEdit;
    private Appointment editedAppointment;

    /**
     * @param index of the appointment in the filtered appointment list to edit
     * @param editAppointmentDescriptor details to edit the appointment with
     */
    public EditAppointmentCommand(Index index, EditAppointmentDescriptor editAppointmentDescriptor,
                                  List<Index> personIndexes) {
        requireNonNull(index);
        requireNonNull(editAppointmentDescriptor);
        requireNonNull(personIndexes);

        this.index = index;
        this.editAppointmentDescriptor = new EditAppointmentDescriptor(editAppointmentDescriptor);
        this.personIndexes = personIndexes;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.updateAppointment(appointmentToEdit, editedAppointment);
        } catch (DuplicateAppointmentException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_APPOINTMENT);
        } catch (AppointmentNotFoundException anfe) {
            throw new AssertionError("The target appointment cannot be missing");
        }
        return new CommandResult(String.format(MESSAGE_EDIT_APPOINTMENT_SUCCESS, editedAppointment));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Appointment> lastShownList = model.getFilteredAppointmentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPOINTMENT_DISPLAYED_INDEX);
        }

        appointmentToEdit = lastShownList.get(index.getZeroBased());

        List<Person> lastShownPersonList = model.getFilteredPersonList();
        UniquePersonList personList = new UniquePersonList();

        for (Index index : personIndexes) {
            if (index.getZeroBased() >= lastShownPersonList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            try {
                personList.add(lastShownPersonList.get(index.getZeroBased()));
            } catch (DuplicatePersonException e) {
                // Ignore duplicate
            }
        }

        if (!personIndexes.isEmpty()) {
            editAppointmentDescriptor.setPersons(personList);
        }

        editedAppointment = createEditedAppointment(appointmentToEdit, editAppointmentDescriptor);
    }

    /**
     * Creates and returns a {@code Appointment} with the details of {@code appointmentToEdit}
     * edited with {@code editAppointmentDescriptor}.
     */
    private static Appointment createEditedAppointment(Appointment appointmentToEdit,
                                                       EditAppointmentDescriptor editAppointmentDescriptor)
                                 throws CommandException {
        assert appointmentToEdit != null;

        AppointmentName updatedName = editAppointmentDescriptor.getName().orElse(appointmentToEdit.getName());
        AppointmentTime updatedTime;
        AppointmentTime originalTime = appointmentToEdit.getTime();

        if (!editAppointmentDescriptor.getDateTime().isPresent()
                && !editAppointmentDescriptor.getTimeZone().isPresent()) {
            updatedTime = originalTime;
        } else {

            String dateTime = editAppointmentDescriptor.getDateTime()
                    .orElse(originalTime.time.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")));

            String timeZone = editAppointmentDescriptor.getTimeZone()
                    .orElse(originalTime.time.format(DateTimeFormatter.ofPattern("VV")));

            try {
                updatedTime = parseAppointmentTime(dateTime, timeZone);
            } catch (IllegalValueException ive) {
                throw new CommandException(ive.getMessage());
            }
        }

        UniquePersonList updatedPersons = new UniquePersonList();

        Optional<ObservableList<Person>> optionalPersonList = editAppointmentDescriptor.getPersons();
        if (!optionalPersonList.isPresent()) {
            try {
                updatedPersons.setPersons(appointmentToEdit.getPersons());
            } catch (DuplicatePersonException e) {
                throw new AssertionError("Impossible to have duplicate. Persons are from appointment");
            }
        } else {
            List<Person> original = new ArrayList(appointmentToEdit.getPersons());
            List<Person> newList = optionalPersonList.get();

            for (Person person : newList) {
                if (original.contains(person)) {
                    original.remove(person);
                } else {
                    original.add(person);
                }
            }

            try {
                updatedPersons.setPersons(original);
            } catch (DuplicatePersonException e) {
                // Ignore duplicate
            }
        }

        return new Appointment(updatedName, updatedTime, updatedPersons);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditAppointmentCommand)) {
            return false;
        }

        // state check
        EditAppointmentCommand e = (EditAppointmentCommand) other;
        return index.equals(e.index)
                && editAppointmentDescriptor.equals(e.editAppointmentDescriptor)
                && Objects.equals(appointmentToEdit, e.appointmentToEdit);
    }

    /**
     * Stores the details to edit the appointment with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditAppointmentDescriptor {
        private AppointmentName name;
        private String  dateTime;
        private String timeZone;
        private UniquePersonList persons;

        public EditAppointmentDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code persons} is used internally.
         */
        public EditAppointmentDescriptor(EditAppointmentDescriptor toCopy) {
            setName(toCopy.name);
            setDateTime(toCopy.dateTime);
            setTimeZone(toCopy.timeZone);
            if (!Objects.isNull(toCopy.persons)) {
                UniquePersonList newPersons = new UniquePersonList();
                newPersons.setPersons(toCopy.persons);
                setPersons(newPersons);
            }
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.dateTime, this.timeZone, this.persons);
        }

        public void setName(AppointmentName name) {
            this.name = name;
        }

        public Optional<AppointmentName> getName() {
            return Optional.ofNullable(name);
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public Optional<String> getDateTime() {
            return Optional.ofNullable(dateTime);
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public Optional<String> getTimeZone() {
            return Optional.ofNullable(timeZone);
        }

        public void setPersons(UniquePersonList persons) {
            this.persons = persons;
        }

        public Optional<ObservableList<Person>> getPersons() {
            return Optional.ofNullable(Objects.isNull(persons) ? null : persons.asObservableList());
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditAppointmentDescriptor)) {
                return false;
            }

            // state check
            EditAppointmentDescriptor e = (EditAppointmentDescriptor) other;

            return getName().equals(e.getName())
                    && getDateTime().equals(e.getDateTime())
                    && getTimeZone().equals(e.getTimeZone())
                    && getPersons().equals(e.getPersons());
        }
    }
}
