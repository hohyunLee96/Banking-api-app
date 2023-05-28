package nl.inholland.bankingapi.model;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TransactionSearchCriteria implements Serializable {
    private String toIban;
    private String fromIban;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Double amount;


}
