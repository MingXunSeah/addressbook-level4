package seedu.recruit.logic.commands.emailcommand;

import static java.util.Objects.requireNonNull;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.CommandResult;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;

/**
 * This class handles the next sub command for email contents phase
 */
public class EmailContentsNextCommand extends EmailContentsCommand {
    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) {
        requireNonNull(model);
        EmailUtil emailUtil = model.getEmailUtil();

        //Check if content array is empty, if it is, do not allow to move on to next stage
        if (isEmpty(emailUtil)) {
            return new CommandResult(NEXT_CONTENTS_ERROR_NO_CONTENTS + EmailContentsCommand.MESSAGE_USAGE);
        } else {
            EventsCenter.getInstance().post(new ChangeLogicStateEvent(EmailSendCommand.COMMAND_LOGIC_STATE));

            return new CommandResult(EmailSendCommand.MESSAGE_USAGE);
        }
    }

    /**
     * checks if contents array is empty
     * @param emailUtil
     * @return boolean value to see if contents array is empty
     */
    private boolean isEmpty(EmailUtil emailUtil) {
        if (emailUtil.getAreRecipientsCandidates() && emailUtil.getJobOffers().size() == 0) {
            return true;
        }
        if (!emailUtil.getAreRecipientsCandidates() && emailUtil.getCandidates().size() == 0) {
            return true;
        }
        return false;
    }
}
