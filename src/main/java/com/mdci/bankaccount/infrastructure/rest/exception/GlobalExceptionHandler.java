package com.mdci.bankaccount.infrastructure.rest.exception;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import com.mdci.bankaccount.domain.exception.InvalidAmountException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(AccountNotFoundException ex, HttpServletRequest request) {
        return buildResponse("AccountNotFound", ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleBalanceError(InsufficientBalanceException ex, HttpServletRequest request) {
        return buildResponse("InsufficientBalance", ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAmount(InvalidAmountException ex, HttpServletRequest request) {
        return buildResponse("InvalidAmount", ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<ErreurValidationDTO>>> handleValidation(MethodArgumentNotValidException ex) {
        List<ErreurValidationDTO> erreurs = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ErreurValidationDTO(err.getField(), err.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("erreurs", erreurs));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String paramName = ex.getName();
        String message;

        if ("date".equals(paramName) && ex.getRequiredType() == LocalDate.class) {
            message = "La date fournie est invalide. Format attendu : yyyy-MM-dd (LocalDate).";
        } else {
            message = "La valeur fournie est invalide. Type attendu : " + ex.getRequiredType().getSimpleName();
        }
        return buildResponse("TypeMismatch", message, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return buildResponse("UnexpectedError", "Erreur inattendue : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(String error, String message, HttpStatus status, String path) {
        ApiErrorResponse response = new ApiErrorResponse(
                error,
                message,
                path,
                status.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}
