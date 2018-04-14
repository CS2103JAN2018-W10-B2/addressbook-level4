package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteTagCommand;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportPersonCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListAllCommand;
import seedu.address.logic.commands.ListAppointmentCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.LockCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.SetPasswordCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SwitchThemeCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UnlockCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD: case AddCommand.COMMAND_ALIAS:
            return new AddCommandParser().parse(arguments);

        case AddAppointmentCommand.COMMAND_WORD: case AddAppointmentCommand.COMMAND_ALIAS:
            return new AddAppointmentCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD: case EditCommand.COMMAND_ALIAS:
            return new EditCommandParser().parse(arguments);

        case EditAppointmentCommand.COMMAND_WORD: case EditAppointmentCommand.COMMAND_ALIAS:
            return new EditAppointmentCommandParser().parse(arguments);

        case SelectCommand.COMMAND_WORD: case SelectCommand.COMMAND_ALIAS:
            return new SelectCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD: case DeleteCommand.COMMAND_ALIAS:
            return new DeleteCommandParser().parse(arguments);

        case DeleteTagCommand.COMMAND_WORD: case DeleteTagCommand.COMMAND_ALIAS:
            return new DeleteTagCommandParser().parse(arguments);

        case DeleteAppointmentCommand.COMMAND_WORD: case DeleteAppointmentCommand.COMMAND_ALIAS:
            return new DeleteAppointmentCommandParser().parse(arguments);

        case ArchiveCommand.COMMAND_WORD: case ArchiveCommand.COMMAND_ALIAS:
            return new ArchiveCommandParser().parse(arguments);

        case UnarchiveCommand.COMMAND_WORD: case UnarchiveCommand.COMMAND_ALIAS:
            return new UnarchiveCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD: case ClearCommand.COMMAND_ALIAS:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD: case FindCommand.COMMAND_ALIAS:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD: case ListCommand.COMMAND_ALIAS:
            return new ListCommand();

        case ListAllCommand.COMMAND_WORD: case ListAllCommand.COMMAND_ALIAS:
            return new ListAllCommand();

        case ListAppointmentCommand.COMMAND_WORD: case ListAppointmentCommand.COMMAND_ALIAS:
            return new ListAppointmentCommand();

        case SortCommand.COMMAND_WORD: case SortCommand.COMMAND_ALIAS:
            return new SortCommand();

        case HistoryCommand.COMMAND_WORD: case HistoryCommand.COMMAND_ALIAS:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case SwitchThemeCommand.COMMAND_WORD:
            return new SwitchThemeCommand();

        case UndoCommand.COMMAND_WORD: case UndoCommand.COMMAND_ALIAS:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD: case RedoCommand.COMMAND_ALIAS:
            return new RedoCommand();

        case LockCommand.COMMAND_WORD: case LockCommand.COMMAND_ALIAS:
            return new LockCommand();

        case UnlockCommand.COMMAND_WORD: case UnlockCommand.COMMAND_ALIAS:
            return new UnlockCommandParser().parse(arguments);

        case SetPasswordCommand.COMMAND_WORD: case SetPasswordCommand.COMMAND_ALIAS:
            return new SetPasswordCommandParser().parse(arguments);

        case ExportPersonCommand.COMMAND_WORD: case ExportPersonCommand.COMMAND_ALIAS:
            return new ExportPersonCommand();

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
