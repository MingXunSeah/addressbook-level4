package seedu.recruit.model.company;

import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.recruit.model.commons.Address;
import seedu.recruit.model.commons.Email;
import seedu.recruit.model.commons.Phone;


/**
 * Represents each Company in the CompanyBook
 */

public class Company {

    // Identity fields
    private final CompanyName name;

    // Data fields
    private final Address address;
    private final Email email;
    private final Phone phone;

    /**
     * Every field must be present and not null.
     */

    public Company (CompanyName name, Address address, Email email, Phone phone) {
        requireAllNonNull(name, address, email, phone);
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }


    public CompanyName getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Email getEmail() {
        return email;
    }

    public Phone getPhone() {
        return phone;
    }



    /**
     * Returns true if both companies is of the same name
     * This defines a weaker notion of equality between two companies.
     */
    public boolean isSameCompany(Company otherCompany) {
        if (otherCompany == this) {
            return true;
        }

        return otherCompany != null
                && otherCompany.getName().equals(getName());
    }

    /**
     * Returns true if both companies have the same identity and data fields.
     * This defines a stronger notion of equality between two companies.
     */


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Company)) {
            return false;
        }

        Company otherCompany = (Company) other;
        return otherCompany.getName().equals(getName())
                && otherCompany.getPhone().equals(getPhone())
                && otherCompany.getEmail().equals(getEmail())
                && otherCompany.getAddress().equals(getAddress());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, address, email, phone);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Company Name: ")
                .append(getName())
                .append(" Address: ")
                .append(getAddress())
                .append(" Email: ")
                .append(getEmail())
                .append(" Phone: ")
                .append(getPhone());

        return builder.toString();
    }

}
