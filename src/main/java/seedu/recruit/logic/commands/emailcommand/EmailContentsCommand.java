package seedu.recruit.logic.commands.emailcommand;

import static seedu.recruit.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.Command;
import seedu.recruit.logic.commands.CommandResult;
import seedu.recruit.logic.parser.exceptions.ParseException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;

/**
 * 3rd step of the Email command: selecting contents
 */
public class EmailContentsCommand extends Command {
    public static final String MESSAGE_USAGE = "Find/Filter the contents you intend to email. "
            + "All items listed below will be added into the contents field.\n"
            + "Type \"add\" to add contents to the field.\n"
            + "Type \"next\" when you have finished adding contents to move on to the next step.\n"
            + "Type \"back\" to return to select recipients command.\n"
            + "Type \"cancel\" to cancel the email command.";
    public static final String COMMAND_LOGIC_STATE = "EmailSelectContents";
    public static final String ADD_CONTENTS_DUPLICATE_MESSAGE =
            "Unable to add the following because it already has been added before:\n";
    public static final String ADD_CONTENTS_CONTENTS_ADDED = "Contents added:\n";
    public static final String ADD_CONTENTS_NOTHING_SELECTED = "ERROR: Nothing was selected!\n";
    public static final String NEXT_CONTENTS_ERROR_NO_CONTENTS = "ERROR: There are no contents selected!\n";

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws ParseException {
        throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
    }
}
