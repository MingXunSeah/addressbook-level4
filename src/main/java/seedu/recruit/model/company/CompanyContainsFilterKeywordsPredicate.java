package seedu.recruit.model.company;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import seedu.recruit.commons.util.StringUtil;

/**
 * Tests that a {@code Company}'s {@code Name} matches all of the keywords given.
 */
public class CompanyContainsFilterKeywordsPredicate implements Predicate<Company> {
    private final HashMap<String, List<String>> keywords;

    public CompanyContainsFilterKeywordsPredicate(HashMap<String, List<String>> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Company company) {
        return ((!keywords.containsKey("CompanyName")) || (keywords.get("CompanyName").stream().anyMatch(keyword ->
                StringUtil.containsWordIgnoreCase(company.getName().value, keyword))))
                && ((!keywords.containsKey("Phone")) || (keywords.get("Phone").stream().anyMatch(keyword ->
                StringUtil.containsWordIgnoreCase(company.getPhone().value, keyword))))
                && ((!keywords.containsKey("Email")) || (keywords.get("Email").stream().anyMatch(keyword ->
                StringUtil.containsWordIgnoreCase(company.getEmail().value, keyword))))
                && ((!keywords.containsKey("Address")) || (keywords.get("Address").stream().anyMatch(keyword ->
                StringUtil.containsWordIgnoreCase(company.getAddress().value, keyword))));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof seedu.recruit.model.company.CompanyContainsFilterKeywordsPredicate
                // instanceof handles nulls
                && keywords.equals(((seedu.recruit.model.company.CompanyContainsFilterKeywordsPredicate)
                other).keywords)); // state check
    }

}

