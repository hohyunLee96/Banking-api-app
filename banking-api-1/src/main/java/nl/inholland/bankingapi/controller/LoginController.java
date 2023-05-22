//package nl.inholland.bankingapi.controller;
//
//import nl.inholland.bankingapi.model.dto.LoginRequest_DTO;
//import nl.inholland.bankingapi.model.dto.LoginResponse_DTO;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth/login")
//public class LoginController {
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest_DTO loginRequest) {
//        // Validate the login credentials and authenticate the user
//        if (!isValidCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
//            // If the credentials are invalid, return an unauthorized status (401)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        // If authentication is successful, generate and return a JWT
//        String jwt = generateJwt(loginRequest.getUsername());
//        LoginResponse_DTO responseDTO = new LoginResponse_DTO(jwt);
//
//        // Return a success status (200) along with the response body
//        return ResponseEntity.ok(responseDTO);
//    }
//
//    LoginRequest_DTO loginRequestDto = new LoginRequest_DTO("admin", "admin");
//    private boolean isValidCredentials(String username, String password) {
//        return username.equals(loginRequestDto.getUsername()) && password.equals(loginRequestDto.getPassword());
//    }
//
//    private String generateJwt(String username) {
//        // Your logic to generate a JWT for the given username
//        // ...
//    }
//}
//
//
//
