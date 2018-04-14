package seedu.address.model.person;

import java.util.function.Predicate;

//@@author XavierMaYuqian
/**
 * HideAllPerson
 */
public class HideAllPerson implements Predicate<Person> {

    public HideAllPerson() {}

    @Override
    public boolean test(Person person) {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

}
