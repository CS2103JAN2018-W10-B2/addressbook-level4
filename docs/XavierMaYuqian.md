# XavierMaYuqian
###### \main\java\seedu\address\logic\commands\DeleteTagCommand.java
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
###### \main\java\seedu\address\logic\parser\DeleteTagCommandParser.java
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
###### \main\java\seedu\address\model\AddressBook.java
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
###### \main\java\seedu\address\model\AddressBook.java
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
###### \main\java\seedu\address\model\AddressBook.java
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
                person.getAddress(), person.getCustTimeZone(), person.getComment(), newTags);

        try {
            updatePerson(person, newPerson);
        } catch (DuplicatePersonException dpe) {
            throw new AssertionError("Modifying a person's tags only should not result in a duplicate. "
                     + "See Person#equals(Object).");
        }
    }

    //// util methods

    @Override
    public String toString() {
        return persons.asObservableList().size() + " persons, " + tags.asObservableList().size() +  " tags";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asObservableList();
    }

    @Override
    public ObservableList<Appointment> getAppointmentList() {
        return appointments.asObservableList();
    }

    @Override
    public ObservableList<Tag> getTagList() {
        return tags.asObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && this.persons.equals(((AddressBook) other).persons)
                && this.tags.equalsOrderInsensitive(((AddressBook) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(persons, tags);
    }
}
```
###### \main\java\seedu\address\model\Model.java
``` java
    /** Removes the given {@code tag} from all {@code Person}s. */
    void deleteTag(Tag t);

}
```
###### \main\java\seedu\address\model\ModelManager.java
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
###### \main\java\seedu\address\model\tag\TagNotFoundException.java
``` java
/**
 * Signals that the operation is unable to find the specified tag.
 */
public class TagNotFoundException extends Exception {}
```
###### \test\java\seedu\address\logic\commands\AddCommandTest.java
``` java
        @Override
        public void deleteTag(Tag tag) {
            fail("This method should not be called.");
        }

        @Override
        public void archivePerson(Person target) {
            fail("This method should not be called.");
        }

        @Override
        public void unarchivePerson(Person target) {
            fail("This method should not be called.");
        }
    }

    /**
     * A Model stub that always throw a DuplicatePersonException when trying to add a person.
     */
    private class ModelStubThrowingDuplicatePersonException extends ModelStub {
        @Override
        public void addPerson(Person person) throws DuplicatePersonException {
            throw new DuplicatePersonException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public void addPerson(Person person) throws DuplicatePersonException {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
```
###### \test\java\seedu\address\model\AddressBookTest.java
``` java
    @Test
    public void updatePersonDetailsChangedPersonsAndTagsListUpdated() throws Exception {
        AddressBook addressBookUpdatedToAmy = new AddressBookBuilder().withPerson(BOB).build();
        addressBookUpdatedToAmy.updatePerson(BOB, AMY);

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(AMY).build();

        assertEquals(expectedAddressBook, addressBookUpdatedToAmy);
    }

```
###### \test\java\seedu\address\model\AddressBookTest.java
``` java
    @Test
    public void removeTagNonExistentTagAddressBookUnchanged() throws Exception {
        addressBookWithBobAndAmy.removeTag(new Tag(VALID_TAG_UNUSED));

        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(BOB).withPerson(AMY).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

```
###### \test\java\seedu\address\model\AddressBookTest.java
``` java
    @Test
    public void removeTagTagUsedByMultiplePersonsTagRemoved() throws Exception {
        addressBookWithBobAndAmy.removeTag(new Tag(VALID_TAG_FRIEND));

        Person amyWithoutFriendTag = new PersonBuilder(AMY).withTags().build();
        Person bobWithoutFriendTag = new PersonBuilder(BOB).withTags(VALID_TAG_HUSBAND).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(bobWithoutFriendTag)
                .withPerson(amyWithoutFriendTag).build();

        assertEquals(expectedAddressBook, addressBookWithBobAndAmy);
    }

    /**
     * A stub ReadOnlyAddressBook whose persons and tags lists can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Tag> tags = FXCollections.observableArrayList();
        private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();


        AddressBookStub(Collection<Person> persons, Collection<? extends Tag> tags,
                        Collection<Appointment> appointments) {
            this.persons.setAll(persons);
            this.tags.setAll(tags);
            this.appointments.setAll(appointments);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Appointment> getAppointmentList() {
            return appointments;
        }

        @Override
        public ObservableList<Tag> getTagList() {
            return tags;
        }
    }

}
```
###### \test\java\seedu\address\model\ModelManagerTest.java
``` java
    @Test
    public void deleteTagNonExistentTagModelUnchanged() throws Exception {
        AddressBook addressBook = new AddressBookBuilder().withPerson(AMY).withPerson(BOB).build();
        UserPrefs userPrefs = new UserPrefs();

        ModelManager modelManager = new ModelManager(addressBook, userPrefs);
        modelManager.deleteTag(new Tag(VALID_TAG_UNUSED));

        assertEquals(new ModelManager(addressBook, userPrefs), modelManager);
    }

```
###### \test\java\seedu\address\model\ModelManagerTest.java
``` java
    @Test
    public void deleteTagTagUsedByMultiplePersonsTagRemoved() throws Exception {
        AddressBook addressBook = new AddressBookBuilder().withPerson(AMY).withPerson(BOB).build();
        UserPrefs userPrefs = new UserPrefs();

        ModelManager modelManager = new ModelManager(addressBook, userPrefs);
        modelManager.deleteTag(new Tag(VALID_TAG_FRIEND));

        Person amyWithoutFriendTag = new PersonBuilder(AMY).withTags().build();
        Person bobWithoutFriendTag = new PersonBuilder(BOB).withTags(VALID_TAG_HUSBAND).build();
        AddressBook expectedAddressBook = new AddressBookBuilder().withPerson(amyWithoutFriendTag)
                .withPerson(bobWithoutFriendTag).build();

        assertEquals(new ModelManager(expectedAddressBook, userPrefs), modelManager);
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        ModelManager modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_UNARCHIVED_PERSONS);

        // different userPrefs -> returns true
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookName("differentName");
        assertTrue(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
```
