package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.UniqueAppointmentList;
import seedu.address.model.appointment.exceptions.AppointmentNotFoundException;
import seedu.address.model.appointment.exceptions.DuplicateAppointmentException;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .equals comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueTagList tags;
    private final UniqueAppointmentList appointments;
    private String password;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        tags = new UniqueTagList();
        appointments = new UniqueAppointmentList();
        password = "123456";
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons and Tags in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    public void setPersons(List<Person> persons) throws DuplicatePersonException {
        this.persons.setPersons(persons);
    }

    public void setTags(Set<Tag> tags) {
        this.tags.setTags(tags);
    }

    public void setAppointments(List<Appointment> appointments) throws DuplicateAppointmentException {
        this.appointments.setAppointments(appointments);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setTags(new HashSet<>(newData.getTagList()));
        List<Person> syncedPersonList = newData.getPersonList().stream()
                .map(this::syncWithMasterTagList)
                .collect(Collectors.toList());
        this.password = newData.getPassword();

        try {
            setPersons(syncedPersonList);
            setAppointments(newData.getAppointmentList());
        } catch (DuplicatePersonException e) {
            throw new AssertionError("AddressBooks should not have duplicate persons");
        } catch (DuplicateAppointmentException e) {
            throw new AssertionError("AddressBooks should not have duplicate appointments");
        }
    }

    //// person-level operations

    /**
     * Adds a person to the address book.
     * Also checks the new person's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the person to point to those in {@link #tags}.
     *
     * @throws DuplicatePersonException if an equivalent person already exists.
     */
    public void addPerson(Person p) throws DuplicatePersonException {
        Person person = syncWithMasterTagList(p);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.add(person);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code AddressBook}'s tag list will be updated with the tags of {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTagList(Person)
     */
    public void updatePerson(Person target, Person editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireNonNull(editedPerson);

        Person syncedEditedPerson = syncWithMasterTagList(editedPerson);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.setPerson(target, syncedEditedPerson);
        removeUnusedTags();
    }

    //@@author XavierMaYuqian
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

    //@@author ongkuanyang

    /**
     * Archives person.
     * @param target
     * @throws PersonNotFoundException
     */
    public void archivePerson(Person target) throws PersonNotFoundException {
        target.setArchived(true);
        try {
            persons.setPerson(target, target);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("Archiving a person only should not result in a duplicate. "
                    + "See Person#equals(Object).");
        }
    }

    /**
     * Unarchives person.
     * @param target
     * @throws PersonNotFoundException
     */
    public void unarchivePerson(Person target) throws PersonNotFoundException {
        target.setArchived(false);
        try {
            persons.setPerson(target, target);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("Unrchiving a person only should not result in a duplicate. "
                    + "See Person#equals(Object).");
        }
    }
    //@@author

    /**
     *  Updates the master tag list to include tags in {@code person} that are not in the list.
     *  @return a copy of this {@code person} such that every tag in this person points to a Tag object in the master
     *  list.
     */
    private Person syncWithMasterTagList(Person person) {
        final UniqueTagList personTags = new UniqueTagList(person.getTags());
        tags.mergeFrom(personTags);

        // Create map with values = tag object references in the master list
        // used for checking person tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of person tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        personTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return new Person(
                person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getCustTimeZone(), person.getComment(),
                person.isArchived(), correctTagReferences);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws PersonNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removePerson(Person key) throws PersonNotFoundException {
        if (persons.remove(key)) {
            removePersonFromAppointments(key);
            return true;
        } else {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Sorts all the persons alphabetical order of their names
     */
    public void sort() {
        requireNonNull(persons);
        persons.sort();
    }

    //@@author ongkuanyang
    //// appointment-level operations

    /**
     * Adds an appointment to the address book.
     *
     * @throws DuplicateAppointmentException if an equivalent appointment already exists.
     */
    public void addAppointment(Appointment appointment) throws DuplicateAppointmentException {
        appointments.add(appointment);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws AppointmentNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removeAppointment(Appointment key) throws AppointmentNotFoundException {
        if (appointments.remove(key)) {
            return true;
        } else {
            throw new AppointmentNotFoundException();
        }
    }

    /**
     * Replaces the given appointment {@code target} in the list with {@code editedAppointment}.
     *
     * @throws DuplicateAppointmentException if updating the appointment's details
     *      causes the appointment to be equivalent toanother existing appointment in the list.
     * @throws AppointmentNotFoundException if {@code target} could not be found in the list.
     */
    public void updateAppointment(Appointment target, Appointment editedAppointment)
            throws DuplicateAppointmentException, AppointmentNotFoundException {
        requireNonNull(editedAppointment);

        appointments.setAppointment(target, editedAppointment);
    }
    //@@author

    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //@@author XavierMaYuqian
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

    //@@author XavierMaYuqian
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


    //@@author XavierMaYuqian
    @Override
    public String getPassword() {
        return password;
    }

    //@@author XavierMaYuqian
    public void setPassword(String password) {
        this.password = password;
    }


    //@@author ongkuanyang

    /**
     * Removes a person from all appointments.
     * @param person person to remove
     */
    private void removePersonFromAppointments(Person person) {
        for (Appointment appointment : appointments) {
            UniquePersonList newPersons = new UniquePersonList();

            try {
                newPersons.setPersons(appointment.getPersons());
            } catch (DuplicatePersonException e) {
                throw new AssertionError("Impossible to have duplicate. Persons is from appointment.");
            }

            if (!newPersons.contains(person)) {
                return;
            }

            try {
                newPersons.remove(person);
            } catch (PersonNotFoundException e) {
                throw new AssertionError("Impossible. We just checked the existence of person.");
            }

            Appointment newAppointment = new Appointment(appointment.getName(), appointment.getTime(), newPersons);

            try {
                updateAppointment(appointment, newAppointment);
            } catch (AppointmentNotFoundException e) {
                throw new AssertionError("Impossible. Appointment is in addressbook.");
            } catch (DuplicateAppointmentException e) {
                throw new AssertionError("Impossible. We are modifying an existing appointment's person list.");
            }
        }
    }

    //@@author

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
