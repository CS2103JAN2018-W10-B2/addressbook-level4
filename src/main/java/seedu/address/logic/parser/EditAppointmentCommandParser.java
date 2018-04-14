//@@author ongkuanyang
package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMEZONE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.EditAppointmentCommand.EditAppointmentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditAppointmentCommand object
 */
public class EditAppointmentCommandParser implements Parser<EditAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditAppointmentCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATETIME, PREFIX_TIMEZONE);

        List<Index> indexes;

        try {
            indexes = ParserUtil.parseIndexes(argMultimap.getPreamble());

            if (indexes.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        EditAppointmentCommand.MESSAGE_USAGE));
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditAppointmentCommand.MESSAGE_USAGE));
        }

        EditAppointmentDescriptor editAppointmentDescriptor = new EditAppointmentDescriptor();

        try {
            ParserUtil.parseAppointmentName(argMultimap.getValue(PREFIX_NAME))
                    .ifPresent(editAppointmentDescriptor::setName);
            argMultimap.getValue(PREFIX_DATETIME).ifPresent(editAppointmentDescriptor::setDateTime);
            argMultimap.getValue(PREFIX_TIMEZONE).ifPresent(editAppointmentDescriptor::setTimeZone);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editAppointmentDescriptor.isAnyFieldEdited() && indexes.size() == 1) {
            throw new ParseException(EditAppointmentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditAppointmentCommand(indexes.get(0), editAppointmentDescriptor,
                indexes.subList(1, indexes.size()));
    }
}
