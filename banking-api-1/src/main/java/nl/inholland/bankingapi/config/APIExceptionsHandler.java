//package nl.inholland.bankingapi.config;
//
//import nl.inholland.bankingapi.model.APIExceptions.ApiExceptions;
//import com.lesson1.BikesAPI.model.APIExceptions.ResponseErrorMessage;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.server.ResponseStatusException;
//
//@ControllerAdvice
//public class APIExceptionsHandler {
//
//    @ExceptionHandler(ApiExceptions.class)
//    public ResponseEntity handleApiExceptions(ApiExceptions e) {
//        System.out.println("ApiExceptions: " + e.getReason());
//        return ResponseEntity.status(e.getStatusCode()).body(new ResponseErrorMessage(e.getReason()));
//    }
//
//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity handleResponseStatusException(ResponseStatusException e) {
//        System.out.println("ResponseStatusException: " + e.getMessage());
//        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
//    }
//}