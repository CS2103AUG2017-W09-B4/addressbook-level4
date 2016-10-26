package seedu.taskscheduler.model.task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.taskscheduler.commons.exceptions.DuplicateDataException;
import seedu.taskscheduler.commons.exceptions.IllegalValueException;
import seedu.taskscheduler.commons.util.CollectionUtil;
import seedu.taskscheduler.model.tag.UniqueTagList.DuplicateTagException;

import java.util.*;

/**
 * A list of tasks that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Task#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class UniqueTaskList implements Iterable<Task> {

    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateTaskException extends DuplicateDataException {
        protected DuplicateTaskException() {
            super("Operation would result in duplicate tasks");
        }
    }

    /**
     * Signals that an operation targeting a specified task in the list would fail because
     * there is no such matching task in the list.
     */
    public static class TaskNotFoundException extends Exception {
        public TaskNotFoundException() {
            
        }
        public TaskNotFoundException(String message) {
            super(message);
        }
    }

    private final ObservableList<Task> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty TaskList.
     */
    public UniqueTaskList() {}

    /**
     * Returns true if the list contains an equivalent task as the given argument.
     */
    public boolean contains(ReadOnlyTask toCheck) {
        assert toCheck != null;
        return internalList.contains(toCheck);
    }

    /**
     * Adds a task to the list.
     *
     * @throws DuplicateTaskException if the task to add is a duplicate of an existing task in the list.
     */
    public void add(Task toAdd) throws DuplicateTaskException {
        assert toAdd != null;
        if (contains(toAdd)) {
            throw new DuplicateTaskException();
        }
        internalList.add(toAdd);
    }

    //@@author A0148145E
    /**
     * Replaces a task in the list.
     *
     * @throws DuplicateTaskException if the task to replace is a duplicate of an existing task in the list.
     */
    public void replace(Task oldTask, Task newTask) throws DuplicateTaskException, TaskNotFoundException{
        assert oldTask != null;
        if (contains(newTask)) {
            throw new DuplicateTaskException();
        }
        int index = internalList.indexOf(oldTask);
        if (index < 0) {
            throw new TaskNotFoundException();
        }
        internalList.set(index, newTask);
    }

    //@@author A0148145E
    /**
     * Marks a task to the list as completed.
     *
     * @throws TaskNotFoundException
     * @throws IllegalValueException if the task is already complete.
     */
    public void mark(Task toMark) throws TaskNotFoundException, IllegalValueException{
        assert toMark != null;
        int index = internalList.indexOf(toMark);
        if (index < 0) {
            throw new TaskNotFoundException();
        }
        toMark.markComplete();
        internalList.set(index, toMark);
    }
    

    //@@author A0148145E
    /**
     * Unmarks a task to the list as completed.
     *
     * @throws TaskNotFoundException
     * @throws DuplicateTagException if the task is already complete.
     */
    public void unMark(Task toMark) throws TaskNotFoundException, IllegalValueException {
        assert toMark != null;
        int index = internalList.indexOf(toMark);
        if (index < 0) {
            throw new TaskNotFoundException();
        }
        toMark.unMarkComplete();
        internalList.set(index, toMark);
    }
    
    //@@author A0140007B
    /**
     * Insert a task into another task's position in the list.
     *
     * @throws TaskNotFoundException
     */
    public void insert(int index, Task newTask) throws TaskNotFoundException {
        assert newTask != null;
        assert index > 0;
        internalList.add(index-1, newTask);
    }
    //@@author
    
    /**
     * Removes the equivalent task from the list.
     *
     * @throws TaskNotFoundException if no such task could be found in the list.
     */
    public boolean remove(ReadOnlyTask toRemove) throws TaskNotFoundException {
        assert toRemove != null;
        final boolean taskFoundAndDeleted = internalList.remove(toRemove);
        if (!taskFoundAndDeleted) {
            throw new TaskNotFoundException();
        }
        return taskFoundAndDeleted;
    }

    public ObservableList<Task> getInternalList() {
        return internalList;
    }

    @Override
    public Iterator<Task> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueTaskList // instanceof handles nulls
                && this.internalList.equals(
                ((UniqueTaskList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
