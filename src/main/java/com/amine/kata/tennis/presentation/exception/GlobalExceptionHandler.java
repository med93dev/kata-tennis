package com.amine.kata.tennis.presentation.exception;

import com.amine.kata.tennis.domain.exception.GameAlreadyFinishedException;
import com.amine.kata.tennis.domain.exception.InvalidPlayerException;
import com.amine.kata.tennis.domain.exception.InvalidSequenceException;
import com.amine.kata.tennis.presentation.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Erreur de validation")
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "Erreur de validation",
                "Les données fournies ne sont pas valides",
                errors,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "400", description = "Argument invalide")
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Argument invalide",
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ApiResponse(responseCode = "409", description = "État invalide du jeu")
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "État invalide du jeu",
                ex.getMessage(),
                null,
                HttpStatus.CONFLICT.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPlayerException.class)
    @ApiResponse(responseCode = "400", description = "Joueur invalide")
    public ResponseEntity<ErrorResponse> handleInvalidPlayerException(
            InvalidPlayerException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Joueur invalide",
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSequenceException.class)
    @ApiResponse(responseCode = "400", description = "Séquence invalide")
    public ResponseEntity<ErrorResponse> handleInvalidSequenceException(
            InvalidSequenceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Séquence invalide",
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GameAlreadyFinishedException.class)
    @ApiResponse(responseCode = "409", description = "Jeu déjà terminé")
    public ResponseEntity<ErrorResponse> handleGameAlreadyFinishedException(
            GameAlreadyFinishedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Jeu déjà terminé",
                ex.getMessage(),
                null,
                HttpStatus.CONFLICT.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Erreur interne du serveur",
                "Une erreur inattendue s'est produite",
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false),
                Instant.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
