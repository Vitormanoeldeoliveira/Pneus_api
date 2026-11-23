package com.mosca.pneus.exception;

import com.mosca.pneus.domain.Equipamento;

public class EquipamentoException extends ValidacaoException {

    public EquipamentoException(String message) {
        super(message);
    }

    public static void validar(Equipamento equipamento) {
        Campos campos = new Campos();
        campos.texto(equipamento.getCodigo(), "Falta o código");
        campos.texto(equipamento.getNome(), "Falta o nome");
        campos.presente(equipamento.getTipo(), "Falta o tipo");
        campos.presente(equipamento.getStatus(), "Falta o status");
        campos.entre(
                equipamento.getQuantidadePosicoes(),
                1,
                24,
                "Falta a quantidade de posições",
                "A quantidade de posições precisa ser de 1 a 24"
        );
        campos.lancar(EquipamentoException::new);
    }
}
