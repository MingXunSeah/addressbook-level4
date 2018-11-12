package seedu.recruit.model.candidate;

import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.recruit.model.commons.Address;
import seedu.recruit.model.commons.Email;
import seedu.recruit.model.commons.Phone;
import seedu.recruit.model.joboffer.Job;
import seedu.recruit.model.joboffer.Salary;
import seedu.recruit.model.tag.Tag;

/**
 * Represents a Candidate in the candidate book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Candidate {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Gender gender;

    // Data fields
    private final Age age;
    private final Address address;
    private final Job desiredJob;
    private final Education education;
    private final Salary expectedSalary;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */

    public Candidate(Name name, Gender gender, Age age, Phone phone, Email email, Address address,
                     Job desiredJob, Education education, Salary expectedSalary, Set<Tag> tags) {
        requireAllNonNull(name, gender, phone, email, address, desiredJob, education, expectedSalary, tags);
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.desiredJob = desiredJob;
        this.education = education;
        this.expectedSalary = expectedSalary;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public Age getAge() {
        return age;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Job getDesiredJob() {
        return desiredJob;
    }

    public Education getEducation() {
        return education;
    }

    public Salary getExpectedSalary() {
        return expectedSalary;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons of the same name and gender have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSameCandidate(Candidate otherCandidate) {
        if (otherCandidate == this) {
            return true;
        }

        return otherCandidate != null
                && otherCandidate.getName().equals(getName())
                && otherCandidate.getGender().equals(getGender())
                && (otherCandidate.getPhone().equals(getPhone()) || otherCandidate.getEmail().equals(getEmail()));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Candidate)) {
            return false;
        }

        Candidate otherCandidate = (Candidate) other;
        return otherCandidate.getName().equals(getName())
                && otherCandidate.getGender().equals(getGender())
                && otherCandidate.getAge().equals(getAge())
                && otherCandidate.getPhone().equals(getPhone())
                && otherCandidate.getEmail().equals(getEmail())
                && otherCandidate.getAddress().equals(getAddress())
                && otherCandidate.getDesiredJob().equals(getDesiredJob())
                && otherCandidate.getEducation().equals(getEducation())
                && otherCandidate.getExpectedSalary().equals(getExpectedSalary())
                && otherCandidate.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, gender, age, phone, email, address, desiredJob, education, expectedSalary, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Gender: ")
                .append(getGender())
                .append(" Age: ")
                .append(getAge())
                .append(" Phone: ")
                .append(getPhone())
                .append(" Email: ")
                .append(getEmail())
                .append(" Address: ")
                .append(getAddress())
                .append(" Job: ")
                .append(getDesiredJob())
                .append(" Education: ")
                .append(getEducation())
                .append(" Salary: ")
                .append(getExpectedSalary())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
