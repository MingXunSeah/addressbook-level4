package systemtests;

import static org.junit.Assert.assertFalse;
import static seedu.recruit.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.recruit.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.recruit.testutil.TypicalPersons.BENSON;
import static seedu.recruit.testutil.TypicalPersons.CARL;
import static seedu.recruit.testutil.TypicalPersons.DANIEL;
import static seedu.recruit.testutil.TypicalPersons.KEYWORD_MATCHING_MEIER;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import seedu.recruit.commons.core.index.Index;
import seedu.recruit.logic.commands.DeleteCandidateCommand;
import seedu.recruit.logic.commands.FindCandidateCommand;
import seedu.recruit.logic.commands.RedoCommand;
import seedu.recruit.logic.commands.UndoCommand;
import seedu.recruit.model.Model;
import seedu.recruit.model.tag.Tag;

@Ignore
public class FindCandidateCommandSystemTest extends CandidateBookSystemTest {

    @Test
    public void find() {
        /* Case: find multiple persons in recruit book, command with leading spaces and trailing spaces
         * -> 2 persons found
         */
        String command = "   " + FindCandidateCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER + "   ";
        Model expectedModel = getModel();
        ModelHelper.setCandidateFilteredList(expectedModel,
                BENSON, DANIEL); // first names of Benson and Daniel are "Meier"
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: repeat previous find command where candidate list is displaying the persons we are finding
         * -> 2 persons found
         */
        command = FindCandidateCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /** Case: find candidate where candidate list is not
         *  displaying the candidate we are finding -> 1 candidate found */
        command = FindCandidateCommand.COMMAND_WORD + " Carl";
        ModelHelper.setCandidateFilteredList(expectedModel, CARL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple persons in recruit book, 2 keywords -> 2 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Benson Daniel";
        ModelHelper.setCandidateFilteredList(expectedModel, BENSON, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple persons in recruit book, 2 keywords in reversed order -> 2 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Daniel Benson";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple persons in recruit book, 2 keywords with 1 repeat -> 2 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Daniel Benson Daniel";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple persons in recruit book, 2 matching keywords and 1 non-matching keyword
         * -> 2 persons found
         */
        command = FindCandidateCommand.COMMAND_WORD + " Daniel Benson NonMatchingKeyWord";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: undo previous find command -> rejected */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: redo previous find command -> rejected */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: find same persons in recruit book after deleting 1 of them -> 1 candidate found */
        executeCommand(DeleteCandidateCommand.COMMAND_WORD + " 1");
        assertFalse(getModel().getCandidateBook().getCandidateList().contains(BENSON));
        command = FindCandidateCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setCandidateFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find candidate in recruit book, keyword is same as name but of different case -> 1 candidate found */
        command = FindCandidateCommand.COMMAND_WORD + " MeIeR";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find candidate in recruit book, keyword is substring of name -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Mei";
        ModelHelper.setCandidateFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find candidate in recruit book, name is substring of keyword -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Meiers";
        ModelHelper.setCandidateFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find candidate not in recruit book -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " Mark";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find phone number of candidate in recruit book -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " " + DANIEL.getPhone().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find recruit of candidate in recruit book -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " " + DANIEL.getAddress().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find email of candidate in recruit book -> 0 persons found */
        command = FindCandidateCommand.COMMAND_WORD + " " + DANIEL.getEmail().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find tags of candidate in recruit book -> 0 persons found */
        List<Tag> tags = new ArrayList<>(DANIEL.getTags());
        command = FindCandidateCommand.COMMAND_WORD + " " + tags.get(0).tagName;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find while a candidate is selected -> selected card deselected */
        showAllPersons();
        selectPerson(Index.fromOneBased(1));
        assertFalse(getCandidateDetailsPanel().getHandleToSelectedCard().getName().equals(DANIEL.getName().fullName));
        command = FindCandidateCommand.COMMAND_WORD + " Daniel";
        ModelHelper.setCandidateFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardDeselected();

        /* Case: find candidate in empty recruit book -> 0 persons found */
        deleteAllPersons();
        command = FindCandidateCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setCandidateFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: mixed case command word -> rejected */
        command = "FiNd Meier";
        assertCommandFailure(command, MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays {@code Messages#MESSAGE_PERSONS_LISTED_OVERVIEW} with the number of people in the filtered list,
     * and the model related components equal to {@code expectedModel}.
     * These verifications are done by
     * {@code CandidateBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the status bar remains unchanged, and the command box has the default style class, and the
     * selected card updated accordingly, depending on {@code cardStatus}.
     * @see CandidateBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel) {
        String expectedResultMessage = String.format(
                MESSAGE_PERSONS_LISTED_OVERVIEW, expectedModel.getFilteredCandidateList().size());

        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code CandidateBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see CandidateBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
