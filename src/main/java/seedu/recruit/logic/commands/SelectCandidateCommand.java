package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.commons.events.ui.FocusOnCandidateBookRequestEvent;
import seedu.recruit.commons.events.ui.JumpToListRequestEvent;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.tag.Tag;

/**
 * Selects a candidate identified using it's displayed index from the recruit book.
 */
public class SelectCandidateCommand extends Command {

    public static final String COMMAND_WORD = "selectc";

    public static final String COMMAND_LOGIC_STATE = "SelectCandidate";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the candidate identified by the index number used in the displayed candidate list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SELECT_PERSON_SUCCESS = "Selected Candidate: %1$s\n";

    public static final String MESSAGE_SELECT_PERSON_FAILURE_DUE_TO_BLACKLIST_TAG =
            "Sorry! You can't shortlist a blacklisted candidate. \n"
            + "Please select again!\n";

    public static final String MESSAGE_CONFIRMATION_FOR_SHORTLIST =
            "for job offer: %1$s, for company: %2$s.\n";

    private static Candidate selectedCandidate;

    private final Index targetIndex;

    public SelectCandidateCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    public static Candidate getSelectedCandidate() {
        return selectedCandidate;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);

        List<Candidate> filteredCandidateList = model.getFilteredCandidateList();

        if (targetIndex.getZeroBased() >= filteredCandidateList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        selectedCandidate = filteredCandidateList.get(targetIndex.getZeroBased());

        if (ShortlistCandidateInitializationCommand.isShortlisting()) {
            EventsCenter.getInstance().post(new JumpToListRequestEvent(targetIndex));

            // If selected candidate is blacklisted
            Tag blacklistTag = new Tag("BLACKLISTED");
            if (selectedCandidate.getTags().contains(blacklistTag)) {
                throw new CommandException(MESSAGE_SELECT_PERSON_FAILURE_DUE_TO_BLACKLIST_TAG
                + MESSAGE_USAGE);
            }

            Company selectedCompany = SelectCompanyCommand.getSelectedCompany();
            JobOffer selectedJobOffer = SelectJobCommand.getSelectedJobOffer();
            EventsCenter.getInstance().post(new ChangeLogicStateEvent(ShortlistCandidateCommand.COMMAND_LOGIC_STATE));

            return new CommandResult(String.format(MESSAGE_SELECT_PERSON_SUCCESS,
                    targetIndex.getOneBased())
                    + String.format(MESSAGE_CONFIRMATION_FOR_SHORTLIST,
                    selectedJobOffer.getJob().value, selectedCompany.getName().value)
                    + ShortlistCandidateCommand.MESSAGE_USAGE);
        }

        EventsCenter.getInstance().post(new FocusOnCandidateBookRequestEvent());
        EventsCenter.getInstance().post(new JumpToListRequestEvent(targetIndex));
        return new CommandResult(String.format(MESSAGE_SELECT_PERSON_SUCCESS, targetIndex.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SelectCandidateCommand // instanceof handles nulls
                && targetIndex.equals(((SelectCandidateCommand) other).targetIndex)); // state check
    }
}
