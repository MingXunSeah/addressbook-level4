package seedu.recruit.model.company;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.recruit.model.company.exceptions.CompanyNotFoundException;
import seedu.recruit.model.company.exceptions.DuplicateCompanyException;

/**
 * A list of companies that enforces uniqueness between its elements and does not allow nulls.
 * A company is considered unique by comparing using {@code Company#isSameCompany(Company)}. As such, adding and
 * updating of companies uses Company#isSameCompany(Company) for equality so as to ensure that the company being
 * added or updated is unique in terms of identity in the UniqueCompanyList. However, the removal of a company uses
 * Company#equals(Company) so as to ensure that the company with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Company#isSameCompany(Company)
 */


public class UniqueCompanyList implements Iterable<Company> {

    private final ObservableList<Company> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent company as the given argument.
     */
    public boolean contains(Company toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameCompany);
    }

    /**
     * Adds a company to the list.
     * The company must not already exist in the list.
     */
    public void add(Company toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateCompanyException();
        }
        internalList.add(toAdd);
    }

    /**
     * Returns index of Company using @param companyName in UniqueCompanyList
     * Returns -1 if no such company with companyName in UniqueCompanyList
     * NOTE: CompanyName is enforced to be unique in CompanyBook
     * See {@code isSameCompany()}
     */
    public int getCompanyIndexFromName(CompanyName companyName) {
        for (int index = 0; index < internalList.size(); index++) {
            if (internalList.get(index).getName().equals(companyName)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns the company object with the given @param index
     */

    public Company getCompanyFromIndex(int index) {
        return internalList.get(index);
    }


    /**
     * Replaces the company {@code target} in the list with {@code editedCompany}.
     * {@code target} must exist in the list.
     * The company identity of {@code editedCompany} must not be the same as another existing company in the list.
     */
    public void setCompany(Company target, Company editedCompany) {
        requireAllNonNull(target, editedCompany);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new CompanyNotFoundException();
        }

        if (!target.isSameCompany(editedCompany) && contains(editedCompany)) {
            throw new DuplicateCompanyException();
        }

        internalList.set(index, editedCompany);
    }

    /**
     * Removes the equivalent company from the list.
     * The company must exist in the list.
     */
    public void remove(Company toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new CompanyNotFoundException();
        }
    }

    /**
     * Sorts the companies by name
     */
    public void sortByCompanyName() {

        Collections.sort(internalList, new Comparator<Company>() {
            @Override
            public int compare(Company o1, Company o2) {
                return ((o1.getName().toString()).toLowerCase()).compareTo((
                        o2.getName().toString()).toLowerCase());
            }
        });
    }

    /**
     * Sorts the companies by email
     */
    public void sortByEmail() {

        Collections.sort(internalList, new Comparator<Company>() {
            @Override
            public int compare(Company o1, Company o2) {
                return ((o1.getEmail().toString()).toLowerCase()).compareTo((o2.getEmail().toString()).toLowerCase());
            }
        });
    }

    /**
     * Sorts the companies in reverse
     */
    public void sortInReverse() {
        Collections.reverse(internalList);
    }

    public void setCompanyList (UniqueCompanyList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code companyList}.
     * {@code companyList} must not contain duplicate companies.
     */
    public void setCompanyList(List<Company> companyList) {
        requireAllNonNull(companyList);
        if (!companiesAreUnique(companyList)) {
            throw new DuplicateCompanyException();
        }
        internalList.setAll(companyList);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Company> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<Company> iterator() {
        return internalList.iterator();
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueCompanyList // instanceof handles nulls
                && internalList.equals(((UniqueCompanyList) other).internalList));
    }

    /**
     * Returns true if {@code companyList} contains only unique companies.
     */
    private boolean companiesAreUnique(List<Company> companyList) {
        for (int i = 0; i < companyList.size() - 1; i++) {
            for (int j = i + 1; j < companyList.size(); j++) {
                if (companyList.get(i).isSameCompany(companyList.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
