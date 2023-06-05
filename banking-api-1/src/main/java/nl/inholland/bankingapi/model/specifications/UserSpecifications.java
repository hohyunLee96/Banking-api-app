package nl.inholland.bankingapi.model.specifications;

import nl.inholland.bankingapi.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecifications {
    private UserSpecifications() {
    }

    //User specifications
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }

    public static Specification<User> hasHasAccount(boolean hasAccount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hasAccount"), hasAccount);
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<User> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("address"), address);
    }

    public static Specification<User> hasCity(String city) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<User> hasPostalCode(String postalCode) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("postalCode"), postalCode);
    }

    public static Specification<User> hasbirthDate(String birthDate){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("birthDate"), birthDate);
    }

    public static Specification<User> hasPhoneNumber(String phoneNumber){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<User> hasUserType(String userType){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userType"), userType);
    }

    public static Specification<User> getSpecifications(String firstName, String lastName, boolean hasAccount, String email, String address, String city, String postalCode, String birthDate, String phoneNumber, String userType) {
        Specification<User> spec = null;
        Specification<User> temp = null;
        if (firstName != null) {
            temp = hasFirstName(firstName);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (lastName != null) {
            temp = hasLastName(lastName);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (hasAccount) {
            temp = hasHasAccount(hasAccount);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (email != null) {
            temp = hasEmail(email);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (address != null) {
            temp = hasAddress(address);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (city != null) {
            temp = hasCity(city);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (postalCode != null) {
            temp = hasPostalCode(postalCode);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (birthDate != null) {
            temp = hasbirthDate(birthDate);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (phoneNumber != null) {
            temp = hasPhoneNumber(phoneNumber);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (userType != null) {
            temp = hasUserType(userType);
            spec = spec == null ? temp : spec.and(temp);
        }
        return spec;
    }
}