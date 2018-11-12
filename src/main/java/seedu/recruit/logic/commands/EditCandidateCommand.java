package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_EDUCATION;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_JOB;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_SALARY;
import static seedu.recruit.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.recruit.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.commons.events.ui.ShowCandidateBookRequestEvent;
import seedu.recruit.commons.util.CollectionUtil;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Age;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.candidate.Education;
import seedu.recruit.model.candidate.Gender;
import seedu.recruit.model.candidate.Name;
import seedu.recruit.model.commons.Address;
import seedu.recruit.model.commons.Email;
import seedu.recruit.model.commons.Phone;
import seedu.recruit.model.joboffer.Job;
import seedu.recruit.model.joboffer.Salary;
import seedu.recruit.model.tag.Tag;

/**
 * Edits the details of an existing candidate in the recruit book.
 */
public class EditCandidateCommand extends Command {

    public static final String COMMAND_WORD = "editc";


    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the candidate identified "
            + "by the index number used in the displayed candidate list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_GENDER + "GENDER] "
            + "[" + PREFIX_AGE + "AGE] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_JOB + "JOB] "
            + "[" + PREFIX_EDUCATION + "EDUCATION] "
            + "[" + PREFIX_SALARY + "SALARY] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Candidate: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This candidate already exists in the recruit book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the candidate in the filtered candidate list to edit
     * @param editPersonDescriptor details to edit the candidate with
     */
    public EditCandidateCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);
        EventsCenter.getInstance().post(new ShowCandidateBookRequestEvent());

        List<Candidate> lastShownList = model.getFilteredCandidateList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Candidate candidateToEdit = lastShownList.get(index.getZeroBased());
        Candidate editedCandidate = createEditedPerson(candidateToEdit, editPersonDescriptor);

        Tag blacklistedTag = new Tag("BLACKLISTED");
        if (candidateToEdit.getTags().contains(blacklistedTag)) {
            throw new CommandException(BlacklistCommand.MESSAGE_WARNING_BLACKLISTED_PERSON);
        }

        if (!candidateToEdit.isSameCandidate(editedCandidate) && model.hasCandidate(editedCandidate)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.updateCandidate(candidateToEdit, editedCandidate);
        model.updateFilteredCandidateList(PREDICATE_SHOW_ALL_PERSONS);
        model.commitRecruitBook();

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedCandidate));
    }

    /**
     * Creates and returns a {@code Candidate} with the details of {@code candidateToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Candidate createEditedPerson(Candidate candidateToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert candidateToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(candidateToEdit.getName());
        Gender updatedGender = editPersonDescriptor.getGender().orElse(candidateToEdit.getGender());
        Age updatedAge = editPersonDescriptor.getAge().orElse(candidateToEdit.getAge());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(candidateToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(candidateToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(candidateToEdit.getAddress());
        Job updatedJob = editPersonDescriptor.getJob().orElse(candidateToEdit.getDesiredJob());
        Education updatedEducation = editPersonDescriptor.getEducation().orElse(candidateToEdit.getEducation());
        Salary updatedSalary = editPersonDescriptor.getSalary().orElse(candidateToEdit.getExpectedSalary());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(candidateToEdit.getTags());

        return new Candidate(updatedName, updatedGender, updatedAge, updatedPhone, updatedEmail, updatedAddress,
            updatedJob, updatedEducation, updatedSalary, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCandidateCommand)) {
            return false;
        }

        // state check
        EditCandidateCommand e = (EditCandidateCommand) other;
        return index.equals(e.index)
                && editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the candidate with. Each non-empty field value will replace the
     * corresponding field value of the candidate.
     */

    public static class EditPersonDescriptor {
        private Name name;
        private Gender gender;
        private Age age;
        private Phone phone;
        private Email email;
        private Address address;
        private Job job;
        private Education education;
        private Salary salary;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setGender(toCopy.gender);
            setAge(toCopy.age);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setJob(toCopy.job);
            setEducation(toCopy.education);
            setSalary(toCopy.salary);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, gender, age, phone, email, address, job, education, salary, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public Optional<Gender> getGender() {
            return Optional.ofNullable(gender);
        }

        public void setAge(Age age) {
            this.age = age;
        }

        public Optional<Age> getAge() {
            return Optional.ofNullable(age);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setJob(Job job) {
            this.job = job;
        }

        public Optional<Job> getJob() {
            return Optional.ofNullable(job);
        }

        public void setEducation(Education education) {
            this.education = education;
        }

        public Optional<Education> getEducation() {
            return Optional.ofNullable(education);
        }

        public void setSalary(Salary salary) {
            this.salary = salary;
        }

        public Optional<Salary> getSalary() {
            return Optional.ofNullable(salary);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getGender().equals(e.getGender())
                    && getAge().equals(e.getAge())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getJob().equals(e.getJob())
                    && getEducation().equals(e.getEducation())
                    && getSalary().equals(e.getSalary())
                    && getTags().equals(e.getTags());
        }
    }
}
