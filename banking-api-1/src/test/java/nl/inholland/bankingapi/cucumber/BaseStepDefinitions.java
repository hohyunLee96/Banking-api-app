package nl.inholland.bankingapi.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class BaseStepDefinitions {

    public static final String VALID_CUSTOMER = "customer@email.com";
    public static final String VALID_EMPLOYEE = "employee";
    public static final String VALID_PASSWORD = "1234";
    public static final String INVALID_USERNAME = "bla";
    public static final String INVALID_PASSWORD = "invalid";
    public static final String EMPLOYEE_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBlbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9FTVBMT1lFRSIsImlhdCI6MTY4NjM0NzMwNiwiZXhwIjoyMDQ2MzQ3MzA2fQ.VZRffabfmZL4bUYdKSTneFwzfV0-WAG4_CJd7YN4tg7nwQlv8NpnladJexdZgQg63PVBP4skulRb3y6WX5e1Fum4T_Np5VFwLdiM_DAueVPLhR2I6pQuPUgjcWhUk5uvX38EssPC6iCEvxeOSBs8YvXZdgXNiSPBdBg-OOnswjImsaJnfiLmcqiEEZResMHSGW7TU9od7F3DHnXetLnG19Rgo9ruEb8QC-ZexfSDIrSu_iMRExWKw-L8r2otry0q-7zWgXqMmOabn-Kr-h-rtpw008oe5K-kb0DAUi7w8cBkJL622Za4SqzA24V1HTtd8tr_sagcwtL49-_ILSFfeQ";
   public static final String INVALID_TOKEN = "invalid_token";
    public static final String CUSTOMER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBlbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9DVVNUT01FUiIsImlhdCI6MTY4NjM0NzI0OSwiZXhwIjoyMDQ2MzQ3MjQ5fQ.C5ykh3MMK6jB-uNrVbXlVMb4spoJm9IHj3Is9MA-GtF9TzY_SIX0PU6qw3i1a-nfFDAWyee5S2vGhtCEODmtd1g6YY11LS4elAGHjy6rR1dFFnHBhWZEXIttuVzc8lwGHYYg-I7s4vuMR7InS0PDASMVbjXk3A69Vgu00uQPVmLd6fFQ5_hWFWVmHgikVQEGzjP56PofuXswYzhXoWATHGC3hymc8ki_VDxxomSXSihpVdR1jmp8eVnt556iW7VcXED0f33icDD9SWqaAkIqf5Ki5W9DScL79UXRxmWRh1l5EenkaKJx3zP-4F5Iz0Em5gtVr9RRtVqS_1c5oDmt1w";
    public final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;


    public void setHttpHeaders(String token) {
        httpHeaders.clear();
        httpHeaders.add("Authorization",  "Bearer " + token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

}
