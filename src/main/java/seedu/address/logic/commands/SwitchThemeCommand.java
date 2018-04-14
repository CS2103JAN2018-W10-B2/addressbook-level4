//@@author ongkuanyang
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
