# XavierMaYuqian
###### /java/seedu/address/logic/commands/DeleteTagCommand.java
``` java
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
```
###### /java/seedu/address/logic/parser/DeleteTagCommandParser.java
``` java
/**
 * Parses input arguments and creates a new DeleteTagCommand object
 */
public class DeleteTagCommandParser implements Parser<DeleteTagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteTagCommand
     * and returns an DeleteTagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteTagCommand parse(String args) throws ParseException {
        try {
            Tag t = ParserUtil.parseTag(args);
            return new DeleteTagCommand(t);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTagCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/model/tag/TagNotFoundException.java
``` java
/**
 * Signals that the operation is unable to find the specified tag.
 */
public class TagNotFoundException extends Exception {}
```
###### /java/seedu/address/model/Model.java
``` java
    /** Removes the given {@code tag} from all {@code Person}s. */
    void deleteTag(Tag t);

}
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void deleteTag(Tag t) {
        addressBook.removeTag(t);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Filtered Appointments List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Appointment} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Appointment> getFilteredAppointmentList() {
        return FXCollections.unmodifiableObservableList(filteredAppointments);
    }

    @Override
    public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
        requireNonNull(predicate);
        filteredAppointments.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredPersons.equals(other.filteredPersons)
                && filteredAppointments.equals(other.filteredAppointments);
    }

}
```
###### /java/seedu/address/model/AddressBook.java
``` java
    /**
     * Removes all {@code Tag}s that are not used by any {@code Person} in this {@code AddressBook}.
     */
    private void removeUnusedTags() {
        Set<Tag> tagsInPersons = persons.asObservableList().stream()
                .map(Person::getTags)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        tags.setTags(tagsInPersons);
    }

```
###### /java/seedu/address/model/AddressBook.java
``` java
    /**
     * Removes tags from persons
     */
    public void removeTag(Tag t) {
        try {
            for (Person person : persons) {
                removeTagFromPerson(t, person);
            }
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("Impossible: original person is obtained from the address book.");
        }
    }

```
###### /java/seedu/address/model/AddressBook.java
``` java
    /**
     * Removes tags from persons
     */
    private void removeTagFromPerson(Tag t, Person person) throws PersonNotFoundException {
        Set < Tag > newTags = new HashSet<>(person.getTags());

        if (!newTags.remove(t)) {
            return;
        }

        Person newPerson = new Person(person.getName(), person.getPhone(), person.getEmail(),
                                      person.getAddress(), person.getCustTimeZone(), newTags);

        try {
            updatePerson(person, newPerson);
        } catch (DuplicatePersonException dpe) {
            throw new AssertionError("Modifying a person's tags only should not result in a duplicate. "
                     + "See Person#equals(Object).");
        }
    }

```
