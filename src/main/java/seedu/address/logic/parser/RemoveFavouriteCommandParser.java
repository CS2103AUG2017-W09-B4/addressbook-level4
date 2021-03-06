package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.RemoveFavouriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author nassy93
/**
 * Parses input arguments and creates a new RemoveFaveCommand object
 */
public class RemoveFavouriteCommandParser implements Parser<RemoveFavouriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RemoveFaveCommand
     * and returns an RemoveFaveCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
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
