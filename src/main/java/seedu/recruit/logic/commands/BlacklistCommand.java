package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.commons.events.ui.ShowCandidateBookRequestEvent;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.tag.Tag;

/**
 * Blacklists a candidate in the CandidateBook.
 */
public class BlacklistCommand extends Command {

    public static final String COMMAND_WORD = "blacklist";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Blacklists a candidate in the RecruitBook. "
            + "Parameters: "
            + "INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "To remove a blacklist tag, add \"rm\" before INDEX.\n"
            + "Example: " + COMMAND_WORD + " rm " + "1";

    public static final String MESSAGE_BLACKLIST_SUCCESS = "Blacklisted candidate: %1$s";
    public static final String MESSAGE_UNBLACKLIST_SUCCESS = "Unblacklisted candidate: %1$s";
    public static final String MESSAGE_ALREADY_BLACKLISTED = "This candidate has already been blacklisted!";
    public static final String MESSAGE_IS_NOT_BLACKLISTED = "This candidate is not blacklisted.";
    public static final String MESSAGE_ALREADY_SHORTLISTED = "This candidate has been shortlisted for a job offer!";
    public static final String MESSAGE_WARNING_BLACKLISTED_PERSON = "The selected candidate has been blacklisted!\n"
            + "You can remove the blacklist with [ blacklist rm <INDEX>]!\n"
            + "NOTE: Once a special interface has been initialised such as addj, shortlist or email, you can only use"
            + " commands specified in that interface!";

    private final Index index;
    private final boolean rmCheck;

    /**
     * @param index of the candidate in the filtered candidate list to blacklist
     */

    public BlacklistCommand(boolean rmCheck, Index index) {
        requireNonNull(index);
        this.index = index;
        this.rmCheck = rmCheck;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);
        EventsCenter.getInstance().post(new ShowCandidateBookRequestEvent());

        List<Candidate> lastShownList = model.getFilteredCandidateList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Candidate selectedCandidateBlacklist = lastShownList.get(index.getZeroBased());
        Candidate updatedCandidate;

        if (rmCheck) {
            Tag blacklistedTag = new Tag("BLACKLISTED");
            if (selectedCandidateBlacklist.getTags().contains(blacklistedTag)) {
                throw new CommandException(MESSAGE_ALREADY_BLACKLISTED);
            }
            Tag shortlistedTag = new Tag("SHORTLISTED");
            if (selectedCandidateBlacklist.getTags().contains(shortlistedTag)) {
                throw new CommandException(MESSAGE_ALREADY_SHORTLISTED);
            }

            updatedCandidate = insertBlacklistTag(selectedCandidateBlacklist);
        } else {
            Tag blacklistedTag = new Tag("BLACKLISTED");
            if (!selectedCandidateBlacklist.getTags().contains(blacklistedTag)) {
                throw new CommandException(MESSAGE_IS_NOT_BLACKLISTED);
            }

            updatedCandidate = removeBlacklistTag(selectedCandidateBlacklist);
        }

        model.updateCandidate(selectedCandidateBlacklist, updatedCandidate);
        model.commitRecruitBook();
        if  (rmCheck) {
            return new CommandResult(String.format(MESSAGE_BLACKLIST_SUCCESS, updatedCandidate));
        } else {
            return new CommandResult(String.format(MESSAGE_UNBLACKLIST_SUCCESS, updatedCandidate));
        }
    }

    /**
     * Returns the original candidate but the tags have been changed to "BLACKLISTED"
     */
    public static Candidate insertBlacklistTag(Candidate blacklistee) {
        assert blacklistee != null;

        Set<Tag> tags = new HashSet<Tag>();
        Tag blacklistedTag = new Tag("BLACKLISTED");
        tags.add(blacklistedTag);
        return new Candidate(blacklistee.getName(), blacklistee.getGender(), blacklistee.getAge(),
                blacklistee.getPhone(), blacklistee.getEmail(), blacklistee.getAddress(), blacklistee.getDesiredJob(),
                blacklistee.getEducation(), blacklistee.getExpectedSalary(), tags);
    }

    /**
     * Returns the candidate but the "BLACKLISTED" tag has been removed
     */
    public static Candidate removeBlacklistTag(Candidate blacklistee) {
        assert blacklistee != null;

        Set<Tag> tags = new HashSet<Tag>();
        return new Candidate(blacklistee.getName(), blacklistee.getGender(), blacklistee.getAge(),
                blacklistee.getPhone(), blacklistee.getEmail(), blacklistee.getAddress(), blacklistee.getDesiredJob(),
                blacklistee.getEducation(), blacklistee.getExpectedSalary(), tags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BlacklistCommand // instanceof handles nulls
                && index.equals(((BlacklistCommand) other).index)); // state check
    }
}
