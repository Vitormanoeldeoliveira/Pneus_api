package com.mosca.pneus.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final class Campos {

    private final List<String> faltas = new ArrayList<>();

    void texto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            faltas.add(mensagem);
        }
    }

    void presente(Object valor, String mensagem) {
        if (valor == null) {
            faltas.add(mensagem);
        }
    }

    void entre(Integer valor, int minimo, int maximo, String ausente, String fora) {
        if (valor == null) {
            faltas.add(ausente);
        } else if (valor < minimo || valor > maximo) {
            faltas.add(fora);
        }
    }

    void lancar(Function<String, ? extends ValidacaoException> excecao) {
        if (!faltas.isEmpty()) {
            throw excecao.apply(String.join(". ", faltas));
        }
    }
}
