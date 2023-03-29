package com.FullStack.WalletBanking.customException;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

//    @ExceptionHandler(TransactionBadRequest.class)
//    public ResponseEntity<Object> handleTransactionBadRequest(TransactionBadRequest ex) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("error", ex.getMessage());
//        response.put("status", HttpStatus.BAD_REQUEST);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleException(Exception ex) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("error", ex.getMessage());
//        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
@ExceptionHandler(TransactionBadRequest.class)
public ResponseEntity<String> handleTransactionBadRequest(TransactionBadRequest ex) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
}
}