//@@author ongkuanyang
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
