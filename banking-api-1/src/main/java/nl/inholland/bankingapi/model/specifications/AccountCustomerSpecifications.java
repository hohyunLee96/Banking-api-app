package nl.inholland.bankingapi.model.specifications;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestParam;

public class AccountCustomerSpecifications {
    private AccountCustomerSpecifications() {
    }
    public static Specification<Account> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }
    public static Specification<Account> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }


    public static Specification<Account>getSpecificationsForCustomer(String firstName, String lastName) {
        Specification<Account> spec = null;
        Specification<Account> temp=null;
        if (firstName != null) {
            temp=hasFirstName(firstName);
            spec= temp;
        }
        if (lastName != null) {
            temp=hasLastName(lastName);
            spec=spec==null?temp:spec.and(temp);
        }

        return spec;
    }
}
