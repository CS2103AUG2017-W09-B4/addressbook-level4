package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.ListEvent;
import seedu.address.model.group.ReadOnlyGroup;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.NoPersonsException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.schedule.ReadOnlySchedule;
import seedu.address.model.schedule.exceptions.DuplicateScheduleException;
import seedu.address.model.schedule.exceptions.ScheduleNotFoundException;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final FilteredList<ReadOnlyPerson> filteredPersons;
    private final FilteredList<ReadOnlyGroup> filteredGroups;
    private final FilteredList<ReadOnlySchedule> filteredSchedules;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredGroups = new FilteredList<>(this.addressBook.getGroupList());
        filteredSchedules = new FilteredList<>(this.addressBook.getScheduleList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    /** Raises an event to indicate a list command has occurred */
    private void indicateListEvent() {
        raise(new ListEvent(getFilteredPersonList()));
    }

    @Override
    public synchronized void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
        addressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void deletePersons(ReadOnlyPerson[] targets) throws PersonNotFoundException {
        for( ReadOnlyPerson target : targets ) {
            addressBook.removePerson(target);
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, editedPerson);
        addressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

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

    @Override
    public void addSchedule(ReadOnlySchedule schedule) throws DuplicateScheduleException {
        addressBook.addSchedule(schedule);
        updateFilteredScheduleList(PREDICATE_SHOW_ALL_SCHEDULES);

    public void addPersonToGroup(Index targetGroup, ReadOnlyPerson toAdd)
            throws GroupNotFoundException, PersonNotFoundException, DuplicatePersonException {
        addressBook.addPersonToGroup(targetGroup, toAdd);

        indicateAddressBookChanged();
    }

    @Override
    public void deleteSchedule(ReadOnlySchedule target) throws ScheduleNotFoundException {
        addressBook.removeSchedule(target);
        indicateAddressBookChanged();
    }

    public void deletePersonFromGroup(Index targetGroup, ReadOnlyPerson toRemove)
            throws GroupNotFoundException, PersonNotFoundException, NoPersonsException {

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

    @Override
    public ObservableList<ReadOnlyGroup> getFilteredGroupList() {
        return FXCollections.unmodifiableObservableList(filteredGroups);
    }

    @Override

    public ObservableList<ReadOnlySchedule> getFilteredScheduleList() {
        return FXCollections.unmodifiableObservableList(filteredSchedules);

    public void showUnfilteredPersonList() {
        filteredPersons.setPredicate(PREDICATE_SHOW_ALL_PERSONS);
        indicateListEvent();
    }

    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
        indicateListEvent();
    }


    @Override
    public void updateFilteredGroupList(Predicate<ReadOnlyGroup> predicate) {
        requireNonNull(predicate);
        filteredGroups.setPredicate(predicate);
    }

    @Override
    public void updateFilteredScheduleList(Predicate<ReadOnlySchedule> predicate) {
        requireNonNull(predicate);
        filteredSchedules.setPredicate(predicate);
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
                && filteredGroups.equals(other.filteredGroups);
    }

}
