package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Scanner;

import seedu.address.logic.commands.SetPasswordCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author XavierMaYuqian
/**
 * Parses arguments for the SetPasswordCommand'
 */
public class SetPasswordCommandParser implements Parser<SetPasswordCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SetPasswordCommand
     * and returns an SetPasswordCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SetPasswordCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        Scanner sc = new Scanner(trimmedArgs);
        if (!sc.hasNext()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetPasswordCommand.MESSAGE_USAGE));
        }
        String oldPsw = sc.next();
        if (!sc.hasNext()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetPasswordCommand.MESSAGE_USAGE));
        }
        String newPsw = sc.next();

        return new SetPasswordCommand(oldPsw, newPsw);
    }
}
