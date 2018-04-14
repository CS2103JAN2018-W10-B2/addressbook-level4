package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.tag.Tag;

//@@author XavierMaYuqian
/**
 * Delete certain tags in the address book.
 */
public class DeleteTagCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "deletetag";
    public static final String COMMAND_ALIAS = "dt";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the tag identified by its name.\n"
            + "Parameters: Tag name (case sensitive)\n"
            + "Example: " + COMMAND_WORD + " friends";

    public static final String MESSAGE_SUCCESS = "Tags deleted successfully";

    private Tag tagToBeDeleted;

    public DeleteTagCommand(Tag t) {
        this.tagToBeDeleted = t;
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        ObservableList<Tag> list = model.getAddressBook().getTagList();

        if (!list.contains(tagToBeDeleted)) {
            throw new CommandException(Messages.MESSAGE_INVALID_TAG);
        }
    }

    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(model);
        model.deleteTag(tagToBeDeleted);

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
