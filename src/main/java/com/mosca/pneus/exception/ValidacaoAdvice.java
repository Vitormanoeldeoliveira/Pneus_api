package com.mosca.pneus.exception;

import com.mosca.pneus.controller.Respostas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ValidacaoAdvice {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> tratar(ValidacaoException ex) {
        return Respostas.erro(ex.getMessage());
    }
}
