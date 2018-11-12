package seedu.recruit.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;

import seedu.recruit.commons.core.ComponentManager;
import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.commons.events.ui.CompanyListDetailsPanelSelectionChangedEvent;
import seedu.recruit.commons.events.ui.ShowUpdatedCompanyJobListRequestEvent;
import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.commands.AuthenticateUserCommand;
import seedu.recruit.logic.commands.Command;
import seedu.recruit.logic.commands.CommandResult;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.logic.parser.RecruitBookParser;
import seedu.recruit.logic.parser.exceptions.ParseException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.joboffer.JobOfferContainsFindKeywordsPredicate;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {

    private static LogicState state = new LogicState("authenticate");

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final CommandHistory history;
    private final RecruitBookParser recruitBookParser;
    private final UserPrefs userPrefs;

    public LogicManager(Model model, UserPrefs userPrefs) {
        this.model = model;
        this.userPrefs = userPrefs;
        history = new CommandHistory();
        recruitBookParser = new RecruitBookParser();
        if (userPrefs.getHashedPassword() == null) {
            state = new LogicState("primary");
        }
    }

    public static LogicState getState() {
        return state;
    }

    @Override
    public CommandResult execute(String commandText)
            throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            EmailUtil emailUtil = model.getEmailUtil();
            if (state.nextCommand == "authenticate") {
                return new AuthenticateUserCommand(commandText).execute(model, history, userPrefs);
            }
            Command command = recruitBookParser.parseCommand(commandText, state, emailUtil, userPrefs);
            return command.execute(model, history, userPrefs);
        } finally {
            history.add(commandText);
        }
    }

    private void setLogicState(LogicState newState) {
        state = newState;
    }

    @Override
    public ObservableList<Candidate> getFilteredPersonList() {
        return model.getFilteredCandidateList();
    }

    @Override
    public ObservableList<Company> getFilteredCompanyList() {
        return model.getFilteredCompanyList();
    }

    @Override
    public ObservableList<JobOffer> getFilteredCompanyJobList() {
        return model.getFilteredCompanyJobList();
    }

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }

    @Override
    public ObservableList<Candidate> getMasterCandidateList() {
        return model.getMasterCandidateList();
    }

    @Override
    public ObservableList<JobOffer> getMasterJobList() {
        return model.getMasterJobList();
    }

    @Subscribe
    private void handleCompanyListDetailsPanelSelectionChangedEvent(CompanyListDetailsPanelSelectionChangedEvent
                                                                            event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Selection Changed to " + event.getNewSelection().getName().value));
        HashMap<String, List<String>> keywordsList = new HashMap<>();
        List<String> companyName = new ArrayList<>();
        companyName.add(event.getNewSelection().getName().toString());
        keywordsList.put("CompanyName", companyName);
        model.updateFilteredCompanyJobList(new JobOfferContainsFindKeywordsPredicate(keywordsList));
        EventsCenter.getInstance().post(new ShowUpdatedCompanyJobListRequestEvent(
                model.getFilteredCompanyJobList().size()));
    }

    @Subscribe
    private void handleChangeLogicStateEvent(ChangeLogicStateEvent event) {
        setLogicState(event.newState);
    }

}
