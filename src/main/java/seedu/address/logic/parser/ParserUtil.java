package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;
import seedu.address.model.person.Address;
import seedu.address.model.person.Comment;
import seedu.address.model.person.CustTimeZone;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 * {@code ParserUtil} contains methods that take in {@code Optional} as parameters. However, it goes against Java's
 * convention (see https://stackoverflow.com/a/39005452) as {@code Optional} should only be used a return type.
 * Justification: The methods in concern receive {@code Optional} return values from other methods as parameters and
 * return {@code Optional} values based on whether the parameters were present. Therefore, it is redundant to unwrap the
 * initial {@code Optional} before passing to {@code ParserUtil} as a parameter and then re-wrap it into an
 * {@code Optional} return value inside {@code ParserUtil} methods.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INSUFFICIENT_PARTS = "Number of parts must be more than 1.";
    public static final String MESSAGE_INVALID_DATETIME = "Datetime is not of the format YYYY-MM-DD HH:MM.";
    public static final String DATETIME_REGEX = "(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws IllegalValueException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    //@@author ongkuanyang
    /**
     * Parses {@code oneBasedIndexes} into an {@code List<Index>} and returns it.
     * Leading and trailing whitespaces will be trimmed. oneBasedIndexes is a space-separated String of indexes.
     * @throws IllegalValueException if any the specified indexes is invalid (not non-zero unsigned integer).
     */
    public static List<Index> parseIndexes(String oneBasedIndexes) throws IllegalValueException {
        List<Index> result = new ArrayList<>();
        String trimmedIndex = oneBasedIndexes.trim();

        if (trimmedIndex.isEmpty()) {
            return result;
        }

        String[] indexes = trimmedIndex.split("\\s+");

        for (String index : indexes) {
            if (!StringUtil.isNonZeroUnsignedInteger(index)) {
                throw new IllegalValueException(MESSAGE_INVALID_INDEX);
            } else {
                result.add(Index.fromOneBased(Integer.parseInt(index)));
            }
        }

        return result;
    }

    /**
     * Parses a {@code String name} into a {@code AppointmentName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code name} is invalid.
     */
    public static AppointmentName parseAppointmentName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!AppointmentName.isValidName(trimmedName)) {
            throw new IllegalValueException(AppointmentName.MESSAGE_NAME_CONSTRAINTS);
        }
        return new AppointmentName(trimmedName);
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<AppointmentName>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<AppointmentName> parseAppointmentName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(parseAppointmentName(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String datetime} and {@code String timezone} into a {@code AppointmentTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code String datetime} or {@code String timezone} is invalid.
     */
    public static AppointmentTime parseAppointmentTime(String datetime, String timezone) throws IllegalValueException {
        requireNonNull(datetime);
        requireNonNull(timezone);
        String trimmedDatetime = datetime.trim();
        String trimmedTimezone = timezone.trim();

        Pattern pattern = Pattern.compile(DATETIME_REGEX);
        Matcher matcher = pattern.matcher(trimmedDatetime);
        int year;
        int month;
        int day;
        int hour;
        int minute;

        if (matcher.matches()) {
            year = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            day = Integer.parseInt(matcher.group(3));
            hour = Integer.parseInt(matcher.group(4));
            minute = Integer.parseInt(matcher.group(5));
        } else {
            throw new IllegalValueException(MESSAGE_INVALID_DATETIME);
        }

        if (!AppointmentTime.isValidTime(year, month, day, hour, minute, trimmedTimezone)) {
            throw new IllegalValueException(AppointmentTime.MESSAGE_TIME_CONSTRAINTS);
        }

        return new AppointmentTime(year, month, day, hour, minute, trimmedTimezone);
    }

    /**
     * Parses a {@code Optional<String> datetime} and {@code Optional<String> timezone}
     * into an {@code Optional<AppointmentTime>} if {@code datetime} and {@code timezone} are present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<AppointmentTime> parseAppointmentTime(Optional<String> datetime, Optional<String> timezone)
            throws IllegalValueException {
        requireNonNull(datetime);
        requireNonNull(timezone);
        return (datetime.isPresent() && timezone.isPresent())
                ? Optional.of(parseAppointmentTime(datetime.get(), timezone.get()))
                : Optional.empty();
    }

    //@@author

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new IllegalValueException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<Name>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Name> parseName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(parseName(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws IllegalValueException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new IllegalValueException(Phone.MESSAGE_PHONE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code Optional<String> phone} into an {@code Optional<Phone>} if {@code phone} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Phone> parsePhone(Optional<String> phone) throws IllegalValueException {
        requireNonNull(phone);
        return phone.isPresent() ? Optional.of(parsePhone(phone.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws IllegalValueException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new IllegalValueException(Address.MESSAGE_ADDRESS_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code Optional<String> address} into an {@code Optional<Address>} if {@code address} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Address> parseAddress(Optional<String> address) throws IllegalValueException {
        requireNonNull(address);
        return address.isPresent() ? Optional.of(parseAddress(address.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws IllegalValueException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new IllegalValueException(Email.MESSAGE_EMAIL_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code Optional<String> email} into an {@code Optional<Email>} if {@code email} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Email> parseEmail(Optional<String> email) throws IllegalValueException {
        requireNonNull(email);
        return email.isPresent() ? Optional.of(parseEmail(email.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String timeZone} into a {@code CustTimeZone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code timeZone} is invalid.
     */
    public static CustTimeZone parseCustTimeZone(String timeZone) throws IllegalValueException {
        requireNonNull(timeZone);
        String trimmedCustTimeZone = timeZone.trim();
        if (!CustTimeZone.isValidTimeZone(trimmedCustTimeZone)) {
            throw new IllegalValueException(CustTimeZone.MESSAGE_TIMEZONE_CONSTRAINTS);
        }
        return new CustTimeZone(trimmedCustTimeZone);
    }

    /**
     * Parses a {@code Optional<String> timeZone} into an {@code Optional<CustTimeZone>} if {@code timeZone} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<CustTimeZone> parseCustTimeZone(Optional<String> timeZone) throws IllegalValueException {
        requireNonNull(timeZone);
        return timeZone.isPresent() ? Optional.of(parseCustTimeZone(timeZone.get())) : Optional.empty();
    }

    //@@author XavierMaYuqian
    /**
     * Parses a {@code String comment} into a {@code Comment}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code comment} is invalid.
     */
    public static Comment parseComment(String comment) throws IllegalValueException {
        requireNonNull(comment);
        String trimmedComment = comment.trim();
        if (!Comment.isValidComment(trimmedComment)) {
            throw new IllegalValueException(Comment.MESSAGE_COMMENT_CONSTRAINTS);
        }
        return new Comment(trimmedComment);
    }

    //@@author XavierMaYuqian
    /**
     * Parses a {@code Optional<String> comment} into an {@code Optional<Comment>} if {@code comment} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Comment> parseComment(Optional<String> comment) throws IllegalValueException {
        requireNonNull(comment);
        return comment.isPresent() ? Optional.of(parseComment(comment.get())) : Optional.empty();
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws IllegalValueException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws IllegalValueException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new IllegalValueException(Tag.MESSAGE_TAG_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws IllegalValueException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
