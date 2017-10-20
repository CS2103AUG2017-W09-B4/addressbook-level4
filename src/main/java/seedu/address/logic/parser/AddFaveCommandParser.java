package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddFaveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddFaveCommand object
 */
public class AddFaveCommandParser implements Parser<AddFaveCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddFaveCommandCommand
     * and returns an AddFaveCommandCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddFaveCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new AddFaveCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFaveCommand.MESSAGE_USAGE));
        }
    }
}
