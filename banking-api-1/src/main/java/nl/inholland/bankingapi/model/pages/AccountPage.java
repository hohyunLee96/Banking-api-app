package nl.inholland.bankingapi.model.pages;

import lombok.Data;

@Data
public class AccountPage {
    private int pageNumber=0;
    private int pageSize=10;
    private String sortBy="id";
    private String sortDirection="ASC";
}
