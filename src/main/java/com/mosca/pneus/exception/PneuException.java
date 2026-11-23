package com.mosca.pneus.exception;

import com.mosca.pneus.domain.Pneu;

public class PneuException extends ValidacaoException {

    public PneuException(String message) {
        super(message);
    }

    public static void validar(Pneu pneu) {
        Campos campos = new Campos();
        campos.texto(pneu.getCodigo(), "Falta o código");
        campos.texto(pneu.getMarca(), "Falta a marca");
        campos.texto(pneu.getModelo(), "Falta o modelo");
        campos.texto(pneu.getMedida(), "Falta a medida");
        campos.presente(pneu.getSulcoMm(), "Falta o sulco");
        campos.presente(pneu.getKm(), "Falta a quilometragem");
        campos.presente(pneu.getStatus(), "Falta o status");
        campos.lancar(PneuException::new);
    }
}
