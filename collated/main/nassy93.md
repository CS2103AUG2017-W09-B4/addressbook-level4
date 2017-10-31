# nassy93
###### \java\seedu\address\logic\commands\AddFavouriteCommand.java
``` java

/**
 * Marks an indexed person as a favourite in the address book.
 */
public class AddFavouriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "fadd";
    public static final String COMMAND_ALT = "fa";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person identified by the index number used in the last person listing as a favourite.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_FAVE_PERSON_SUCCESS = "%1$s has been marked as a favourite contact.";
    public static final String MESSAGE_ALREADY_FAVOURITE = "This person is already marked as a favourite.";

    private final Index targetIndex;

    public AddFavouriteCommand(Index index) {
        requireNonNull(index);
        this.targetIndex = index;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(targetIndex.getZeroBased());
        Person editedPerson = createFavePerson(personToEdit);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_ALREADY_FAVOURITE);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_FAVE_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the the Favourite attribute set to true.
     */
    private static Person createFavePerson(ReadOnlyPerson personToEdit) {
        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Email updatedEmail = personToEdit.getEmail();
        Address updatedAddress = personToEdit.getAddress();
        Favourite updatedFavourite = new Favourite(true);
        Set<Tag> updatedTags = personToEdit.getTags();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedFavourite, updatedTags);
    }
}
```
###### \java\seedu\address\logic\commands\ModListCommand.java
``` java

/**
 * Lists all persons in the address book to the user with priority to favourites
 */
public class ModListCommand extends Command {
    public static final String COMMAND_WORD = "list2";
    public static final String COMMAND_ALT = "l2";

    public static final String MESSAGE_SUCCESS = "Listed all persons";


    @Override
    public CommandResult execute() throws CommandException {
        Comparator<ReadOnlyPerson> faveComparator = getFaveComparator();
        try {
            model.sortPerson(faveComparator, false);
        } catch (NoPersonsException npe) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    private Comparator<ReadOnlyPerson> getFaveComparator() {
        return (f1, f2) -> Boolean.compare(f2.getFavourite().getStatus(), f1.getFavourite().getStatus());
    }
}
```
###### \java\seedu\address\logic\commands\RemoveFavouriteCommand.java
``` java

/**
 * Sets Favourite attribute of Indexed person as false in the address book. (remove from favourites)
 */
public class RemoveFavouriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "fremove";
    public static final String COMMAND_ALT = "fr";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the favourite status from the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNFAVE_PERSON_SUCCESS = "%1$s has been removed from favourites.";
    public static final String MESSAGE_ALREADY_NORMAL = "This person is not a favourite.";

    private final Index targetIndex;

    public RemoveFavouriteCommand(Index index) {
        requireNonNull(index);
        this.targetIndex = index;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(targetIndex.getZeroBased());
        Person editedPerson = removeFavePerson(personToEdit);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_ALREADY_NORMAL);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_UNFAVE_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the the Favourite attribute set to true.
     */
    private static Person removeFavePerson(ReadOnlyPerson personToEdit) {
        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Email updatedEmail = personToEdit.getEmail();
        Address updatedAddress = personToEdit.getAddress();
        Favourite updatedFavourite = new Favourite(false);
        Set<Tag> updatedTags = personToEdit.getTags();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedFavourite, updatedTags);
    }

}
```
###### \java\seedu\address\logic\parser\AddFavouriteCommandParser.java
``` java

/**
 * Parses input arguments and creates a new AddFaveCommand object
 */
public class AddFavouriteCommandParser implements Parser<AddFavouriteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddFaveCommandCommand
     * and returns an AddFaveCommandCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddFavouriteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new AddFavouriteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFavouriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\RemoveFavouriteCommandParser.java
``` java

/**
 * Parses input arguments and creates a new RemoveFaveCommand object
 */
public class RemoveFavouriteCommandParser implements Parser<RemoveFavouriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RemoveFaveCommandCommand
     * and returns an RemoveFaveCommandCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemoveFavouriteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new RemoveFavouriteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveFavouriteCommand.MESSAGE_USAGE));
        }
    }
}
```
