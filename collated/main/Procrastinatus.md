# Procrastinatus
###### \java\seedu\address\commons\events\ui\GroupPanelSelectionChangedEvent.java
``` java

/**
 * Represents a selection change in the Group List Panel
 */
public class GroupPanelSelectionChangedEvent extends BaseEvent {


    private final GroupCard newSelection;

    public GroupPanelSelectionChangedEvent(GroupCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public GroupCard getNewSelection() {
        return newSelection;
    }
}
```
###### \java\seedu\address\commons\events\ui\JumpToScheduleListRequestEvent.java
``` java

/**
 * Indicates a request to jump to the list of persons
 */
public class JumpToScheduleListRequestEvent extends BaseEvent {

    public final int targetIndex;

    public JumpToScheduleListRequestEvent(Index targetIndex) {
        this.targetIndex = targetIndex.getZeroBased();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
```
###### \java\seedu\address\commons\events\ui\SchedulePanelSelectionChangedEvent.java
``` java

/**
 * Represents a selection change in the Schedule List Panel
 */
public class SchedulePanelSelectionChangedEvent extends BaseEvent {

    private final ScheduleCard newSelection;

    public SchedulePanelSelectionChangedEvent(ScheduleCard newSelection) {
        this.newSelection = newSelection;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ScheduleCard getNewSelection() {
        return newSelection;
    }
}
```
###### \java\seedu\address\logic\commands\DeleteCommand.java
``` java
    private Index[] targetIndexes;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    public DeleteCommand(Index[] targetIndexes) {
        this.targetIndexes = targetIndexes;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        ReadOnlyPerson personToDelete = null;
        ReadOnlyPerson[] personsToDelete = null;

        if (targetIndex != null) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToDelete = lastShownList.get(targetIndex.getZeroBased());
        }

        if (targetIndexes != null) {
            personsToDelete = new ReadOnlyPerson[targetIndexes.length];
            for (int i = 0; i < personsToDelete.length; i++) {
                personsToDelete[i] = lastShownList.get(targetIndexes[i].getZeroBased());
            }
        }

        try {
            if (personsToDelete == null) {
                model.deletePerson(personToDelete);
            } else {
                model.deletePersons(personsToDelete);
            }
        } catch (PersonNotFoundException pnfe) {
            assert false : "The target person cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));
    }
```
###### \java\seedu\address\logic\Logic.java
``` java
    /** Returns an unmodifiable view of the filtered list of schedules */
    ObservableList<ReadOnlySchedule> getFilteredScheduleList();
```
###### \java\seedu\address\logic\LogicManager.java
``` java
    @Override
    public ObservableList<ReadOnlySchedule> getFilteredScheduleList() {
        return model.getFilteredScheduleList();
    }
```
###### \java\seedu\address\logic\parser\DeleteCommandParser.java
``` java

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            String[] arguments = args.trim().split(" ");
            if (arguments.length == 1) {
                Index index = ParserUtil.parseIndex(args);
                return new DeleteCommand(index);
            } else {
                Index[] indexes = new Index[arguments.length];
                for (int i = 0; i < indexes.length; i++) {
                    indexes[i] = ParserUtil.parseIndex(arguments[i]);
                }
                return new DeleteCommand(indexes);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }

}
```
###### \java\seedu\address\model\Model.java
``` java
    /** {@code Predicate} that always evaluate to true */
    Predicate<ReadOnlySchedule> PREDICATE_SHOW_ALL_SCHEDULES = unused -> true;
```
###### \java\seedu\address\model\Model.java
``` java
    /** Deletes the given persons. */
    void deletePersons(ReadOnlyPerson[] targets) throws PersonNotFoundException;
```
###### \java\seedu\address\model\Model.java
``` java
    /** Adds the given schedule */
    void addSchedule(ReadOnlySchedule schedule) throws DuplicateScheduleException;

    /** Deletes the given schedule */
    void deleteSchedule(ReadOnlySchedule schedule) throws ScheduleNotFoundException;

```
###### \java\seedu\address\model\Model.java
``` java
    /** Returns an unmodifiable view of the filtered schedule list */
    ObservableList<ReadOnlySchedule> getFilteredScheduleList();

    void showUnfilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate);

```
###### \java\seedu\address\model\Model.java
``` java
    /**
     * Updates the filter of the filtered schedule list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredScheduleList(Predicate<ReadOnlySchedule> predicate);
}
```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public synchronized void deletePersons(ReadOnlyPerson[] targets) throws PersonNotFoundException {
        for (ReadOnlyPerson target : targets) {
            addressBook.removePerson(target);
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }
```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void addSchedule(ReadOnlySchedule schedule) throws DuplicateScheduleException {
        addressBook.addSchedule(schedule);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void deleteSchedule(ReadOnlySchedule target) throws ScheduleNotFoundException {
        addressBook.removeSchedule(target);
        indicateAddressBookChanged();
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public ObservableList<ReadOnlySchedule> getFilteredScheduleList() {
        return FXCollections.unmodifiableObservableList(filteredSchedules);
    }
```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void updateFilteredScheduleList(Predicate<ReadOnlySchedule> predicate) {
        requireNonNull(predicate);
        filteredSchedules.setPredicate(predicate);
    }
```
###### \java\seedu\address\model\ReadOnlyAddressBook.java
``` java
    /**
     * Returns an unmodifiable view of the schedules list.
     * This list will not contain any duplicate schedules.
     */
    ObservableList<ReadOnlySchedule> getScheduleList();

}
```
###### \java\seedu\address\model\schedule\exceptions\DuplicateScheduleException.java
``` java

/**
 * Signals that the operation will result in duplicate Schedule objects.
 */
public class DuplicateScheduleException extends DuplicateDataException {
    public DuplicateScheduleException() {
        super("Operation would result in duplicate schedules");
    }
}
```
###### \java\seedu\address\model\schedule\exceptions\NoSchedulesException.java
``` java

/**
 * Signals that the operation is unable to sort due to an empty list.
 */
public class NoSchedulesException extends Exception {}
```
###### \java\seedu\address\model\schedule\exceptions\ScheduleNotFoundException.java
``` java

/**
 * Signals that the operation is unable to find the specified person.
 */
public class ScheduleNotFoundException extends Exception {}
```
###### \java\seedu\address\model\schedule\ReadOnlySchedule.java
``` java

/**
 * A read-only immutable interface for a Group in the addressbook.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlySchedule {

    ObjectProperty<ScheduleName> nameProperty();
    ScheduleName getName();
    ObservableList<ReadOnlySchedule> getSchedules();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlySchedule other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getSchedules().equals(this.getSchedules()));
    }

    /**
     * Formats the Schedule as text, showing schedule name.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Schedule Name: ")
                .append(getName());
        return builder.toString();
    }

}
```
###### \java\seedu\address\model\schedule\Schedule.java
``` java

/**
 * Represents a Schedule in an address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Schedule implements ReadOnlySchedule {

    private ObjectProperty<ScheduleName> scheduleName;
    /**
     *  A Schedule will have an empty schedules list by default
     */
    private final UniqueScheduleList schedules = new UniqueScheduleList();

    /**
     * Every field must be present and not null.
     */
    public Schedule(ScheduleName name) {
        requireNonNull(name);
        this.scheduleName = new SimpleObjectProperty<>(name);
    }

    /**
     * Every field must be present and not null.
     */
    public Schedule(String name) throws IllegalValueException {
        requireNonNull(name);
        this.scheduleName = new SimpleObjectProperty<>(new ScheduleName(name));
    }
    /**
     * Creates a copy of the given ReadOnlySchedule.
     */
    public Schedule(ReadOnlySchedule source) {
        this(source.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Schedule // instanceof handles nulls
                && this.scheduleName.toString().equals(((Schedule) other).scheduleName.toString())); // state check
    }

    @Override
    public int hashCode() {
        return scheduleName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return getAsText();
    }

    public void addMember(ReadOnlySchedule schedule) throws DuplicateScheduleException {
        this.schedules.add(schedule);
    }

    public void deleteMember(ReadOnlySchedule schedule) throws ScheduleNotFoundException {
        this.schedules.remove(schedule);
    }

    @Override
    public ObjectProperty<ScheduleName> nameProperty() {
        return scheduleName;
    }

    @Override
    public ScheduleName getName() {
        return scheduleName.get();
    }

    public void setScheduleName(ScheduleName name) {
        this.scheduleName.set(requireNonNull(name));
    }

    @Override
    public ObservableList<ReadOnlySchedule> getSchedules() {
        return schedules.asObservableList();
    }

}
```
###### \java\seedu\address\model\schedule\ScheduleName.java
``` java

/**
 * Represents a Schedule's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class ScheduleName {

    public static final String MESSAGE_SCHEDULE_CONSTRAINTS = "Schedule names should contain only "
            + "alphanumeric characters, spaces, underscores and dashes";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String SCHEDULE_VALIDATION_REGEX = "^[a-zA-Z0-9]([\\w -]*[a-zA-Z0-9])?$";

    public final String fullName;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public ScheduleName(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!isValidName(trimmedName)) {
            throw new IllegalValueException(MESSAGE_SCHEDULE_CONSTRAINTS);
        }
        this.fullName = trimmedName;
    }

    /**
     * Returns true if a given string is a valid schedule name.
     */
    public static boolean isValidName(String test) {
        return test.matches(SCHEDULE_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ScheduleName // instanceof handles nulls
                && this.fullName.equals(((ScheduleName) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
```
###### \java\seedu\address\model\schedule\UniqueScheduleList.java
``` java

/**
 * A list of schedules that enforces no nulls and uniqueness between its elements.
 *
 * Supports minimal set of list operations for the app's features.
 *
 * @see Schedule#equals(Object)
 */


public class UniqueScheduleList implements Iterable<Schedule> {

    private final ObservableList<Schedule> internalList = FXCollections.observableArrayList();
    // used by asObservableList()
    private final ObservableList<ReadOnlySchedule> mappedList = EasyBind.map(internalList, (schedule) -> schedule);

    /**
     * Creates a UniqueScheduleList using given Schedules.
     * Enforces no nulls.
     */
    public UniqueScheduleList(Set<Schedule> schedules) {
        requireAllNonNull(schedules);
        internalList.addAll(schedules);

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Constructs empty ScheduleList.
     */
    public UniqueScheduleList() {}

    /**
     * Returns all schedules in this list as a Set.
     * This set is mutable and change-insulated against the internal list.
     */

    /**
     * Returns true if the list contains an equivalent Schedule as the given argument.
     */
    public boolean contains(ReadOnlySchedule toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Returns a set representation of the schedule.
     */
    public Set<Schedule> toSet() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return new HashSet<>(internalList);
    }

    /**
     * Ensures every schedule in the argument list exists in this object.
     */
    public void mergeFrom(UniqueScheduleList from) {
        final Set<Schedule> alreadyInside = this.toSet();
        from.internalList.stream()
                .filter(schedule -> !alreadyInside.contains(schedule))
                .forEach(internalList::add);

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Adds a Schedule to the list.
     *
     * @throws seedu.address.model.schedule.exceptions.DuplicateScheduleException
     * if the Schedule to add is a duplicate of an existing Schedule in the list.
     */
    public void add(ReadOnlySchedule toAdd) throws DuplicateScheduleException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateScheduleException();
        }
        internalList.add(new Schedule(toAdd));

        assert CollectionUtil.elementsAreUnique(internalList);
    }

    /**
     * Removes the equivalent schedule from the list.
     *
     * @throws ScheduleNotFoundException if no such schedule could be found in the list.
     */
    public boolean remove(ReadOnlySchedule toRemove) throws ScheduleNotFoundException {
        requireNonNull(toRemove);
        final boolean scheduleFoundAndDeleted = internalList.remove(toRemove);
        if (!scheduleFoundAndDeleted) {
            throw new ScheduleNotFoundException();
        }
        return scheduleFoundAndDeleted;
    }

    @Override
    public Iterator<Schedule> iterator() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return internalList.iterator();
    }

    public void setSchedules(UniqueScheduleList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setSchedules(List<? extends ReadOnlySchedule> schedules) throws DuplicateScheduleException {
        final UniqueScheduleList replacement = new UniqueScheduleList();
        for (final ReadOnlySchedule schedule: schedules) {
            replacement.add(new Schedule(schedule));
        }
        setSchedules(replacement);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ReadOnlySchedule> asObservableList() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return FXCollections.unmodifiableObservableList(mappedList);
    }

    @Override
    public boolean equals(Object other) {
        assert CollectionUtil.elementsAreUnique(internalList);
        return other == this // short circuit if same object
                || (other instanceof seedu.address.model.schedule.UniqueScheduleList // instanceof handles nulls
                && this.internalList.equals(((seedu.address.model.schedule.UniqueScheduleList) other).internalList));
    }

    /**
     * Returns true if the element in this list is equal to the elements in {@code other}.
     * The elements do not have to be in the same order.
     */
    public boolean equalsOrderInsensitive(seedu.address.model.schedule.UniqueScheduleList other) {
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
###### \java\seedu\address\storage\XmlAdaptedSchedule.java
``` java

/**
 * JAXB-friendly adapted version of the Schedule.
 */
public class XmlAdaptedSchedule {

    @XmlElement(required = true)
    private String scheduleName;
    @XmlElement
    private List<XmlAdaptedSchedule> schedules = new ArrayList<>();
    /**
     * Constructs an XmlAdapteddSchedule.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedSchedule() {}

    /**
     * Converts a given Schedule into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedSchedule(ReadOnlySchedule source) {
        scheduleName = source.getName().toString();
        for (ReadOnlySchedule schedule: source.getSchedules()) {
            schedules.add(new XmlAdaptedSchedule(schedule));
        }

    }

    /**
     * Converts this jaxb-friendly adapted group object into the model's Group object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Schedule toModelType() throws IllegalValueException {
        final ScheduleName scheduleName = new ScheduleName(this.scheduleName);
        return new Schedule(scheduleName);
    }

}
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "default.html";
    public static final String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com.sg/search?safe=off&q=";
    public static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";

    private static final String FXML = "BrowserPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private WebView browser;

    @FXML
    private Circle contactImageCircle;

    @FXML
    private BorderPane socialIcon1Placeholder;

    @FXML
    private BorderPane socialIcon2Placeholder;

    @FXML
    private BorderPane socialIcon3Placeholder;

    @FXML
    private BorderPane socialIcon4Placeholder;

    @FXML
    private BorderPane contactImagePlaceholder;

    @FXML
    private VBox contactDetailsVBox;

    //This has a getter method as MainWindow.java needs to
    // access this node to populate it with Logic.getFilteredScheduleList().
    @FXML
    private StackPane schedulePlaceholder;

    private ParallelTransition pt;

    public BrowserPanel() {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);
        loadDefaultPage();
        //Setup needed JFX nodes which will be updated upon selecting persons
        setupContactImageCircle();
        setupContactDetailsVBox();
        setupScheduleListViewPlaceholder();
        registerAsAnEventHandler(this);
    }

    private void setContactImage() {
        Image img = new Image("images/maleIcon.png");
        contactImageCircle.setVisible(true);
        contactImageCircle.setFill(new ImagePattern(img));
        easeIn(contactImageCircle);
    }

    private void setupContactImageCircle() {
        contactImageCircle = new Circle(250, 250, 90);
        contactImageCircle.setStroke(Color.valueOf("#3fc380"));
        contactImageCircle.setStrokeWidth(5);
        contactImageCircle.radiusProperty().bind(Bindings.min(
                contactImagePlaceholder.widthProperty().divide(3),
                contactImagePlaceholder.heightProperty().divide(3))
        );
        contactImagePlaceholder.setCenter(contactImageCircle);
        contactImageCircle.setVisible(false);
    }

    private void setupContactDetailsVBox() {
        contactDetailsVBox.setSpacing(0);
        contactDetailsVBox.getChildren().addAll(
                new Label(""),
                new Label(""),
                new Label(""),
                new Label("")
        );
        contactDetailsVBox.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 0 10");
    }

    private void setIcons() {
        BorderPane[] socialIconPlaceholders = {
            socialIcon1Placeholder,
            socialIcon2Placeholder,
            socialIcon3Placeholder,
            socialIcon4Placeholder
        };
        String[] imgUrls = {
            "images/facebook.png",
            "images/twitter.png",
            "images/instagram.png",
            "images/googleplus.png"
        };

        for (int i = 0; i < 4; i++) {
            Circle cir = new Circle(250, 250, 30);
            cir.setStroke(Color.valueOf("#3fc380"));
            cir.setStrokeWidth(5);
            cir.radiusProperty().bind(Bindings.min(
                    socialIconPlaceholders[i].widthProperty().divide(3),
                    socialIconPlaceholders[i].heightProperty().divide(3))
            );
            cir.setFill(new ImagePattern(new Image(imgUrls[i])));
            socialIconPlaceholders[i].setCenter(cir);
            easeIn(cir);
        }
    }

    private void setupScheduleListViewPlaceholder() {
        schedulePlaceholder.setVisible(false);
    }

    private void loadPersonPage(ReadOnlyPerson person) {
        loadPage(GOOGLE_SEARCH_URL_PREFIX + person.getName().fullName.replaceAll(" ", "+")
                + GOOGLE_SEARCH_URL_SUFFIX);
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPersonPage(event.getNewSelection().person);
        setContactImage();
        setContactDetails(event.getNewSelection().person);
        setIcons();
        setSchedule();
    }

    private void setContactDetails(ReadOnlyPerson person) {
        //Set up name label separately as it has no icons
        contactDetailsVBox.setSpacing(0);
        contactDetailsVBox.getChildren().addAll();

        Label name = (Label) contactDetailsVBox.getChildren().get(0);
        name.setText("" + person.getName());
        name.setStyle("-fx-font-size: 60;");
        name.setWrapText(true);
        easeIn(name);

        //Set values of other labels
        Label phone = (Label) contactDetailsVBox.getChildren().get(1);
        phone.setText("" + person.getPhone());
        Label email = (Label) contactDetailsVBox.getChildren().get(2);
        email.setText("" + person.getEmail());
        Label address = (Label) contactDetailsVBox.getChildren().get(3);
        address.setText("" + person.getAddress());

        //Add images to these labels
        Label[] labels = {phone, email, address};
        String[] iconUrls = {"images/phone.png", "images/mail.png", "images/homeBlack.png"};
        for (int i = 0; i < labels.length; i++) {
            ImageView icon = new ImageView(iconUrls[i]);
            icon.setImage(new Image(iconUrls[i]));
            icon.setPreserveRatio(true);
            icon.setFitWidth(20);
            labels[i].setGraphic(icon);
            labels[i].setWrapText(true);
            labels[i].setStyle("-fx-font-size: 17");
            easeIn(labels[i]);
        }
    }

    private void setSchedule() {
        schedulePlaceholder.setVisible(true);
        //scheduleListView.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 0 10;");
        easeIn(schedulePlaceholder);
    }

    /**
     * Animates any node passed into this method with an ease-in
     */
    private void easeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(node);
        tt.setFromX(20);
        tt.setToX(0);
        tt.setDuration(Duration.millis(400));
        tt.setInterpolator(Interpolator.EASE_IN);
        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(ft, tt);
        pt.play();
    }

    public StackPane getSchedulePlaceholder() {
        return schedulePlaceholder;
    }
}
```
###### \java\seedu\address\ui\GroupCard.java
``` java

/**
 * An UI component that displays information of a {@code Person}.
 */
public class GroupCard extends UiPart<Region> {

    private static final String FXML = "GroupListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final ReadOnlyGroup group;

    @FXML
    private Label groupName;
    @FXML
    private Label groupId;

    public GroupCard(ReadOnlyGroup group, int displayedIndex) {
        super(FXML);
        this.group = group;
        groupId.setText(displayedIndex + ". ");
        bindListeners(group);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyGroup group) {
        groupName.textProperty().bind(Bindings.convert(group.nameProperty()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GroupCard)) {
            return false;
        }

        // state check
        GroupCard card = (GroupCard) other;
        return groupId.getText().equals(card.groupId.getText())
                && group.equals(card.group);
    }
}
```
###### \java\seedu\address\ui\GroupListPanel.java
``` java

/**
 * Panel containing the list of groups.
 */
public class GroupListPanel extends UiPart<Region> {
    private static final String FXML = "GroupListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(GroupListPanel.class);

    @FXML
    private ListView<GroupCard> groupListView;

    public GroupListPanel(ObservableList<ReadOnlyGroup> groupList) {
        super(FXML);
        setConnections(groupList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<ReadOnlyGroup> groupList) {
        ObservableList<GroupCard> mappedList = EasyBind.map(
                groupList, (group) -> new GroupCard(group, groupList.indexOf(group) + 1));
        groupListView.setItems(mappedList);
        groupListView.setCellFactory(listView -> new GroupListViewCell());
        setEventHandlerForGroupSelectionChangeEvent();
    }

    private void setEventHandlerForGroupSelectionChangeEvent() {
        groupListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in group list panel changed to : '" + newValue + "'");
                        raise(new GroupPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code GroupCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            groupListView.scrollTo(index);
            groupListView.getSelectionModel().clearAndSelect(index);
        });
    }

```
###### \java\seedu\address\ui\MainWindow.java
``` java
    @FXML
    private StackPane groupListPanelPlaceholder;
```
###### \java\seedu\address\ui\MainWindow.java
``` java
        ScheduleListPanel scheduleListPanel = new ScheduleListPanel(logic.getFilteredScheduleList());
        browserPanel.getSchedulePlaceholder().getChildren().add(scheduleListPanel.getRoot());
```
###### \java\seedu\address\ui\MainWindow.java
``` java
        GroupListPanel groupListPanel = new GroupListPanel(logic.getFilteredGroupList());
        groupListPanelPlaceholder.getChildren().add(groupListPanel.getRoot());
```
###### \java\seedu\address\ui\ScheduleCard.java
``` java

/**
 * An UI component that displays information of a {@code Schedule}.
 */
public class ScheduleCard extends UiPart<Region> {

    private static final String FXML = "ScheduleListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final ReadOnlySchedule schedule;

    @FXML
    private Label scheduleName;
    @FXML
    private Label scheduleId;

    public ScheduleCard(ReadOnlySchedule schedule, int displayedIndex) {
        super(FXML);
        this.schedule = schedule;
        scheduleId.setText(displayedIndex + ". ");
        bindListeners(schedule);
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Schedule} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlySchedule schedule) {
        scheduleName.textProperty().bind(Bindings.convert(schedule.nameProperty()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ScheduleCard)) {
            return false;
        }

        // state check
        ScheduleCard card = (ScheduleCard) other;
        return scheduleId.getText().equals(card.scheduleId.getText())
                && schedule.equals(card.schedule);
    }
}
```
###### \java\seedu\address\ui\ScheduleListPanel.java
``` java

/**
 * Panel containing the list of schedules.
 */
public class ScheduleListPanel extends UiPart<Region> {
    private static final String FXML = "ScheduleListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(ScheduleListPanel.class);

    @FXML
    private ListView<ScheduleCard> scheduleListView;

    public ScheduleListPanel(ObservableList<ReadOnlySchedule> scheduleList) {
        super(FXML);
        setConnections(scheduleList);
        registerAsAnEventHandler(this);
    }

    private void setConnections(ObservableList<ReadOnlySchedule> scheduleList) {
        ObservableList<ScheduleCard> mappedList = EasyBind.map(
                scheduleList, (schedule) -> new ScheduleCard(schedule, scheduleList.indexOf(schedule) + 1));
        scheduleListView.setItems(mappedList);
        scheduleListView.setCellFactory(listView -> new scheduleListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        scheduleListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in schedule list panel changed to : '" + newValue + "'");
                        raise(new SchedulePanelSelectionChangedEvent(newValue));
                    }
                });
    }



    /**
     * Scrolls to the {@code ScheduleCard} at the {@code index} and selects it.
     */
    /* Both methods should be disabled until tests for this schedule list panel is done.
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            scheduleListView.scrollTo(index);
            scheduleListView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Subscribe
    private void handleJumpToScheduleListRequestEvent(JumpToScheduleListRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        scrollTo(event.targetIndex);
    }
    */

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code ScheduleCard}.
     */
    class scheduleListViewCell extends ListCell<ScheduleCard> {

        @Override
        protected void updateItem(ScheduleCard schedule, boolean empty) {
            super.updateItem(schedule, empty);

            if (empty || schedule == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(schedule.getRoot());
            }
        }
    }

}
```
###### \resources\view\BrowserPanel.fxml
``` fxml

<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.web.WebView?>

<?import javafx.scene.control.Label?>
<StackPane xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
   <GridPane fx:id="mainDetailsPane" gridLinesVisible="false">
     <columnConstraints>
       <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
       <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
     </columnConstraints>
     <rowConstraints>
       <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
       <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
     </rowConstraints>
      <children>
        <BorderPane fx:id="contactImagePlaceholder" prefHeight="150.0" prefWidth="200.0" styleClass="centerImagesInStackPane" />
        <WebView fx:id="browser" disable="true" visible="false" />
         <GridPane fx:id="socialIconsPane" gridLinesVisible="false" GridPane.rowIndex="1">
           <columnConstraints>
             <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
             <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
           </columnConstraints>
           <rowConstraints>
             <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
             <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
           </rowConstraints>
            <children>
              <BorderPane fx:id="socialIcon1Placeholder" prefHeight="150.0" prefWidth="200.0" styleClass="centerImagesInStackPane" GridPane.halignment="CENTER" />
              <BorderPane fx:id="socialIcon2Placeholder" prefHeight="150.0" prefWidth="200.0" styleClass="centerImagesInStackPane" GridPane.columnIndex="1" />
              <BorderPane fx:id="socialIcon3Placeholder" prefHeight="150.0" prefWidth="200.0" styleClass="centerImagesInStackPane" GridPane.rowIndex="1" />
              <BorderPane fx:id="socialIcon4Placeholder" prefHeight="150.0" prefWidth="200.0" styleClass="centerImagesInStackPane" GridPane.columnIndex="1" GridPane.rowIndex="1" />
            </children>
         </GridPane>
         <VBox fx:id="contactDetailsVBox" prefHeight="200.0" prefWidth="100.0" GridPane.columnIndex="1" />
              <StackPane fx:id="schedulePlaceholder" styleClass="panelTitlePadding" GridPane.columnIndex="1" GridPane.rowIndex="1" />
      </children>
   </GridPane>
</StackPane>
```
###### \resources\view\GroupListCard.fxml
``` fxml

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.FlowPane?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.VBox?>

<HBox id="groupCardPane" fx:id="groupCardPane" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
  <GridPane HBox.hgrow="ALWAYS">
    <columnConstraints>
      <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
    </columnConstraints>
    <VBox alignment="CENTER_LEFT" GridPane.columnIndex="0">
      <padding>
        <Insets bottom="5" left="15" right="5" top="5" />
      </padding>
      <HBox alignment="CENTER_LEFT" spacing="5">
        <Label fx:id="groupId" styleClass="cell_big_label">
          <minWidth>
            <!-- Ensures that the label text is never truncated -->
            <Region fx:constant="USE_PREF_SIZE" />
          </minWidth>
        </Label>
        <Label fx:id="groupName" styleClass="cell_big_label" text="\$first" />
      </HBox>
      <FlowPane fx:id="groupTags" />
    </VBox>
      <rowConstraints>
         <RowConstraints />
      </rowConstraints>
  </GridPane>
</HBox>
```
###### \resources\view\GroupListPanel.fxml
``` fxml

<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1" style="-fx-border-width: 1">
   <Label text="Groups" styleClass="panelTitlePadding"/>
  <ListView fx:id="groupListView" VBox.vgrow="ALWAYS" />
</VBox>
```
###### \resources\view\ScheduleListCard.fxml
``` fxml

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.FlowPane?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.VBox?>

<HBox id="scheduleCardPane" fx:id="scheduleCardPane" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
  <GridPane HBox.hgrow="ALWAYS">
    <columnConstraints>
      <ColumnConstraints hgrow="SOMETIMES" minWidth="10" prefWidth="150" />
    </columnConstraints>
    <VBox alignment="CENTER_LEFT" GridPane.columnIndex="0">
      <padding>
        <Insets bottom="5" left="15" right="5" top="5" />
      </padding>
      <HBox alignment="CENTER_LEFT" spacing="5">
        <Label fx:id="scheduleId" styleClass="cell_big_label">
          <minWidth>
            <!-- Ensures that the label text is never truncated -->
            <Region fx:constant="USE_PREF_SIZE" />
          </minWidth>
        </Label>
        <Label fx:id="scheduleName" styleClass="cell_big_label" text="\$first" />
      </HBox>
      <FlowPane fx:id="scheduleTags" />
    </VBox>
      <rowConstraints>
         <RowConstraints />
      </rowConstraints>
  </GridPane>
</HBox>
```
###### \resources\view\ScheduleListPanel.fxml
``` fxml

<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ListView?>
<?import javafx.scene.layout.VBox?>

<VBox style="-fx-border-width: 1; -fx-border-color:black; -fx-border-radius: 10; -fx-background-radius: 10;
 -fx-border-color: white;" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
   <Label fx:id="scheduleLabel" prefWidth="${scheduleLabel.parent.width}" styleClass="panelTitlePadding" text="Schedule" />
  <ListView fx:id="scheduleListView" VBox.vgrow="ALWAYS" />
</VBox>
```
###### \resources\view\UITheme.css
``` css

.individualBorderInSplitPane {
    -fx-border-width: 1;
    -fx-border-color: black;
    -fx-border-radius: 5;
}

.panelTitlePadding{
    -fx-padding: 10 10 10 10 ;
}

.centerImagesInStackPane{
    -fx-background-position: center center;
    -fx-background-repeat: no-repeat;
    -fx-background-size: contain;
    -fx-background-radius: 4;
}
```
