# cjianhui
###### \java\seedu\address\commons\events\ui\JumpToGroupListRequestEvent.java
``` java

/**
 * Indicates a request to jump to the list of persons
 */
public class JumpToGroupListRequestEvent extends BaseEvent {

    public final int targetIndex;

    public JumpToGroupListRequestEvent(Index targetIndex) {
        this.targetIndex = targetIndex.getZeroBased();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
```
###### \java\seedu\address\logic\commands\AddPersonToGroupCommand.java
``` java

/**
 * Adds a person to the address book.
 */
public class AddPersonToGroupCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "gadd";
    public static final String COMMAND_ALT = "ga";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to a group. "
            + "Parameters: "
            + PREFIX_GROUP + "GROUP INDEX "
            + "p/PERSON INDEX"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_GROUP + "2" + "p/1";

    public static final String MESSAGE_SUCCESS = "Added %1$s to %2$s.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the group.";

    private final Index personIndex;
    private final Index groupIndex;

    /**
     * Creates an CreateGroupCommand to add the specified {@code ReadOnlyGroup}
     */
    public AddPersonToGroupCommand(Index groupIndex, Index personIndex) {
        this.groupIndex = groupIndex;
        this.personIndex = personIndex;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);

        List<ReadOnlyGroup> lastShownGroupList = model.getFilteredGroupList();
        List<ReadOnlyPerson> lastShownPersonList = model.getFilteredPersonList();

        if (groupIndex.getZeroBased() >= lastShownGroupList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_GROUP_DISPLAYED_INDEX);
        }

        ReadOnlyGroup targetGroup = lastShownGroupList.get(groupIndex.getZeroBased());
        String groupName = targetGroup.getName().toString();

        if (personIndex.getZeroBased() >= lastShownPersonList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson toAdd = lastShownPersonList.get(personIndex.getZeroBased());
        String personName = toAdd.getName().toString();

        try {
            model.addPersonToGroup(groupIndex, toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, personName, groupName));
        } catch (GroupNotFoundException gnfe) {
            assert false : "The target group cannot be missing";
        } catch (PersonNotFoundException pnfe) {
            assert false : "The target person cannot be missing";
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, personName, groupName));

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddPersonToGroupCommand // instanceof handles nulls
                && groupIndex.equals(((AddPersonToGroupCommand) other).groupIndex)
                && personIndex.equals(((AddPersonToGroupCommand) other).personIndex));

    }
}
```
###### \java\seedu\address\logic\commands\CreateGroupCommand.java
``` java

/**
 * Adds a person to the address book.
 */
public class CreateGroupCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "gcreate";
    public static final String COMMAND_ALT = "gc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Creates a group in address book. "
            + "Parameters: "
            + PREFIX_NAME + "GROUP NAME "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Bamboo";

    public static final String MESSAGE_SUCCESS = "New group added: %1$s";
    public static final String MESSAGE_DUPLICATE_GROUP = "This group already exists in the address book";

    private final Group toAdd;

    /**
     * Creates an CreateGroupCommand to add the specified {@code ReadOnlyGroup}
     */
    public CreateGroupCommand(ReadOnlyGroup group) {
        toAdd = new Group(group);
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addGroup(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicateGroupException dge) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreateGroupCommand // instanceof handles nulls
                && toAdd.equals(((CreateGroupCommand) other).toAdd));
    }
}
```
###### \java\seedu\address\logic\commands\DeleteGroupCommand.java
``` java

/**
 * Adds a person to the address book.
 */
public class DeleteGroupCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "gdelete";
    public static final String COMMAND_ALT = "gd";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_GROUP_SUCCESS = "Deleted Group: %1$s";

    private final Index targetIndex;

    public DeleteGroupCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyGroup> lastShownList = model.getFilteredGroupList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_GROUP_DISPLAYED_INDEX);
        }

        ReadOnlyGroup groupToDelete = lastShownList.get(targetIndex.getZeroBased());

        try {
            model.deleteGroup(groupToDelete);
        } catch (GroupNotFoundException gnfe) {
            assert false : "The target group cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_GROUP_SUCCESS, groupToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteGroupCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteGroupCommand) other).targetIndex)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\ListCommand.java
``` java

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_ALT = "l";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons in the address book.\n"
            + ": Specify prefix f/ to list all person(s) marked as 'Favourite'.\n"
            + "Parameters: [f/]\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all persons";
    public static final String MESSAGE_LIST_FAVOURITE_SUCCESS = "Listed all 'favourite' persons";

    private final boolean listFavourite;

    public ListCommand(boolean listFavourite) {
        this.listFavourite = listFavourite;
    }

    @Override
    public CommandResult execute() {
        if (listFavourite) {
            model.updateFilteredPersonList(isFavourite());
            return new CommandResult(MESSAGE_LIST_FAVOURITE_SUCCESS);
        } else {
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

    }

    public static Predicate<ReadOnlyPerson> isFavourite() {
        return p ->
                p.getFavourite().getStatus();
    }
}
```
###### \java\seedu\address\logic\commands\RemovePersonFromGroupCommand.java
``` java

/**
 * Adds a person to the address book.
 */
public class RemovePersonFromGroupCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "gremove";
    public static final String COMMAND_ALT = "gr";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a person from a group. "
            + "Parameters: "
            + PREFIX_GROUP + "GROUP INDEX "
            + "p/PERSON INDEX"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_GROUP + "2" + "p/1";

    public static final String MESSAGE_SUCCESS = "Removed %1$s from %2$s.";
    public static final String MESSAGE_PERSON_NOT_FOUND = "This person does not exist in the group.";
    public static final String MESSAGE_EMPTY_GROUP = "The group is empty.";

    private final Index personIndex;
    private final Index groupIndex;

    /**
     * Creates an CreateGroupCommand to add the specified {@code ReadOnlyGroup}
     */
    public RemovePersonFromGroupCommand(Index groupIndex, Index personIndex) {
        this.groupIndex = groupIndex;
        this.personIndex = personIndex;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);

        List<ReadOnlyGroup> lastShownGroupList = model.getFilteredGroupList();
        List<ReadOnlyPerson> lastShownPersonList = model.getFilteredPersonList();

        if (groupIndex.getZeroBased() >= lastShownGroupList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_GROUP_DISPLAYED_INDEX);
        }

        ReadOnlyGroup targetGroup = lastShownGroupList.get(groupIndex.getZeroBased());
        String groupName = targetGroup.getName().toString();

        if (personIndex.getZeroBased() >= lastShownPersonList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson toAdd = lastShownPersonList.get(personIndex.getZeroBased());
        String personName = toAdd.getName().toString();

        try {
            model.deletePersonFromGroup(groupIndex, toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, personName, groupName));
        } catch (GroupNotFoundException gnfe) {
            assert false : "The target group cannot be missing";
        } catch (PersonNotFoundException pnfe) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        } catch (NoPersonsException dpe) {
            throw new CommandException(MESSAGE_EMPTY_GROUP);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, personName, groupName));
    }

    @Override
    public boolean equals (Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemovePersonFromGroupCommand // instanceof handles nulls
                && groupIndex.equals(((RemovePersonFromGroupCommand) other).groupIndex)
                && personIndex.equals(((RemovePersonFromGroupCommand) other).personIndex));

    }
}
```
###### \java\seedu\address\logic\commands\SelectCommand.java
``` java

/**
 * Selects a person identified using it's last displayed index from the address book.
 */
public class SelectCommand extends Command {

    public static final String COMMAND_WORD = "select";
    public static final String COMMAND_ALT = "sel";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the person identified by the index number used in the last person listing.\n"
            + ": Specify prefix g/ to select a group by its index number.\n"
            + "Parameters: [g/]INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SELECT_PERSON_SUCCESS = "Selected Person: %1$s";
    public static final String MESSAGE_SELECT_GROUP_SUCCESS = "Selected Group: %1$s";

    private final Index targetIndex;
    private final boolean isGroup;

    public SelectCommand(Index targetIndex, boolean isGroup) {
        this.targetIndex = targetIndex;
        this.isGroup = isGroup;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        List<ReadOnlyGroup> lastShownGroupList = model.getFilteredGroupList();

        if (isGroup) {
            if (targetIndex.getZeroBased() >= lastShownGroupList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_GROUP_DISPLAYED_INDEX);
            }
            EventsCenter.getInstance().post(new JumpToGroupListRequestEvent(targetIndex));
            return new CommandResult(String.format(MESSAGE_SELECT_GROUP_SUCCESS, targetIndex.getOneBased()));
        } else {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            EventsCenter.getInstance().post(new JumpToPersonListRequestEvent(targetIndex));
            return new CommandResult(String.format(MESSAGE_SELECT_PERSON_SUCCESS, targetIndex.getOneBased()));
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SelectCommand // instanceof handles nulls
                && this.targetIndex.equals(((SelectCommand) other).targetIndex))
                && this.isGroup == ((SelectCommand) other).isGroup; // state check
    }
}
```
###### \java\seedu\address\logic\commands\SortCommand.java
``` java

/**
 * Sorts persons according to field specified.
 */
public class SortCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "sort";
    public static final String COMMAND_ALT = "s";
    public static final String REVERSE_ORDER = "r";

    public static final String MESSAGE_SORT_PERSON_SUCCESS = "Sorted address book by %1$s in %2$s order.";
    public static final String MESSAGE_EMPTY_LIST = "No person(s) to sort.";

    private static final String PREFIX_NAME_FIELD = "n/";
    private static final String PREFIX_PHONE_FIELD = "p/";
    private static final String PREFIX_EMAIL_FIELD = "e/";
    private static final String PREFIX_ADDRESS_FIELD = "a/";


    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts persons either in ascending or descending order (ascending by default)"
            + " according to prefix specified (name by default)\n"
            + "Parameters: "
            + "[PREFIX/[r]]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_EMAIL_FIELD + REVERSE_ORDER;

    private final String field;
    private final boolean isReverseOrder;

    /*
        Default values assigned to variable used in MESSAGE_SORT_PERSON_SUCCESS
     */
    private String sortBy = "name";
    private String order = "ascending";

    /**
     * @param field     specify which field to sort by
     * @param isReverseOrder specify if sorting is to be in reverse order
     */
    public SortCommand(String field, Boolean isReverseOrder) {
        requireNonNull(field);
        requireNonNull(isReverseOrder);

        this.field = field;
        this.isReverseOrder = isReverseOrder;

    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        Comparator<ReadOnlyPerson> sortComparator = getSortComparator(this.field);
        try {
            model.sortPerson(sortComparator, isReverseOrder);
        } catch (NoPersonsException npe) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        if (isReverseOrder) {
            this.order = "descending";
        }
        return new CommandResult(String.format(MESSAGE_SORT_PERSON_SUCCESS, sortBy, order));


    }

    private Comparator<ReadOnlyPerson> getSortComparator(String field) {
        switch (field) {
        case PREFIX_NAME_FIELD:
            this.sortBy = "name";
            return (o1, o2) -> o1.getName().toString().compareToIgnoreCase(o2.getName().toString()
            );
        case PREFIX_PHONE_FIELD:
            this.sortBy = "phone";
            return (o1, o2) -> o1.getPhone().toString().compareToIgnoreCase(
                        o2.getPhone().toString()
            );
        case PREFIX_EMAIL_FIELD:
            this.sortBy = "email";
            return (o1, o2) -> o1.getEmail().toString().compareToIgnoreCase(
                        o2.getEmail().toString()
            );
        case PREFIX_ADDRESS_FIELD:
            this.sortBy = "address";
            return (o1, o2) -> o1.getAddress().toString().compareToIgnoreCase(
                        o2.getAddress().toString()
            );
        default:
            return (o1, o2) -> o1.getName().toString().compareToIgnoreCase(
                        o2.getName().toString()
            );
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SortCommand // instanceof handles nulls
                && field.equals(((SortCommand) other).field)
                && REVERSE_ORDER.equals(((SortCommand) other).REVERSE_ORDER));

    }

}


```
###### \java\seedu\address\logic\Logic.java
``` java
    /** Returns an unmodifiable view of the filtered list of groups */
    ObservableList<ReadOnlyGroup> getFilteredGroupList();

```
###### \java\seedu\address\logic\LogicManager.java
``` java
    @Override
    public ObservableList<ReadOnlyGroup> getFilteredGroupList() {
        return model.getFilteredGroupList();
    }

```
###### \java\seedu\address\logic\parser\AddPersonToGroupCommandParser.java
``` java

/**
 * Parses input arguments and creates a new CreateGroupCommand object
 */
public class AddPersonToGroupCommandParser implements Parser<AddPersonToGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public static final Prefix PREFIX_PERSON = new Prefix("p/");

    /** Parse AddPersonToGroupCommand Arguments */
    public AddPersonToGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_PERSON);

        if (!arePrefixesPresent(argMultimap, PREFIX_GROUP, PREFIX_PERSON)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddPersonToGroupCommand.MESSAGE_USAGE));
        }

        try {
            Index groupIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_GROUP).get());
            Index personIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_PERSON).get());
            return new AddPersonToGroupCommand(groupIndex, personIndex);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
```
###### \java\seedu\address\logic\parser\CreateGroupCommandParser.java
``` java

/**
 * Parses input arguments and creates a new CreateGroupCommand object
 */
public class CreateGroupCommandParser implements Parser<CreateGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public CreateGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateGroupCommand.MESSAGE_USAGE));
        }

        try {
            GroupName name = ParserUtil.parseGroupName(argMultimap.getValue(PREFIX_NAME)).get();
            ReadOnlyGroup group = new Group(name);
            return new CreateGroupCommand(group);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
```
###### \java\seedu\address\logic\parser\DeleteGroupCommandParser.java
``` java

/**
 * Parses input arguments and creates a new DeleteGroundCommand object
 */
public class DeleteGroupCommandParser implements Parser<DeleteGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteGroupCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteGroupCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteGroupCommand.MESSAGE_USAGE));
        }
    }

}
```
###### \java\seedu\address\logic\parser\ListCommandParser.java
``` java

/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    private Boolean isFavourite = false;

    /**
     * Parses the given {@code String} of arguments in the context of the SelectCommand
     * and returns an SelectCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        if (!args.matches("^( f/)?$")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        if (args.trim().contains(PREFIX_FAVOURITE.getPrefix())) {
            isFavourite = true;
        }
        return new ListCommand(isFavourite);


    }
}
```
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Parses {@code Collection<String> groups} into a {@code Set<Group>}.
     */
    public static Set<Group> parseGroups(Collection<String> groups) throws IllegalValueException {
        requireNonNull(groups);
        final Set<Group> groupSet = new HashSet<>();
        for (String groupName : groups) {
            groupSet.add(new Group(groupName));
        }
        return groupSet;
    }

}
```
###### \java\seedu\address\logic\parser\RemovePersonFromGroupCommandParser.java
``` java

/**
 * Parses input arguments and creates a new CreateGroupCommand object
 */
public class RemovePersonFromGroupCommandParser implements Parser<RemovePersonFromGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public static final Prefix PREFIX_PERSON = new Prefix("p/");

    /** Parse AddPersonToGroupCommand Arguments */
    public RemovePersonFromGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GROUP, PREFIX_PERSON);

        if (!arePrefixesPresent(argMultimap, PREFIX_GROUP, PREFIX_PERSON)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddPersonToGroupCommand.MESSAGE_USAGE));
        }

        try {
            Index groupIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_GROUP).get());
            Index personIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_PERSON).get());
            return new RemovePersonFromGroupCommand(groupIndex, personIndex);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
```
###### \java\seedu\address\logic\parser\SortCommandParser.java
``` java

/**
 * Parses input arguments and creates a new SortCommand object
 */
public class SortCommandParser implements Parser<SortCommand> {

    private String field;
    private Boolean isReverseOrder = false;

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns an SortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (!args.matches("^|( [npea]/(r)?)$")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }

        if ("".equals(args)) {
            args = " n/";
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        argMultimap.getValue(PREFIX_NAME).ifPresent(setOrder(PREFIX_NAME));
        argMultimap.getValue(PREFIX_PHONE).ifPresent(setOrder(PREFIX_PHONE));
        argMultimap.getValue(PREFIX_EMAIL).ifPresent(setOrder(PREFIX_EMAIL));
        argMultimap.getValue(PREFIX_ADDRESS).ifPresent(setOrder(PREFIX_ADDRESS));

        return new SortCommand(field, isReverseOrder);

    }

    private Consumer<String> setOrder(Prefix prefix) {
        return s -> {

            field = prefix.toString();

            if (s.equals(SortCommand.REVERSE_ORDER)) {
                isReverseOrder = Boolean.TRUE;
                return;
            } else {
                isReverseOrder = Boolean.FALSE;
                return;

            }
        };

    }


}
```
###### \java\seedu\address\model\group\exceptions\DuplicateGroupException.java
``` java

/**
 * Signals that the operation will result in duplicate Person objects.
 */
public class DuplicateGroupException extends DuplicateDataException {
    public DuplicateGroupException() {
        super("Operation would result in duplicate groups");
    }
}
```
###### \java\seedu\address\model\group\exceptions\GroupNotFoundException.java
``` java

/**
 * Signals that the operation is unable to find the specified person.
 */
public class GroupNotFoundException extends Exception {}
```
###### \java\seedu\address\model\group\exceptions\NoGroupsException.java
``` java

/**
 * Signals that the operation is unable to sort due to an empty list.
 */
public class NoGroupsException extends Exception {}
```
###### \java\seedu\address\model\group\Group.java
``` java

/**
 * Represents a Group in an address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Group implements ReadOnlyGroup {

    private ObjectProperty<GroupName> groupName;
    /**
     *  A Group will have an empty persons list by default
     */
    private ObjectProperty<UniquePersonList> groupMembers =
            new SimpleObjectProperty<>(new UniquePersonList());

    /**
     * Every field must be present and not null.
     */
    public Group(GroupName name) {
        requireNonNull(name);
        this.groupName = new SimpleObjectProperty<>(name);
    }

    /**
     * Every field must be present and not null.
     */
    public Group(GroupName name, Set<Person> groupMembers) {
        requireAllNonNull(name, groupMembers);
        this.groupName = new SimpleObjectProperty<>(name);
        this.groupMembers = new SimpleObjectProperty<>(new UniquePersonList(groupMembers));
    }

    /**
     * Every field must be present and not null.
     */
    public Group(String name) throws IllegalValueException {
        requireNonNull(name);
        this.groupName = new SimpleObjectProperty<>(new GroupName(name));
    }
    /**
     * Creates a copy of the given ReadOnlyGroup.
     */
    public Group(ReadOnlyGroup source) {
        this(source.getName(), source.getMembers());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Group // instanceof handles nulls
                && this.groupName.toString().equals(((Group) other).groupName.toString())); // state check
    }

    @Override
    public int hashCode() {
        return groupName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return getAsText();
    }

    public void addMember(ReadOnlyPerson person) throws DuplicatePersonException {
        this.groupMembers.get().add(person);
    }

    public void deleteMember(ReadOnlyPerson person) throws PersonNotFoundException {
        this.groupMembers.get().remove(person);
    }

    @Override
    public ObjectProperty<GroupName> nameProperty() {
        return groupName;
    }

    @Override
    public GroupName getName() {
        return groupName.get();
    }

    @Override
    public ObjectProperty<UniquePersonList> groupMembersProperty() {
        return groupMembers;
    }

    public void setGroupName(GroupName name) {
        this.groupName.set(requireNonNull(name));
    }

    @Override
    public Set<Person> getMembers() {
        return groupMembers.get().toSet();
    }



}
```
###### \java\seedu\address\model\group\GroupName.java
``` java

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class GroupName {

    public static final String MESSAGE_GROUP_CONSTRAINTS = "Group names should contain only "
            + "alphanumeric characters, spaces, underscores and dashes";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String GROUP_VALIDATION_REGEX = "^[a-zA-Z0-9]([\\w -]*[a-zA-Z0-9])?$";

    public final String fullName;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public GroupName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!isValidName(trimmedName)) {
            throw new IllegalValueException(MESSAGE_GROUP_CONSTRAINTS);
        }
        this.fullName = trimmedName;
    }

    /**
     * Returns true if a given string is a valid person name.
     */
    public static boolean isValidName(String test) {
        return test.matches(GROUP_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof GroupName // instanceof handles nulls
                && this.fullName.equals(((GroupName) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
```
###### \java\seedu\address\model\group\ReadOnlyGroup.java
``` java

/**
 * A read-only immutable interface for a Group in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyGroup {

    ObjectProperty<GroupName> nameProperty();
    GroupName getName();
    ObjectProperty<UniquePersonList> groupMembersProperty();
    Set<Person> getMembers();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyGroup other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getMembers().equals(this.getMembers()));
    }

    /**
     * Formats the Group as text, showing group name.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Group Name: ")
                .append(getName());
        return builder.toString();
    }

}
```
###### \java\seedu\address\model\group\UniqueGroupList.java
``` java

/**
 * A list of groups that enforces no nulls and uniqueness between its elements.
 *
 * Supports minimal set of list operations for the app's features.
 *
 * @see Group#equals(Object)
 */


public class UniqueGroupList implements Iterable<Group> {

    private final ObservableList<Group> internalList = FXCollections.observableArrayList();
    // used by asObservableList()
    private final ObservableList<ReadOnlyGroup> mappedList = EasyBind.map(internalList, (group) -> group);

    /**
     * Creates a UniqueGroupList using given Groups.
     * Enforces no nulls.
     */
    public UniqueGroupList(Set<Group> groups) {
        requireAllNonNull(groups);
        internalList.addAll(groups);

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Constructs empty UniqueGroupList.
     */
    public UniqueGroupList() {}

    /**
     * Returns true if the list contains an equivalent Group as the given argument.
     */
    public boolean contains(ReadOnlyGroup toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Returns all groups in this list as a Set.
     * This set is mutable and change-insulated against the internal list.
     */
    public Set<Group> toSet() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return new HashSet<>(internalList);
    }

    /**
     * Ensures every group in the argument list exists in this object.
     */
    public void mergeFrom(UniqueGroupList from) {
        final Set<Group> alreadyInside = this.toSet();
        from.internalList.stream()
                .filter(group -> !alreadyInside.contains(group))
                .forEach(internalList::add);

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Adds a Group to the list.
     *
     * @throws seedu.address.model.group.exceptions.DuplicateGroupException
     * if the Tag to add is a duplicate of an existing Tag in the list.
     */
    public void add(ReadOnlyGroup toAdd) throws DuplicateGroupException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateGroupException();
        }
        internalList.add(new Group(toAdd));

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Removes the equivalent person from the list.
     *
     * @throws GroupNotFoundException if no such group could be found in the list.
     */
    public boolean remove(ReadOnlyGroup toRemove) throws GroupNotFoundException {
        requireNonNull(toRemove);
        final boolean groupFoundAndDeleted = internalList.remove(toRemove);
        if (!groupFoundAndDeleted) {
            throw new GroupNotFoundException();
        }
        return groupFoundAndDeleted;
    }

    /**
     * Adds person to specified group in the list.
     *
     * @throws GroupNotFoundException if no such group could be found in the list.
     * @throws DuplicatePersonException if an equivalent person exists in the list.
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public void addPersonToGroup(Index target, ReadOnlyPerson toAdd)
            throws GroupNotFoundException, DuplicatePersonException, PersonNotFoundException {
        requireNonNull(toAdd);
        requireNonNull(target);

        Group targetGroup = internalList.get(target.getZeroBased());

        if (isNull(targetGroup)) {
            throw new GroupNotFoundException();
        }

        try {
            targetGroup.addMember(toAdd);
        } catch (DuplicatePersonException dpe) {
            throw new DuplicatePersonException();
        }

        internalList.set(target.getZeroBased(), targetGroup);
    }

    /**
     * Removes person from specified group in the list.
     *
     * @throws GroupNotFoundException if no such group could be found in the list.
     * @throws NoPersonsException if list is empty.
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public void removePersonFromGroup(Index target, ReadOnlyPerson toAdd)
            throws GroupNotFoundException, NoPersonsException, PersonNotFoundException {
        requireNonNull(toAdd);
        requireNonNull(target);

        Group targetGroup = internalList.get(target.getZeroBased());

        if (targetGroup.getMembers().size() < 1) {
            throw new NoPersonsException();
        }

        if (isNull(targetGroup)) {
            throw new GroupNotFoundException();
        }

        try {
            targetGroup.deleteMember(toAdd);
        } catch (PersonNotFoundException pnfe) {
            throw new PersonNotFoundException();
        }

        internalList.set(target.getZeroBased(), targetGroup);
    }

    @Override
    public Iterator<Group> iterator() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return internalList.iterator();
    }


    public void setGroups(UniqueGroupList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setGroups(List<? extends ReadOnlyGroup> groups) throws DuplicateGroupException {
        final UniqueGroupList replacement = new UniqueGroupList();
        for (final ReadOnlyGroup group : groups) {
            replacement.add(new Group(group));
        }
        setGroups(replacement);
    }


    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ReadOnlyGroup> asObservableList() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return FXCollections.unmodifiableObservableList(mappedList);
    }

    @Override
    public boolean equals(Object other) {
        assert CollectionUtil.elementsAreUnique(internalList);
        return other == this // short circuit if same object
                || (other instanceof seedu.address.model.group.UniqueGroupList // instanceof handles nulls
                && this.internalList.equals(((seedu.address.model.group.UniqueGroupList) other).internalList));
    }

    /**
     * Returns true if the element in this list is equal to the elements in {@code other}.
     * The elements do not have to be in the same order.
     */
    public boolean equalsOrderInsensitive(seedu.address.model.group.UniqueGroupList other) {
        assert CollectionUtil.elementsAreUnique(internalList);
        assert CollectionUtil.elementsAreUnique(other.internalList);
        return this == other || new HashSet<>(this.internalList).equals(new HashSet<>(other.internalList));
    }

    @Override
    public int hashCode() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return internalList.hashCode();
    }

}

```
###### \java\seedu\address\model\Model.java
``` java
    /** {@code Predicate} that always evaluate to true */
    Predicate<ReadOnlyGroup> PREDICATE_SHOW_ALL_GROUPS = unused -> true;

```
###### \java\seedu\address\model\Model.java
``` java
    /** Sorts address book list */
    void sortPerson(Comparator<ReadOnlyPerson> sortComparator, boolean isReverseOrder) throws NoPersonsException;

    /** Adds the given group */
    void addGroup(ReadOnlyGroup group) throws DuplicateGroupException;

    /** Deletes the given group */
    void deleteGroup(ReadOnlyGroup group) throws GroupNotFoundException;

```
###### \java\seedu\address\model\Model.java
``` java
    /** Adds given person to given group */
    void addPersonToGroup(Index targetGroup, ReadOnlyPerson toAdd) throws
            GroupNotFoundException, PersonNotFoundException, DuplicatePersonException;

    /** Deletes given person from given group */
    void deletePersonFromGroup(Index targetGroup, ReadOnlyPerson toRemove) throws
            GroupNotFoundException, PersonNotFoundException, NoPersonsException;

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException;


    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<ReadOnlyPerson> getFilteredPersonList();

```
###### \java\seedu\address\model\Model.java
``` java
    /** Returns an unmodifiable view of the filtered group list */
    ObservableList<ReadOnlyGroup> getFilteredGroupList();

```
###### \java\seedu\address\model\Model.java
``` java
    /**
     * Updates the filter of the filtered group list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredGroupList(Predicate<ReadOnlyGroup> predicate);

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void sortPerson(Comparator<ReadOnlyPerson> sortComparator, boolean isReverseOrder)
            throws NoPersonsException {
        addressBook.sortPerson(sortComparator, isReverseOrder);
    }

    @Override
    public void addGroup(ReadOnlyGroup group) throws DuplicateGroupException {
        addressBook.addGroup(group);
        updateFilteredGroupList(PREDICATE_SHOW_ALL_GROUPS);
        indicateAddressBookChanged();
    }

    @Override
    public void deleteGroup(ReadOnlyGroup target) throws GroupNotFoundException {
        addressBook.removeGroup(target);
        indicateAddressBookChanged();
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void addPersonToGroup(Index targetGroup, ReadOnlyPerson toAdd)
            throws GroupNotFoundException, PersonNotFoundException, DuplicatePersonException {
        addressBook.addPersonToGroup(targetGroup, toAdd);

        indicateAddressBookChanged();
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void deletePersonFromGroup(Index targetGroup, ReadOnlyPerson toRemove)
            throws GroupNotFoundException, PersonNotFoundException, NoPersonsException {
        addressBook.deletePersonFromGroup(targetGroup, toRemove);
        /** Update filtered list with predicate for current group members in group after removing a person */
        ObservableList<ReadOnlyPerson> personList = addressBook.getGroupList()
                .get(targetGroup.getZeroBased()).groupMembersProperty().get().asObservableList();
        updateFilteredPersonList(getGroupMembersPredicate(personList));
        indicateAddressBookChanged();

    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code ReadOnlyPerson} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public ObservableList<ReadOnlyGroup> getFilteredGroupList() {
        return FXCollections.unmodifiableObservableList(filteredGroups);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void updateFilteredGroupList(Predicate<ReadOnlyGroup> predicate) {
        requireNonNull(predicate);
        filteredGroups.setPredicate(predicate);
    }

    /** Returns predicate that returns true if group member list contains a person */
    /** Used to update FilteredPersonList whenever there is a need to display group members */
    public Predicate<ReadOnlyPerson> getGroupMembersPredicate(ObservableList<ReadOnlyPerson> personList) {
        return personList::contains;
    }
```
###### \java\seedu\address\model\ReadOnlyAddressBook.java
``` java
    /**
     * Returns an unmodifiable view of the groups list.
     * This list will not contain any duplicate groups.
     */
    ObservableList<ReadOnlyGroup> getGroupList();

```
###### \java\seedu\address\storage\XmlAdaptedGroup.java
``` java

/**
 * JAXB-friendly adapted version of the Tag.
 */
public class XmlAdaptedGroup {

    @XmlElement(required = true)
    private String groupName;
    @XmlElement
    private List<XmlAdaptedPerson> members = new ArrayList<>();
    /**
     * Constructs an XmlAdaptedGroup.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedGroup() {}

    /**
     * Converts a given Tag into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedGroup(ReadOnlyGroup source) {
        groupName = source.getName().toString();
        for (ReadOnlyPerson person: source.getMembers()) {
            members.add(new XmlAdaptedPerson(person));
        }

    }

    /**
     * Converts this jaxb-friendly adapted group object into the model's Group object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Group toModelType() throws IllegalValueException {
        final List<Person> personList = new ArrayList<>();
        for (XmlAdaptedPerson person: members) {
            personList.add(person.toModelType());
        }

        final GroupName groupName = new GroupName(this.groupName);
        final Set<Person> persons = new HashSet<>(personList);
        return new Group(groupName, persons);
    }

}
```
###### \java\seedu\address\ui\GroupListPanel.java
``` java
    @Subscribe
    private void handleJumpToGroupListRequestEvent(JumpToGroupListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
        /** To ensure that group at index 1 can be selected even when previous selection was its index */
        groupListView.getSelectionModel().clearSelection();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code GroupCard}.
     */
    class GroupListViewCell extends ListCell<GroupCard> {

        @Override
        protected void updateItem(GroupCard group, boolean empty) {
            super.updateItem(group, empty);

            if (empty || group == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(group.getRoot());
            }
        }
    }

}
```
