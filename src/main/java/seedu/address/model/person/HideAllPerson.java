package seedu.address.model.person;

import seedu.address.commons.util.StringUtil;

import java.util.List;
import java.util.function.Predicate;

import javax.swing.*;

//@@author XavierMaYuqian
public class HideAllPerson implements Predicate<Person>{

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
