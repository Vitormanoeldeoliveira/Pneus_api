package com.mosca.pneus.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public final class Respostas {

    private Respostas() {
    }

    public static ResponseEntity<Map<String, String>> erro(String mensagem) {
        String texto = mensagem.toLowerCase();
        int status = texto.contains("não encontrado") || texto.contains("não há") ? 404 : 400;
        return ResponseEntity.status(status).body(Map.of("message", mensagem));
    }
}
