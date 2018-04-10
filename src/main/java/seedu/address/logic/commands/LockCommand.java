package seedu.address.logic.commands;

import seedu.address.logic.LogicManager;
import seedu.address.model.person.HideAllPerson;


/**
 * Locks the app with a password
 * */
public class LockCommand extends Command {

    public static final String COMMAND_WORD = "lock";

    public static final String COMMAND_ALIAS = "lk";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lock the address book. ";

    public static final String MESSAGE_SUCCESS = "Address book has been locked!";

    private final HideAllPerson predicate = new HideAllPerson();

    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(predicate);
        LogicManager.lock();

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
