package seedu.recruit.model.candidate;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.recruit.model.candidate.exceptions.CandidateNotFoundException;
import seedu.recruit.model.candidate.exceptions.DuplicateCandidateException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * A candidate is considered unique by comparing using {@code Candidate#isSamePerson(Candidate)}. As such, adding and
 * updating of persons uses Candidate#isSamePerson(Candidate) for equality so as to ensure that the candidate being
 * added or updated is unique in terms of identity in the UniqueCandidateList. However, the removal of a candidate uses
 * Candidate#equals(Object) so as to ensure that the candidate with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Candidate#isSameCandidate(Candidate)
 */
public class UniqueCandidateList implements Iterable<Candidate> {

    private final ObservableList<Candidate> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent candidate as the given argument.
     */
    public boolean contains(Candidate toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameCandidate);
    }

    /**
     * Adds a candidate to the list.
     * The candidate must not already exist in the list.
     */
    public void add(Candidate toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateCandidateException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the candidate {@code target} in the list with {@code editedCandidate}.
     * {@code target} must exist in the list.
     * The candidate identity of {@code editedCandidate} must not be the same as another existing candidate in the list.
     */
    public void setCandidate(Candidate target, Candidate editedCandidate) {
        requireAllNonNull(target, editedCandidate);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new CandidateNotFoundException();
        }

        if (!target.isSameCandidate(editedCandidate) && contains(editedCandidate)) {
            throw new DuplicateCandidateException();
        }

        internalList.set(index, editedCandidate);
    }

    /**
     * Removes the equivalent candidate from the list.
     * The candidate must exist in the list.
     */
    public void remove(Candidate toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new CandidateNotFoundException();
        }
    }

    /**
     * Sorts candidates in CandidateBook by name
     */
    public void sortByName() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return ((o1.getName().toString()).toLowerCase()).compareTo((o2.getName().toString()).toLowerCase());
            }
        });
    }

    /**
     * Sorts candidates in CandidateBook by age
     */
    public void sortByAge() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return (Integer.parseInt(o1.getAge().toString()) - Integer.parseInt(o2.getAge().toString()));
            }
        });
    }

    /**
     * Sorts candidates in CandidateBook by email
     */
    public void sortByEmail() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return ((o1.getEmail().toString()).toLowerCase()).compareTo((o2.getEmail().toString()).toLowerCase());
            }
        });
    }

    /**
     * Sorts candidates in CandidateBook by job
     */
    public void sortByJob() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return ((o1.getDesiredJob().toString()).toLowerCase()).compareTo((o2.getDesiredJob().toString())
                        .toLowerCase());
            }
        });
    }

    /**
     * Sorts candidates in CandidateBook by education level
     */
    public void sortByEducation() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return (o1.getEducation().order - o2.getEducation().order);
            }
        });
    }

    /**
     * Sorts candidates in CandidateBook by salary
     */
    public void sortBySalary() {

        Collections.sort(internalList, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                return (Integer.parseInt(o1.getExpectedSalary().toString()) - Integer.parseInt(o2.getExpectedSalary()
                        .toString()));
            }
        });
    }

    /**
     * Sorts the candidates in the displayed list in reverse order
     */
    public void sortInReverse() {

        Collections.reverse(internalList);
    }

    public void setCandidates(UniqueCandidateList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code candidates}.
     * {@code candidates} must not contain duplicate candidates.
     */
    public void setCandidates(List<Candidate> candidates) {
        requireAllNonNull(candidates);
        if (!candidatesAreUnique(candidates)) {
            throw new DuplicateCandidateException();
        }

        internalList.setAll(candidates);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Candidate> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<Candidate> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueCandidateList // instanceof handles nulls
                        && internalList.equals(((UniqueCandidateList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code candidates} contains only unique candidates.
     */
    private boolean candidatesAreUnique(List<Candidate> candidates) {
        for (int i = 0; i < candidates.size() - 1; i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                if (candidates.get(i).isSameCandidate(candidates.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
