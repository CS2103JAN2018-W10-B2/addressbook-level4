# ongkuanyang
###### /java/seedu/address/commons/events/ui/SwitchThemeRequestEvent.java
``` java
package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * An event requesting to view the help page.
 */
public class SwitchThemeRequestEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
```
###### /java/seedu/address/logic/commands/ArchiveCommand.java
``` java
/**
 * Archives a person identified using it's last displayed index from the address book.
 */
public class ArchiveCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "archive";
    public static final String COMMAND_ALIAS = "ar";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Archived Person: %1$s";
    public static final String MESSAGE_PERSON_ALREADY_ARCHIVED = "Person is already archived!";

    private final Index targetIndex;

    private Person personToArchive;

    public ArchiveCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(personToArchive);
        try {
            model.archivePerson(personToArchive);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_UNARCHIVED_PERSONS);
        return new CommandResult(String.format(MESSAGE_ARCHIVE_PERSON_SUCCESS, personToArchive));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToArchive = lastShownList.get(targetIndex.getZeroBased());

        if (personToArchive.isArchived()) {
            throw new CommandException(MESSAGE_PERSON_ALREADY_ARCHIVED);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ArchiveCommand // instanceof handles nulls
                && this.targetIndex.equals(((ArchiveCommand) other).targetIndex) // state check
                && Objects.equals(this.personToArchive, ((ArchiveCommand) other).personToArchive));
    }
}
```
###### /java/seedu/address/logic/commands/UnarchiveCommand.java
``` java
/**
 * Unarchives a person identified using it's last displayed index from the address book.
 */
public class UnarchiveCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "unarchive";
    public static final String COMMAND_ALIAS = "uar";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unarchives the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Unarchived Person: %1$s";
    public static final String MESSAGE_PERSON_ALREADY_UNARCHIVED = "Person is already not archived!";

    private final Index targetIndex;

    private Person personToUnarchive;

    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(personToUnarchive);
        try {
            model.unarchivePerson(personToUnarchive);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_UNARCHIVED_PERSONS);
        return new CommandResult(String.format(MESSAGE_ARCHIVE_PERSON_SUCCESS, personToUnarchive));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToUnarchive = lastShownList.get(targetIndex.getZeroBased());

        if (!personToUnarchive.isArchived()) {
            throw new CommandException(MESSAGE_PERSON_ALREADY_UNARCHIVED);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnarchiveCommand // instanceof handles nulls
                && this.targetIndex.equals(((UnarchiveCommand) other).targetIndex) // state check
                && Objects.equals(this.personToUnarchive, ((UnarchiveCommand) other).personToUnarchive));
    }
}
```
###### /java/seedu/address/logic/commands/AddAppointmentCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMEZONE;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;
import seedu.address.model.appointment.exceptions.DuplicateAppointmentException;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * Adds a person to the address book.
 */
public class AddAppointmentCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "addappointment";
    public static final String COMMAND_ALIAS = "aa";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds an appointment to the address book, persons are identified "
            + "by the index number used in the last person listing. "
            + "Parameters: "
            + "[INDEX (must be a positive integer)]... "
            + PREFIX_NAME + "NAME "
            + PREFIX_DATETIME + "DATETIME "
            + PREFIX_TIMEZONE + "TIMEZONE\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 2 "
            + PREFIX_NAME + "Promote laptop "
            + PREFIX_DATETIME + "2018-06-13 13:25 "
            + PREFIX_TIMEZONE + "America/New_York";

    public static final String MESSAGE_SUCCESS = "New appointment added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This appointment already exists in the address book";

    private Appointment toAdd;
    private final AppointmentName name;
    private final AppointmentTime time;
    private final List<Index> indexes;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddAppointmentCommand(AppointmentName name, AppointmentTime time, List<Index> indexes) {
        requireNonNull(name);
        requireNonNull(time);
        requireNonNull(indexes);
        this.name = name;
        this.time = time;
        this.indexes = indexes;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addAppointment(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateAppointmentException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        UniquePersonList persons = new UniquePersonList();

        for (Index index : indexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            try {
                persons.add(lastShownList.get(index.getZeroBased()));
            } catch (DuplicatePersonException e) {
                // Ignore duplicate
            }
        }

        toAdd = new Appointment(name, time, persons);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddAppointmentCommand // instanceof handles nulls
                && toAdd.equals(((AddAppointmentCommand) other).toAdd)
                && name.equals(((AddAppointmentCommand) other).name)
                && time.equals(((AddAppointmentCommand) other).time)
                && indexes.equals(((AddAppointmentCommand) other).indexes));
    }
}
//@@ author
```
###### /java/seedu/address/logic/commands/DeleteAppointmentCommand.java
``` java
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
```
###### /java/seedu/address/logic/commands/ListAllCommand.java
``` java
package seedu.address.logic.commands;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

/**
 * Lists all persons (including archived) in the address book to the user.
 */
public class ListAllCommand extends Command {

    public static final String COMMAND_WORD = "listall";
    public static final String COMMAND_ALIAS = "la";

    public static final String MESSAGE_SUCCESS = "Listed all persons (including archived persons)";


    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
```
###### /java/seedu/address/logic/commands/SwitchThemeCommand.java
``` java
package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.SwitchThemeRequestEvent;

/**
 * Switches the current theme.
 */
public class SwitchThemeCommand extends Command {

    public static final String COMMAND_WORD = "switch";
    public static final String MESSAGE_SUCCESS = "Theme switched.";

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new SwitchThemeRequestEvent());
        return new CommandResult(MESSAGE_SUCCESS);
    }

}
```
###### /java/seedu/address/logic/parser/ArchiveCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ArchiveCommand object
 */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ArchiveCommand
     * and returns an ArchiveCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ArchiveCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ArchiveCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/logic/parser/UnarchiveCommandParser.java
``` java
/**
 * Parses input arguments and creates a new UnarchiveCommand object
 */
public class UnarchiveCommandParser implements Parser<UnarchiveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnarchiveCommand
     * and returns an UnarchiveCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UnarchiveCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new UnarchiveCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/logic/parser/AddAppointmentCommandParser.java
``` java
package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ArgumentMultimap.arePrefixesPresent;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMEZONE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;

/**
 * Parses input arguments and creates a new AddAppointmentCommand object
 */
public class AddAppointmentCommandParser implements Parser<AddAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddAppointmentCommand
     * and returns an AddAppointmentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATETIME, PREFIX_TIMEZONE);

        List<Index> indexes;

        try {
            indexes = ParserUtil.parseIndexes(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAppointmentCommand.MESSAGE_USAGE));
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATETIME, PREFIX_TIMEZONE)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddAppointmentCommand.MESSAGE_USAGE));
        }

        try {
            AppointmentName name = ParserUtil.parseAppointmentName(argMultimap.getValue(PREFIX_NAME)).get();
            AppointmentTime time = ParserUtil.parseAppointmentTime(argMultimap.getValue(PREFIX_DATETIME),
                    argMultimap.getValue(PREFIX_TIMEZONE)).get();
            return new AddAppointmentCommand(name, time, indexes);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

}
```
###### /java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Parses {@code oneBasedIndexes} into an {@code List<Index>} and returns it.
     * Leading and trailing whitespaces will be trimmed. oneBasedIndexes is a space-separated String of indexes.
     * @throws IllegalValueException if any the specified indexes is invalid (not non-zero unsigned integer).
     */
    public static List<Index> parseIndexes(String oneBasedIndexes) throws IllegalValueException {
        List<Index> result = new ArrayList<>();
        String trimmedIndex = oneBasedIndexes.trim();

        if (trimmedIndex.isEmpty()) {
            return result;
        }

        String[] indexes = trimmedIndex.split("\\s+");

        for (String index : indexes) {
            if (!StringUtil.isNonZeroUnsignedInteger(index)) {
                throw new IllegalValueException(MESSAGE_INVALID_INDEX);
            } else {
                result.add(Index.fromOneBased(Integer.parseInt(index)));
            }
        }

        return result;
    }

    /**
     * Parses a {@code String name} into a {@code AppointmentName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code name} is invalid.
     */
    public static AppointmentName parseAppointmentName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!AppointmentName.isValidName(trimmedName)) {
            throw new IllegalValueException(AppointmentName.MESSAGE_NAME_CONSTRAINTS);
        }
        return new AppointmentName(trimmedName);
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<AppointmentName>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<AppointmentName> parseAppointmentName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(parseAppointmentName(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String datetime} and {@code String timezone} into a {@code AppointmentTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code String datetime} or {@code String timezone} is invalid.
     */
    public static AppointmentTime parseAppointmentTime(String datetime, String timezone) throws IllegalValueException {
        requireNonNull(datetime);
        requireNonNull(timezone);
        String trimmedDatetime = datetime.trim();
        String trimmedTimezone = timezone.trim();

        Pattern pattern = Pattern.compile(DATETIME_REGEX);
        Matcher matcher = pattern.matcher(trimmedDatetime);
        int year;
        int month;
        int day;
        int hour;
        int minute;

        if (matcher.matches()) {
            year = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            day = Integer.parseInt(matcher.group(3));
            hour = Integer.parseInt(matcher.group(4));
            minute = Integer.parseInt(matcher.group(5));
        } else {
            throw new IllegalValueException(MESSAGE_INVALID_DATETIME);
        }

        if (!AppointmentTime.isValidTime(year, month, day, hour, minute, trimmedTimezone)) {
            throw new IllegalValueException(AppointmentTime.MESSAGE_TIME_CONSTRAINTS);
        }

        return new AppointmentTime(year, month, day, hour, minute, trimmedTimezone);
    }

    /**
     * Parses a {@code Optional<String> datetime} and {@code Optional<String> timezone}
     * into an {@code Optional<AppointmentTime>} if {@code datetime} and {@code timezone} are present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<AppointmentTime> parseAppointmentTime(Optional<String> datetime, Optional<String> timezone)
            throws IllegalValueException {
        requireNonNull(datetime);
        requireNonNull(timezone);
        return (datetime.isPresent() && timezone.isPresent())
                ? Optional.of(parseAppointmentTime(datetime.get(), timezone.get()))
                : Optional.empty();
    }

```
###### /java/seedu/address/logic/parser/DeleteAppointmentCommandParser.java
``` java
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteAppointmentCommand object
 */
public class DeleteAppointmentCommandParser implements Parser<DeleteAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteAppointmentCommand
     * and returns an DeleteAppointmentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteAppointmentCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteAppointmentCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAppointmentCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/model/appointment/Appointment.java
``` java
package seedu.address.model.appointment;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Represents an appointment in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Appointment {

    private final AppointmentName name;
    private final AppointmentTime time;

    private final UniquePersonList persons;

    /**
     * Every field must be present and not null.
     */
    public Appointment(AppointmentName name, AppointmentTime time, UniquePersonList persons) {
        requireAllNonNull(name, time);
        this.name = name;
        this.time = time;
        this.persons = persons;
    }


    public AppointmentName getName() {
        return name;
    }

    public AppointmentTime getTime() {
        return time;
    }

    /**
     * Returns an immutable persons list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public ObservableList<Person> getPersons() {
        return persons.asObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Appointment)) {
            return false;
        }

        Appointment otherAppointment = (Appointment) other;
        return otherAppointment.getName().equals(this.getName())
                && otherAppointment.getTime().equals(this.getTime());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, time);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Time: ")
                .append(getTime())
                .append(" Persons: ");
        getPersons().forEach(builder::append);
        return builder.toString();
    }

}
```
###### /java/seedu/address/model/appointment/UniqueAppointmentList.java
``` java
package seedu.address.model.appointment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.model.appointment.exceptions.AppointmentNotFoundException;
import seedu.address.model.appointment.exceptions.DuplicateAppointmentException;

/**
 * A list of appointments that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Appointment#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class UniqueAppointmentList implements Iterable<Appointment> {

    private final ObservableList<Appointment> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent appointment as the given argument.
     */
    public boolean contains(Appointment toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds an appointment to the list.
     *
     * @throws DuplicateAppointmentException if the person to add is a duplicate of an existing person in the list.
     */
    public void add(Appointment toAdd) throws DuplicateAppointmentException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateAppointmentException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the appointment {@code target} in the list with {@code editedAppointment}.
     *
     * @throws DuplicateAppointmentException if the replacement is equivalent
     *         to another existing appointment in the list.
     * @throws AppointmentNotFoundException if {@code target} could not be found in the list.
     */
    public void setAppointment(Appointment target, Appointment editedAppointment)
            throws DuplicateAppointmentException, AppointmentNotFoundException {
        requireNonNull(editedAppointment);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new AppointmentNotFoundException();
        }

        if (!target.equals(editedAppointment) && internalList.contains(editedAppointment)) {
            throw new DuplicateAppointmentException();
        }

        internalList.set(index, editedAppointment);
    }

    /**
     * Removes the equivalent appointment from the list.
     *
     * @throws AppointmentNotFoundException if no such appointment could be found in the list.
     */
    public boolean remove(Appointment toRemove) throws AppointmentNotFoundException {
        requireNonNull(toRemove);
        final boolean appointmentFoundAndDeleted = internalList.remove(toRemove);
        if (!appointmentFoundAndDeleted) {
            throw new AppointmentNotFoundException();
        }
        return appointmentFoundAndDeleted;
    }

    public void setAppointments(UniqueAppointmentList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setAppointments(List<Appointment> appointments) throws DuplicateAppointmentException {
        requireAllNonNull(appointments);
        final UniqueAppointmentList replacement = new UniqueAppointmentList();
        for (final Appointment appointment : appointments) {
            replacement.add(appointment);
        }
        setAppointments(replacement);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Appointment> asObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<Appointment> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueAppointmentList // instanceof handles nulls
                        && this.internalList.equals(((UniqueAppointmentList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
```
###### /java/seedu/address/model/appointment/exceptions/AppointmentNotFoundException.java
``` java
package seedu.address.model.appointment.exceptions;

/**
 * Signals that the operation is unable to find the specified appointment.
 */
public class AppointmentNotFoundException extends Exception {}
```
###### /java/seedu/address/model/appointment/exceptions/DuplicateAppointmentException.java
``` java
package seedu.address.model.appointment.exceptions;

import seedu.address.commons.exceptions.DuplicateDataException;

/**
 * Signals that the operation will result in duplicate Appointment objects.
 */
public class DuplicateAppointmentException extends DuplicateDataException {
    public DuplicateAppointmentException() {
        super("Operation would result in duplicate appointments");
    }
}
```
###### /java/seedu/address/model/appointment/AppointmentName.java
``` java
package seedu.address.model.appointment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an appointment's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class AppointmentName {

    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Appointment names should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String NAME_VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String name;

    /**
     * Constructs a {@code AppointmentName}.
     *
     * @param name A valid name.
     */
    public AppointmentName(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_NAME_CONSTRAINTS);
        this.name = name;
    }

    /**
     * Returns true if a given string is a valid appointment name.
     */
    public static boolean isValidName(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AppointmentName // instanceof handles nulls
                && this.name.equals(((AppointmentName) other).name)); // state check
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
```
###### /java/seedu/address/model/appointment/AppointmentTime.java
``` java
package seedu.address.model.appointment;

import static java.time.ZoneId.getAvailableZoneIds;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an appointment's time in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class AppointmentTime {

    public static final String MESSAGE_TIME_CONSTRAINTS =
            "Year, month, day, hour and minute should constitute a valid date and time. "
            + "Timezone should be of the form '{area}/{city}', such as 'Europe/Paris' or 'America/New_York'";

    public static final String STRING_FORMAT = "d MMM uuuu HH:mm VV";

    public final ZonedDateTime time;

    /**
     * Constructs an {@code AppointmentTime}
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param timezone region IDs of the form '{area}/{city}', such as 'Europe/Paris' or 'America/New_York'
     */
    public AppointmentTime(int year, int month, int dayOfMonth, int hour, int minute, String timezone) {
        requireNonNull(timezone);
        checkArgument(isValidTime(year, month, dayOfMonth, hour, minute, timezone), MESSAGE_TIME_CONSTRAINTS);
        this.time = ZonedDateTime.of(LocalDateTime.of(year, month, dayOfMonth, hour, minute), ZoneId.of(timezone));
    }

    /**
     * Constructs an {@code AppointmentTime}
     *
     * @param string string formatted as per {@code STRING_FORMAT}
     */
    public AppointmentTime(String string) {
        requireNonNull(string);
        checkArgument(isValidTime(string), MESSAGE_TIME_CONSTRAINTS);
        this.time = ZonedDateTime.parse(string, DateTimeFormatter.ofPattern(STRING_FORMAT));
    }

    /**
     * Returns true if a parameters give a valid time and timezone.
     */
    public static boolean isValidTime(int year, int month, int dayOfMonth, int hour, int minute, String timezone) {
        try {
            LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        } catch (DateTimeException e) {
            return false;
        }

        return getAvailableZoneIds().contains(timezone);
    }

    /**
     * Returns true if string gives a valid time and timezone as per {@code STRING_FORMAT}
     */
    public static boolean isValidTime(String string) {
        try {
            ZonedDateTime.parse(string, DateTimeFormatter.ofPattern(STRING_FORMAT));
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return time.format(DateTimeFormatter.ofPattern(STRING_FORMAT));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AppointmentTime // instanceof handles nulls
                && this.time.equals(((AppointmentTime) other).time)); // state check
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }

}
```
###### /java/seedu/address/model/Model.java
``` java
    /** Deletes the given apppointment. */
    void deleteAppointment(Appointment target) throws AppointmentNotFoundException;

    /** Adds the given appointment */
    void addAppointment(Appointment appointment) throws DuplicateAppointmentException;

    /**
     * Replaces the given appointment {@code target} with {@code editedAppointment}.
     *
     * @throws DuplicateAppointmentException if updating the appointment's details
     *      causes the apppointment to be equivalent to another existing appointment in the list.
     * @throws AppointmentNotFoundException if {@code target} could not be found in the list.
     */
    void updateAppointment(Appointment target, Appointment editedAppointment)
            throws DuplicateAppointmentException, AppointmentNotFoundException;
```
###### /java/seedu/address/model/Model.java
``` java
    /** Archives the given person. */
    void archivePerson(Person target) throws PersonNotFoundException;

    /** Unarchive the given person. */
    void unarchivePerson(Person target) throws PersonNotFoundException;
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public synchronized void deleteAppointment(Appointment target) throws AppointmentNotFoundException {
        addressBook.removeAppointment(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addAppointment(Appointment appointment) throws DuplicateAppointmentException {
        addressBook.addAppointment(appointment);
        indicateAddressBookChanged();
    }

    @Override
    public void updateAppointment(Appointment target, Appointment editedAppointment)
            throws DuplicateAppointmentException, AppointmentNotFoundException {
        requireAllNonNull(target, editedAppointment);

        addressBook.updateAppointment(target, editedAppointment);
        indicateAddressBookChanged();
    }
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void archivePerson(Person target) throws PersonNotFoundException {
        addressBook.archivePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public void unarchivePerson(Person target) throws PersonNotFoundException {
        addressBook.unarchivePerson(target);
        indicateAddressBookChanged();
    }
```
###### /java/seedu/address/model/AddressBook.java
``` java

    /**
     * Archives person.
     * @param target
     * @throws PersonNotFoundException
     */
    public void archivePerson(Person target) throws PersonNotFoundException {
        target.setArchived(true);
        try {
            persons.setPerson(target, target);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("Archiving a person only should not result in a duplicate. "
                    + "See Person#equals(Object).");
        }
    }

    /**
     * Unarchives person.
     * @param target
     * @throws PersonNotFoundException
     */
    public void unarchivePerson(Person target) throws PersonNotFoundException {
        target.setArchived(false);
        try {
            persons.setPerson(target, target);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("Unrchiving a person only should not result in a duplicate. "
                    + "See Person#equals(Object).");
        }
    }
```
###### /java/seedu/address/model/AddressBook.java
``` java
    //// appointment-level operations

    /**
     * Adds an appointment to the address book.
     *
     * @throws DuplicateAppointmentException if an equivalent appointment already exists.
     */
    public void addAppointment(Appointment appointment) throws DuplicateAppointmentException {
        appointments.add(appointment);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws AppointmentNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removeAppointment(Appointment key) throws AppointmentNotFoundException {
        if (appointments.remove(key)) {
            return true;
        } else {
            throw new AppointmentNotFoundException();
        }
    }

    /**
     * Replaces the given appointment {@code target} in the list with {@code editedAppointment}.
     *
     * @throws DuplicateAppointmentException if updating the appointment's details
     *      causes the appointment to be equivalent toanother existing appointment in the list.
     * @throws AppointmentNotFoundException if {@code target} could not be found in the list.
     */
    public void updateAppointment(Appointment target, Appointment editedAppointment)
            throws DuplicateAppointmentException, AppointmentNotFoundException {
        requireNonNull(editedAppointment);

        appointments.setAppointment(target, editedAppointment);
    }
```
###### /java/seedu/address/model/AddressBook.java
``` java

    /**
     * Removes a person from all appointments.
     * @param person person to remove
     */
    private void removePersonFromAppointments(Person person) {
        for (Appointment appointment : appointments) {
            UniquePersonList newPersons = new UniquePersonList();

            try {
                newPersons.setPersons(appointment.getPersons());
            } catch (DuplicatePersonException e) {
                throw new AssertionError("Impossible to have duplicate. Persons is from appointment.");
            }

            if (!newPersons.contains(person)) {
                return;
            }

            try {
                newPersons.remove(person);
            } catch (PersonNotFoundException e) {
                throw new AssertionError("Impossible. We just checked the existence of person.");
            }

            Appointment newAppointment = new Appointment(appointment.getName(), appointment.getTime(), newPersons);

            try {
                updateAppointment(appointment, newAppointment);
            } catch (AppointmentNotFoundException e) {
                throw new AssertionError("Impossible. Appointment is in addressbook.");
            } catch (DuplicateAppointmentException e) {
                throw new AssertionError("Impossible. We are modifying an existing appointment's person list.");
            }
        }
    }

```
###### /java/seedu/address/storage/XmlAdaptedAppointment.java
``` java
package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * JAXB-friendly version of the Appointment.
 */
public class XmlAdaptedAppointment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Appointment's %s field is missing!";

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String time;
    @XmlElement
    private List<XmlAdaptedPerson> registered = new ArrayList<>();

    /**
     * Constructs an XmlAdaptedAppointment.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedAppointment() {}

    /**
     * Constructs an {@code XmlAdaptedAppointment} with the given appointment details.
     */
    public XmlAdaptedAppointment(String name, String time, String email, List<XmlAdaptedPerson> registered) {
        this.name = name;
        this.time = time;
        if (registered != null) {
            this.registered = new ArrayList<>(registered);
        }
    }

    /**
     * Converts a given Appointment into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedAppointment
     */
    public XmlAdaptedAppointment(Appointment source) {
        name = source.getName().name;
        time = source.getTime().toString();
        registered = new ArrayList<>();
        for (Person person : source.getPersons()) {
            registered.add(new XmlAdaptedPerson(person));
        }
    }

    /**
     * Converts this jaxb-friendly adapted appointment object into the model's Appointment object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted appointment
     */
    public Appointment toModelType() throws IllegalValueException {
        final UniquePersonList appointmentPersons = new UniquePersonList();
        for (XmlAdaptedPerson person : registered) {
            appointmentPersons.add(person.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!AppointmentName.isValidName(this.name)) {
            throw new IllegalValueException(AppointmentName.MESSAGE_NAME_CONSTRAINTS);
        }
        final AppointmentName name = new AppointmentName(this.name);

        if (this.time == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                        AppointmentTime.class.getSimpleName()));
        }
        if (!AppointmentTime.isValidTime(this.time)) {
            throw new IllegalValueException(AppointmentTime.MESSAGE_TIME_CONSTRAINTS);
        }
        final AppointmentTime time = new AppointmentTime(this.time);

        Appointment appointment = new Appointment(name, time, appointmentPersons);

        return appointment;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedAppointment)) {
            return false;
        }

        XmlAdaptedAppointment otherAppointment = (XmlAdaptedAppointment) other;
        return Objects.equals(name, otherAppointment.name)
                && Objects.equals(time, otherAppointment.time)
                && registered.equals(otherAppointment.registered);
    }
}
```
###### /java/seedu/address/ui/AppointmentCard.java
``` java
package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.appointment.Appointment;

/**
 * An UI component that displays information of an {@code Appointment}.
 */
public class AppointmentCard extends UiPart<Region> {

    private static final String FXML = "AppointmentListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Appointment appointment;

    @FXML
    private HBox appointmentCardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label datetime;
    @FXML
    private FlowPane persons;

    public AppointmentCard(Appointment appointment, int displayedIndex) {
        super(FXML);
        this.appointment = appointment;
        id.setText(displayedIndex + ". ");
        name.setText(appointment.getName().name);
        datetime.setText(appointment.getTime().toString());
        appointment.getPersons().forEach(person -> persons.getChildren().add(new Label(person.getName().fullName)));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentCard)) {
            return false;
        }

        // state check
        AppointmentCard card = (AppointmentCard) other;
        return id.getText().equals(card.id.getText())
                && appointment.equals(card.appointment);
    }
}
```
###### /java/seedu/address/ui/AppointmentListPanel.java
``` java
package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.AppointmentPanelSelectionChangedEvent;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.model.appointment.Appointment;

/**
 * Panel containing the list of appointments.
 */
public class AppointmentListPanel extends UiPart<Region> {
    private static final String FXML = "AppointmentListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(AppointmentListPanel.class);

    @FXML
    private ListView<AppointmentCard> appointmentListView;

    public AppointmentListPanel(ObservableList<Appointment> appointmentList) {
        super(FXML);
        setConnections(appointmentList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<Appointment> appointmentList) {
        ObservableList<AppointmentCard> mappedList = EasyBind.map(
                appointmentList, (appointment) ->
                        new AppointmentCard(appointment, appointmentList.indexOf(appointment) + 1));
        appointmentListView.setItems(mappedList);
        appointmentListView.setCellFactory(listView -> new AppointmentListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        appointmentListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in appointment list panel changed to : '" + newValue + "'");
                        raise(new AppointmentPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code AppointmentCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            appointmentListView.scrollTo(index);
            appointmentListView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code AppointmentCard}.
     */
    class AppointmentListViewCell extends ListCell<AppointmentCard> {

        @Override
        protected void updateItem(AppointmentCard appointment, boolean empty) {
            super.updateItem(appointment, empty);

            if (empty || appointment == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(appointment.getRoot());
            }
        }
    }

}
```
###### /java/seedu/address/ui/MainWindow.java
``` java
    /**
     * Sets the default theme based on UserPrefs
     */
    private void setDefaultTheme(UserPrefs prefs) {
        this.theme = prefs.getGuiSettings().getTheme();
        String fullPath = getClass().getResource(this.theme).toExternalForm();
        primaryStage.getScene().getStylesheets().add(fullPath);
    }
```
###### /java/seedu/address/ui/MainWindow.java
``` java
    /**
     * Switches the current theme
     */
    @FXML
    public void handleSwitchTheme() {
        String fullPath = getClass().getResource(this.theme).toExternalForm();
        primaryStage.getScene().getStylesheets().remove(fullPath);

        if (this.theme.equals(LIGHT_THEME)) {
            this.theme = DARK_THEME;
        } else {
            this.theme = LIGHT_THEME;
        }

        fullPath = getClass().getResource(this.theme).toExternalForm();
        primaryStage.getScene().getStylesheets().add(fullPath);
    }
```
###### /resources/view/AppointmentListCard.fxml
``` fxml

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.FlowPane?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.VBox?>

<HBox id="appointmentCardPane" fx:id="appointmentCardPane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
  <GridPane HBox.hgrow="ALWAYS">
    <columnConstraints>
      <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
    </columnConstraints>
    <VBox alignment="CENTER_LEFT" minHeight="105" GridPane.columnIndex="0">
      <padding>
        <Insets top="5" right="5" bottom="5" left="15" />
      </padding>
      <HBox spacing="5" alignment="CENTER_LEFT">
        <Label fx:id="id" styleClass="cell_big_label">
          <minWidth>
            <!-- Ensures that the label text is never truncated -->
            <Region fx:constant="USE_PREF_SIZE" />
          </minWidth>
        </Label>
        <Label fx:id="name" text="\$first" styleClass="cell_big_label" />
      </HBox>
      <FlowPane fx:id="persons" />
      <Label fx:id="datetime" styleClass="cell_small_label" text="\$phone" />
    </VBox>
  </GridPane>
</HBox>
```
###### /resources/view/AppointmentListPanel.fxml
``` fxml

<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
  <ListView fx:id="appointmentListView" VBox.vgrow="ALWAYS" />
</VBox>
```
