package seedu.address.logic.commands;
import static java.util.Objects.requireNonNull;

//@@author XavierMaYuqian
/**
 * Sorts all persons in the address book based on alphabetical order of their names.
 */
public class SortCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "sort";
    public static final String COMMAND_ALIAS = "so";

    public static final String MESSAGE_SUCCESS = "Sorted successfully";

    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(model);
        model.sort();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
