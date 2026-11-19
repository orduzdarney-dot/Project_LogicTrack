package com.example.logitrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {


    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException excepcion, WebRequest solicitud) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso No Encontrado",
                excepcion.getMessage(),
                solicitud.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Maneja errores de negocio (400)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> manejarErrorDeNegocio(
            RuntimeException excepcion, WebRequest solicitud) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de Negocio",
                excepcion.getMessage(),
                solicitud.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Maneja errores de validación (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresDeValidacion(
            MethodArgumentNotValidException excepcion) {

        Map<String, String> errores = new HashMap<>();

        excepcion.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // 4. Maneja errores de JSON mal formado (400)
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarErrorDeFormatoJson(
            org.springframework.http.converter.HttpMessageNotReadableException excepcion,
            WebRequest solicitud) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "JSON Mal Formado",
                "El formato del JSON enviado es incorrecto. Verifica la sintaxis.",
                solicitud.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 5. Maneja cualquier otro error inesperado (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInterno(
            Exception excepcion, WebRequest solicitud) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ocurrió un error inesperado. Por favor, contacte al administrador.",
                solicitud.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}