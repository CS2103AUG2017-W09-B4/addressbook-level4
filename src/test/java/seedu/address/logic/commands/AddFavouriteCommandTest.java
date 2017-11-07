package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

//@@author nassy93

public class AddFavouriteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addFavourite_success() throws Exception {
        Person editedPerson = new PersonBuilder().withFavourite(true).withEmail("alice@example.com").build();
        AddFavouriteCommand addFavouriteCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(AddFavouriteCommand.MESSAGE_FAVE_PERSON_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(addFavouriteCommand, model, expectedMessage, expectedModel);
    }
    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AddFavouriteCommand addFavouriteCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(addFavouriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
    @Test
    public void execute_duplicatePerson_failure() {
        AddFavouriteCommand addFavouriteCommand = prepareCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(addFavouriteCommand, model, AddFavouriteCommand.MESSAGE_ALREADY_FAVOURITE);
    }
    /**
     * Returns an {@code ResetPictureCommand} with parameters {@code index}
     **/
    private AddFavouriteCommand prepareCommand(Index index) {
        AddFavouriteCommand addFavouriteCommand = new AddFavouriteCommand(index);
        addFavouriteCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return addFavouriteCommand;
    }
}
