package seedu.address.logic.commands;

import seedu.address.logic.LogicManager;

//@@author XavierMaYuqian
/**
 * Set the application password
 * */
public class SetPasswordCommand extends Command {

    public static final String COMMAND_WORD = "setPassword";

    public static final String COMMAND_ALIAS = "sp";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Set new application password, and old "
            + "password requeired."
            + "Parameters: "
            + "oldPassword" + " newPassword ";

    public static final String MESSAGE_SUCCESS = "New password has been set!";

    public static final String MESSAGE_INCORRECT_OLDPASSWORD = "Incorrect old password!";

    private String oldPassword;

    private String newPassword;

    public SetPasswordCommand(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    public CommandResult execute() {
        if (this.oldPassword.compareTo(LogicManager.getPassword()) == 0) {
            LogicManager.setPassword(this.newPassword);
            model.setPassword(this.newPassword);
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            return new CommandResult(MESSAGE_INCORRECT_OLDPASSWORD);
        }
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SetPasswordCommand // instanceof handles nulls
                && this.oldPassword.equals(((SetPasswordCommand) other).getOldPassword())
                && this.newPassword.equals(((SetPasswordCommand) other).getNewPassword())); // state check
    }
}
