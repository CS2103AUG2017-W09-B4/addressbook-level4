# Procrastinatus
###### \java\guitests\AddressBookGuiTest.java
``` java
    protected GroupListPanelHandle getGroupListPanel() {
        return mainWindowHandle.getGroupListPanel();
    }
```
###### \java\guitests\guihandles\GroupCardHandle.java
``` java

/**
 * Provides a handle to a person card in the person list panel.
 */
public class GroupCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#groupId";
    private static final String NAME_FIELD_ID = "#groupName";

    private final Label idLabel;
    private final Label nameLabel;

    public GroupCardHandle(Node cardNode) {
        super(cardNode);
        this.idLabel = getChildNode(ID_FIELD_ID);
        this.nameLabel = getChildNode(NAME_FIELD_ID);
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

}
```
###### \java\guitests\guihandles\GroupListPanelHandle.java
``` java

/**
 * Provides a handle for {@code GroupListPanel} containing the list of {@code GroupCard}.
 */
public class GroupListPanelHandle extends NodeHandle<ListView<GroupCard>> {
    public static final String GROUP_LIST_VIEW_ID = "#groupListView";

    private Optional<GroupCard> lastRememberedSelectedGroupCard;

    public GroupListPanelHandle(ListView<GroupCard> groupListPanelNode) {
        super(groupListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code GroupCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     */
    public GroupCardHandle getHandleToSelectedCard() {
        List<GroupCard> groupList = getRootNode().getSelectionModel().getSelectedItems();
        if (groupList.size() != 1) {
            throw new AssertionError("Group list size expected 1.");
        }

        return new GroupCardHandle(groupList.get(0).getRoot());
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<GroupCard> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
    }

    /**
     * Navigates the listview to display and select the group.
     */
    public void navigateToCard(ReadOnlyGroup group) {
        List<GroupCard> cards = getRootNode().getItems();
        Optional<GroupCard> matchingCard = cards.stream().filter(card -> card.group.equals(group)).findFirst();

        if (!matchingCard.isPresent()) {
            throw new IllegalArgumentException("Group does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(matchingCard.get());
            getRootNode().getSelectionModel().select(matchingCard.get());
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Returns the group card handle of a group associated with the {@code index} in the list.
     */
    public GroupCardHandle getGroupCardHandle(int index) {
        return getGroupCardHandle(getRootNode().getItems().get(index).group);
    }

    /**
     * Returns the {@code GroupCardHandle} of the specified {@code group} in the list.
     */
    public GroupCardHandle getGroupCardHandle(ReadOnlyGroup group) {
        Optional<GroupCardHandle> handle = getRootNode().getItems().stream()
                .filter(card -> card.group.equals(group))
                .map(card -> new GroupCardHandle(card.getRoot()))
                .findFirst();
        return handle.orElseThrow(() -> new IllegalArgumentException("Group does not exist."));
    }

    /**
     * Selects the {@code GroupCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
    }

    /**
     * Remembers the selected {@code GroupCard} in the list.
     */
    public void rememberSelectedGroupCard() {
        List<GroupCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedGroupCard = Optional.empty();
        } else {
            lastRememberedSelectedGroupCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code GroupCard} is different from the value remembered by the most recent
     * {@code rememberSelectedGroupCard()} call.
     */
    public boolean isSelectedGroupCardChanged() {
        List<GroupCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedGroupCard.isPresent();
        } else {
            return !lastRememberedSelectedGroupCard.isPresent()
                    || !lastRememberedSelectedGroupCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
```
###### \java\seedu\address\logic\commands\AddCommandTest.java
``` java
        @Override
        public void addSchedule(ReadOnlySchedule schedule) throws DuplicateScheduleException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteSchedule(ReadOnlySchedule schedule) throws ScheduleNotFoundException {
            fail("This method should not be called.");
        }

```
###### \java\seedu\address\logic\commands\AddCommandTest.java
``` java
        @Override
        public void deletePersons(ReadOnlyPerson[] targets) throws PersonNotFoundException {
            fail("This method should not be called.");
        }
```
###### \java\seedu\address\logic\commands\AddCommandTest.java
``` java
        @Override
        public ObservableList<ReadOnlySchedule> getFilteredScheduleList() {
            return null;
        }
```
###### \java\seedu\address\logic\commands\AddCommandTest.java
``` java
        @Override
        public void updateFilteredScheduleList(Predicate<ReadOnlySchedule> predicate) {
            fail("This method should not be called.");
        }
```
###### \java\seedu\address\model\group\GroupNameTest.java
``` java

public class GroupNameTest {

    @Test
    public void isValidName() {
        // invalid name
        assertFalse(GroupName.isValidName("")); // empty string
        assertFalse(GroupName.isValidName(" ")); // spaces only
        assertFalse(GroupName.isValidName("^")); // only non-alphanumeric characters
        assertFalse(GroupName.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        assertTrue(GroupName.isValidName("peter jack")); // alphabets only
        assertTrue(GroupName.isValidName("12345")); // numbers only
        assertTrue(GroupName.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(GroupName.isValidName("Capital Tan")); // with capital letters
        assertTrue(GroupName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }
}
```
###### \java\seedu\address\model\UniqueGroupListTest.java
``` java

public class UniqueGroupListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void asObservableListModifyListThrowsUnsupportedOperationException() {
        UniqueGroupList uniqueGroupList = new UniqueGroupList();
        thrown.expect(UnsupportedOperationException.class);
        uniqueGroupList.asObservableList().remove(0);
    }
}
```
###### \java\seedu\address\testutil\GroupBuilder.java
``` java

/**
 * A utility class to help with building Group objects.
 */
public class GroupBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline Group";

    private Group group;

    public GroupBuilder() {
        try {
            GroupName defaultName = new GroupName(DEFAULT_NAME);
            this.group = new Group(defaultName);
        } catch (IllegalValueException ive) {
            throw new AssertionError("Default group's values are invalid.");
        }
    }

    /**
     * Initializes the GroupBuilder with the data of {@code groupToCopy}.
     */
    public GroupBuilder(ReadOnlyGroup groupToCopy) {
        this.group = new Group(groupToCopy);
    }

    /**
     * Sets the {@code Name} of the {@code Group} that we are building.
     */
    public GroupBuilder withName(String name) {
        try {
            this.group.setGroupName(new GroupName(name));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("name is expected to be unique.");
        }
        return this;
    }

    public Group build() {
        return this.group;
    }

}
```
###### \java\seedu\address\testutil\GroupUtil.java
``` java

/**
 * A utility class for Group.
 */
public class GroupUtil {

    /**
     * Returns an add command string for adding the {@code group}.
     */
    public static String getAddCommand(ReadOnlyGroup group) {
        return AddCommand.COMMAND_WORD + " " + getGroupDetails(group);
    }

    /**
     * Returns the part of command string for the given {@code group}'s details.
     */
    public static String getGroupDetails(ReadOnlyGroup group) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + group.getName().fullName + " ");
        return sb.toString();
    }
}
```
###### \java\seedu\address\testutil\TypicalGroups.java
``` java

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalGroups {

    public static final ReadOnlyGroup ALICE_GROUP = new GroupBuilder().withName("Alice Pauline Group").build();
    public static final ReadOnlyGroup BENSON_GROUP = new GroupBuilder().withName("Benson Meier Group").build();
    public static final ReadOnlyGroup CARL_GROUP = new GroupBuilder().withName("Carl Kurz Group").build();
    public static final ReadOnlyGroup DANIEL_GROUP = new GroupBuilder().withName("Daniel Meier Group").build();
    public static final ReadOnlyGroup ELLE_GROUP = new GroupBuilder().withName("Elle Meyer Group").build();
    public static final ReadOnlyGroup FIONA_GROUP = new GroupBuilder().withName("Fiona Kunz Group").build();
    public static final ReadOnlyGroup GEORGE_GROUP = new GroupBuilder().withName("George Best Group").build();

    // Manually added
    public static final ReadOnlyGroup HOON_GROUP = new GroupBuilder().withName("Hoon Meier Group").build();
    public static final ReadOnlyGroup IDA_GROUP = new GroupBuilder().withName("Ida Mueller Group").build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final ReadOnlyGroup AMY_GROUP = new GroupBuilder().withName(VALID_NAME_AMY).build();
    public static final ReadOnlyGroup BOB_GROUP = new GroupBuilder().withName(VALID_NAME_BOB).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier Group"; // A keyword that matches MEIER

    private TypicalGroups() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical groups.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (ReadOnlyGroup group : getTypicalGroups()) {
            try {
                ab.addGroup(group);
            } catch (DuplicateGroupException e) {
                assert false : "not possible";
            }
        }
        return ab;
    }

    public static List<ReadOnlyGroup> getTypicalGroups() {
        return new ArrayList<>(Arrays.asList(
                ALICE_GROUP, BENSON_GROUP, CARL_GROUP, DANIEL_GROUP, ELLE_GROUP, FIONA_GROUP, GEORGE_GROUP));
    }

    //Methods for getEmptyAddressBook() and getSortedAddressBook() are removed unlike in TypicalPersons
    //as there's no equivalent usage in SortCommandTest.

}
```
###### \java\seedu\address\ui\GroupCardTest.java
``` java

public class GroupCardTest extends GuiUnitTest {

    @Test
    public void display() {
        Group group = new GroupBuilder().build();
        GroupCard groupCard = new GroupCard(group, 1);
        uiPartRule.setUiPart(groupCard);
        assertCardDisplay(groupCard, group, 1);

        Group group2 = new GroupBuilder().build();
        groupCard = new GroupCard(group2, 2);
        uiPartRule.setUiPart(groupCard);
        assertCardDisplay(groupCard, group2, 2);

        // changes made to Group reflects on card
        guiRobot.interact(() -> {
            group2.setGroupName(ALICE_GROUP.getName());
        });
        assertCardDisplay(groupCard, group2, 2);
    }

    @Test
    public void equals() {
        Group group = new GroupBuilder().build();
        GroupCard groupCard = new GroupCard(group, 0);

        // same group, same index -> returns true
        GroupCard copy = new GroupCard(group, 0);
        assertTrue(groupCard.equals(copy));

        // same object -> returns true
        assertTrue(groupCard.equals(groupCard));

        // null -> returns false
        assertFalse(groupCard == null);

        // different types -> returns false
        assertFalse(groupCard.equals(0));

        // different group, same index -> returns false
        Group differentGroup = new GroupBuilder().withName("differentName").build();
        assertFalse(groupCard.equals(new GroupCard(differentGroup, 0)));

        // same group, different index -> returns false
        assertFalse(groupCard.equals(new GroupCard(group, 1)));
    }

    /**
     * Asserts that {@code groupCard} displays the details of {@code expectedGroup} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(GroupCard groupCard, ReadOnlyGroup expectedGroup, int expectedId) {
        guiRobot.pauseForHuman();

        GroupCardHandle groupCardHandle = new GroupCardHandle(groupCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", groupCardHandle.getId());

        // verify group details are displayed correctly
        assertCardDisplaysGroup(expectedGroup, groupCardHandle);
    }
}
```
###### \java\seedu\address\ui\GroupListPanelTest.java
``` java

public class GroupListPanelTest extends GuiUnitTest {
    private static final ObservableList<ReadOnlyGroup> TYPICAL_GROUPS =
            FXCollections.observableList(getTypicalGroups());

    private static final JumpToGroupListRequestEvent JUMP_TO_SECOND_EVENT =
            new JumpToGroupListRequestEvent(INDEX_SECOND_GROUP);

    private GroupListPanelHandle groupListPanelHandle;

    @Before
    public void setUp() {
        GroupListPanel groupListPanel = new GroupListPanel(TYPICAL_GROUPS);
        uiPartRule.setUiPart(groupListPanel);

        groupListPanelHandle = new GroupListPanelHandle(getChildNode(groupListPanel.getRoot(),
                GroupListPanelHandle.GROUP_LIST_VIEW_ID));
    }

    @Test
    public void display() {
        for (int i = 0; i < TYPICAL_GROUPS.size(); i++) {
            groupListPanelHandle.navigateToCard(TYPICAL_GROUPS.get(i));
            ReadOnlyGroup expectedGroup = TYPICAL_GROUPS.get(i);
            GroupCardHandle actualCard = groupListPanelHandle.getGroupCardHandle(i);

            assertCardDisplaysGroup(expectedGroup, actualCard);
            assertEquals(Integer.toString(i + 1) + ". ", actualCard.getId());
        }
    }

    @Test
    public void handleJumpToGroupListRequestEvent() {
        postNow(JUMP_TO_SECOND_EVENT);
        guiRobot.pauseForHuman();

        GroupCardHandle expectedCard = groupListPanelHandle.getGroupCardHandle(INDEX_SECOND_GROUP.getZeroBased());
        GroupCardHandle selectedCard = groupListPanelHandle.getHandleToSelectedCard();
        assertCardEquals(expectedCard, selectedCard);
    }
}
```
