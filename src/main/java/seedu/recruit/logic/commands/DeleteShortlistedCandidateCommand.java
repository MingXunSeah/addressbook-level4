package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.logic.CommandHistory;

import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.tag.Tag;

/**
 * Deletes a candidate identified using its displayed index
 * from the list of shortlisted candidates of a selected job offer.
 */
public class DeleteShortlistedCandidateCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String COMMAND_LOGIC_STATE = "DeleteShortlistedCandidate";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the candidate identified by the index number used in the shortlisted candidate list.\n"
            + "Parameters: INDEX (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_CANDIDATE_SUCCESS =
            "Deleted Candidate: %1$s from shortlisted list for Job: %2$s of Company: %3$s.\n"
            + "Delete Process for shortlisted candidates done. You may carry out other commands.\n";

    public static final String MESSAGE_EMPTY_LIST =
            "ERROR: No candidate shortlisted for this job offer yet.\n"
            + "Exited from delete process.\n";

    private final Index targetIndex;

    public DeleteShortlistedCandidateCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);

        Company selectedCompany = SelectCompanyCommand.getSelectedCompany();
        JobOffer selectedJob = SelectJobCommand.getSelectedJobOffer();

        // If there are no candidates inside the list of shortlisted candidates.
        if (selectedJob.getObservableCandidateList().isEmpty()) {
            EventsCenter.getInstance().post(new ChangeLogicStateEvent("primary"));
            return new CommandResult(MESSAGE_EMPTY_LIST);
        }

        // If targetIndex exceeds the size of the shortlisted candidate list
        if (targetIndex.getZeroBased() >= selectedJob.getObservableCandidateList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Candidate selectedCandidate = selectedJob.getObservableCandidateList().get(targetIndex.getZeroBased());
        Candidate removedShortlistedTagFromCandidate = removeShortlistTag(selectedCandidate);

        model.deleteShortlistedCandidateFromJobOffer(selectedCandidate, selectedJob);
        model.updateCandidate(selectedCandidate, removedShortlistedTagFromCandidate);
        model.commitRecruitBook();


        if (DeleteShortlistedCandidateInitializationCommand.isDeleting()) {
            DeleteShortlistedCandidateInitializationCommand.isDoneDeleting();
        }
        EventsCenter.getInstance().post(new ChangeLogicStateEvent("primary"));
        return new CommandResult(String.format(MESSAGE_DELETE_CANDIDATE_SUCCESS,
                removedShortlistedTagFromCandidate.getName().fullName, selectedJob.getJob().value,
                selectedCompany.getName().value));
    }

    /**
     * Returns the candidate but the "SHORTLISTED" tag has been removed
     */
    Candidate removeShortlistTag(Candidate shortlistee) {
        assert shortlistee != null;

        Set<Tag> tags = new HashSet<Tag>();
        return new Candidate(shortlistee.getName(), shortlistee.getGender(), shortlistee.getAge(),
                shortlistee.getPhone(), shortlistee.getEmail(), shortlistee.getAddress(), shortlistee.getDesiredJob(),
                shortlistee.getEducation(), shortlistee.getExpectedSalary(), tags);
    }

}
