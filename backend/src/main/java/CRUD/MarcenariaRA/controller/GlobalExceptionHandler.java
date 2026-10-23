
package CRUD.MarcenariaRA.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarStatusException(
            ResponseStatusException e) {

        String mensagem = e.getReason() != null
                ? e.getReason()
                : "Erro na requisição.";

        return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of("message", mensagem));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarRuntimeException(RuntimeException e) {

        return Map.of(
                "message",
                e.getMessage() != null
                        ? e.getMessage()
                        : "Erro na requisição."
        );
    }
}
