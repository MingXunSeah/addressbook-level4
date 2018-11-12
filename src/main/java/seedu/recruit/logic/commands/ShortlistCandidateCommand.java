package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.commons.events.ui.ShowLastViewedBookRequestEvent;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.tag.Tag;

/**
 * Last stage of Shortlist Command.
 * Shortlists selected candidates for a job offer
 */
public class ShortlistCandidateCommand extends Command {
    public static final String COMMAND_WORD = "confirm";

    public static final String COMMAND_LOGIC_STATE = "ShortlistCandidate";

    public static final String MESSAGE_USAGE = "Enter confirm to confirm shortlist.\n"
            + "Enter cancel to cancel shortlist.\n";

    public static final String MESSAGE_SUCCESS = "Successfully shortlisted candidate: %1$s\n"
            + "for job offer: %2$s, for company: %3$s.\n"
            + "Shortlisting Process Done. You may carry out other commands.\n";

    public static final String MESSAGE_DUPLICATE_CANDIDATE_SHORTLISTED =
            "This candidate is already shortlisted for job offer: %1$s, for company: %2$s.\n"
            + "Please cancel the current shortlisting process.\n";

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);
        Company selectedCompany = SelectCompanyCommand.getSelectedCompany();
        JobOffer selectedJobOffer = SelectJobCommand.getSelectedJobOffer();
        Candidate selectedCandidate = SelectCandidateCommand.getSelectedCandidate();

        // If selected candidate already exists in shortlisted candidate list of the selected job offer
        if (selectedJobOffer.getUniqueCandidateList().contains(selectedCandidate)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CANDIDATE_SHORTLISTED,
                    selectedJobOffer.getJob().value, selectedCompany.getName().value)
                    + MESSAGE_USAGE);
        }

        Candidate selectedShortlistedCandidate = insertShortlistTag(selectedCandidate);

        model.updateCandidate(selectedCandidate, selectedShortlistedCandidate);
        model.shortlistCandidateToJobOffer(selectedShortlistedCandidate, selectedJobOffer);
        model.commitRecruitBook();

        EventsCenter.getInstance().post(new ChangeLogicStateEvent("primary"));

        EventsCenter.getInstance().post(new ShowLastViewedBookRequestEvent());
        if (ShortlistCandidateInitializationCommand.isShortlisting()) {
            ShortlistCandidateInitializationCommand.isDoneShortlisting();
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedCandidate.getName().fullName,
                selectedJobOffer.getJob().value, selectedCompany.getName().value));
    }

    /**
     * Returns the original candidate but the tags have been changed to "SHORTLISTED"
     */
    Candidate insertShortlistTag(Candidate shortlistee) {
        assert shortlistee != null;

        Set<Tag> tags = new HashSet<Tag>();
        Tag shortlistedTag = new Tag("SHORTLISTED");
        tags.add(shortlistedTag);
        return new Candidate(shortlistee.getName(), shortlistee.getGender(), shortlistee.getAge(),
                shortlistee.getPhone(), shortlistee.getEmail(), shortlistee.getAddress(), shortlistee.getDesiredJob(),
                shortlistee.getEducation(), shortlistee.getExpectedSalary(), tags);
    }
}
